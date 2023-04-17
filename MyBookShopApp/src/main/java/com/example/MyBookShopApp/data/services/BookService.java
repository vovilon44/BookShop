package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.ApiResponse;
import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.data.BooksPageDto;
import com.example.MyBookShopApp.data.TransactionsDto;
import com.example.MyBookShopApp.data.repositories.BookRepository;
import com.example.MyBookShopApp.data.repositories.BookRepositoryJDBC;
import com.example.MyBookShopApp.data.repositories.FileDownloadRepository;
import com.example.MyBookShopApp.data.struct.book.file.FileDownloadEntity;
import com.example.MyBookShopApp.data.telegram.BotBooksResponse;
import com.example.MyBookShopApp.data.telegram.BotUser;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.security.BookstoreUser;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final ResourceStorage storage;
    private final FileDownloadRepository fileDownloadRepository;
    private final BookRepositoryJDBC bookRepositoryJDBC;
    private final BookstoreUserRegister userRegister;
    @Value("${google.books.api.key}")
    private String apiKey;


    @Autowired
    public BookService(BookRepository bookRepository, ResourceStorage storage, FileDownloadRepository fileDownloadRepository, BookRepositoryJDBC bookRepositoryJDBC, BookstoreUserRegister userRegister) {
        this.bookRepository = bookRepository;
        this.storage = storage;
        this.fileDownloadRepository = fileDownloadRepository;
        this.bookRepositoryJDBC = bookRepositoryJDBC;
        this.userRegister = userRegister;
    }


    //-------------------------------------------------------------------------YEA----------------------------------------------------------------
    public BooksPageDto getPageOfRecommendedBooksNotAuthorized(Integer offset, Integer limit, String cartContents, String keptContents, String historyVisit){
        List<Set<String>> slugs =  getSlugsBooksInKeptCartHistoryNotAuthorized(cartContents, keptContents, historyVisit);
        List<Book> books = bookRepositoryJDBC.getPageOfRecommendedBooksNotAuthorized(offset, limit, slugs);
        return new BooksPageDto(books, false, 0);
    }



    //-------------------------------------------------------------------------YEA----------------------------------------------------------------
    public BooksPageDto getPageOfRecommendedBooksAuthorized(Integer offset, Integer limit){

        List<Book> books = bookRepositoryJDBC.getPageOfRecommendedBooksAuthorized(userRegister.getCurrentUser().getId(), offset, limit);
        return getBooksPageDtoAuthorized(books, offset, limit, 0);
    }



    //-------------------------------------------------------------------------YEA----------------------------------------------------------------
    public BooksPageDto getListOfPopularBooksAuthorized(Integer offset, Integer limit)
    {
        Pageable nextPage = PageRequest.of(offset,limit);
        Page<Book> pageBooks = bookRepository.getListOfPopularBooksAuthorized(userRegister.getCurrentUser().getId(), nextPage);
        return getBooksPageDtoAuthorized(pageBooks.getContent(), offset, limit, (int) pageBooks.getTotalElements());

    }
    //-------------------------------------------------------------------------YEA----------------------------------------------------------------
    public BooksPageDto getListOfPopularBooksNotAuthorized(Integer offset, Integer limit, String cartContents, String keptContents) {
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<Book> pageBooks = bookRepository.getListOfPopularBooksNotAuthorized(getSlugsBooksInKeptCartHistoryNotAuthorized(cartContents, keptContents, null).get(1), nextPage);
        return getBooksPageDtoNotAuthorized(pageBooks.getContent(), offset, limit, (int) pageBooks.getTotalElements(), cartContents, keptContents);
    }


    //-------------------------------------------------------------------------YEA----------------------------------------------------------------
    public BooksPageDto getListOfRecentBooksAuthorized(Integer offset, Integer limit, String from, String to) throws ParseException {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        Pageable nextPage = PageRequest.of(offset,limit);
        Page books;
        if (from != null && to != null){
            books = bookRepository.findBooksByPubDateBetweenOrderByPubDateDescCustom(df.parse(from), df.parse(to), userRegister.getCurrentUser().getId(), nextPage);
        } else if (from != null){
            books =  bookRepository.findBooksByPubDateBeforeOrderByPubDateDescCustom(df.parse(to), userRegister.getCurrentUser().getId(), nextPage);
        } else if (to != null){
            books =  bookRepository.findBooksByPubDateAfterOrderByPubDateDescCustom(df.parse(from), userRegister.getCurrentUser().getId(), nextPage);
        } else {
            books =  bookRepository.findByOrderByPubDateDescCustom(userRegister.getCurrentUser().getId(), nextPage);
        }
        return getBooksPageDtoAuthorized(books.getContent(), offset, limit, (int) books.getTotalElements());
    }
    //-------------------------------------------------------------------------YEA----------------------------------------------------------------
    public BooksPageDto getListOfRecentBooksNotAuthorized(Integer offset, Integer limit, String from, String to, String cartContents, String keptContents) throws ParseException {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        Pageable nextPage = PageRequest.of(offset,limit);
        Set<String> slugs = getSlugsBooksInKeptCartHistoryNotAuthorized(cartContents, keptContents, null).get(1);
        Page books;
        if (from != null && to != null){
            books = bookRepository.findBooksByPubDateBetweenAndSlugIsNotInOrderByPubDateDesc(df.parse(from), df.parse(to), slugs, nextPage);
        } else if (from != null){
            books =  bookRepository.findBooksByPubDateAfterAndSlugIsNotInOrderByPubDateDesc(df.parse(from), slugs, nextPage);
        } else if (to != null){
            books =  bookRepository.findBooksByPubDateBeforeAndSlugIsNotInOrderByPubDateDesc(df.parse(to), slugs, nextPage);
        } else {
                books = bookRepository.findBooksBySlugNotInOrderByPubDateDesc(slugs, nextPage);
        }

        return getBooksPageDtoNotAuthorized(books.getContent(), offset, limit, (int) books.getTotalElements(), cartContents, keptContents);
    }




    //-------------------------------------------------------------------------YEA----------------------------------------------------------------
    public BooksPageDto getBookFromGenreAuthorized(String slugGenre, Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset,limit);
        Page bookPage = bookRepository.findBooksByBook2GenreEntityList_Genre_SlugIsCustom(slugGenre, userRegister.getCurrentUser().getId(), nextPage);
        return getBooksPageDtoAuthorized(bookPage.getContent(), offset, limit, (int) bookPage.getTotalElements());
    }
    //-------------------------------------------------------------------------YEA----------------------------------------------------------------
    public BooksPageDto getBookFromGenreNotAuthorized(String slugGenre, Integer offset, Integer limit, String cartContents, String keptContents){
        Pageable nextPage = PageRequest.of(offset,limit);
        Page bookPage = bookRepository.findDistinctByBook2GenreEntityList_Genre_SlugIsAndSlugNotInOrderByPubDate(slugGenre, getSlugsBooksInKeptCartHistoryNotAuthorized(cartContents, keptContents, null).get(1), nextPage);
        return getBooksPageDtoNotAuthorized(bookPage.getContent(), offset, limit, (int) bookPage.getTotalElements(), cartContents, keptContents);
    }
    //-------------------------------------------------------------------------YEA----------------------------------------------------------------
    public BooksPageDto getBooksFromAuthorAuthorized(String slugAuthor, Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset,limit);
        Page<Book> bookPage =  bookRepository.findAllByBook2AuthorEntityList_Author_SlugIs(slugAuthor, nextPage);
        return getBooksPageDtoAuthorized(bookPage.getContent(), offset, limit, (int) bookPage.getTotalElements());
    }
    //-------------------------------------------------------------------------YEA----------------------------------------------------------------
    public BooksPageDto getBooksFromAuthorNotAuthorized(String slugAuthor, Integer offset, Integer limit, String cartContents, String keptContents){
        Pageable nextPage = PageRequest.of(offset,limit);
        Page<Book> bookPage =  bookRepository.findAllByBook2AuthorEntityList_Author_SlugIs(slugAuthor, nextPage);
        return getBooksPageDtoNotAuthorized(bookPage.getContent(), offset, limit, (int) bookPage.getTotalElements(), cartContents, keptContents);
    }


    //-------------------------------------------------------------------------YEA----------------------------------------------------------------
    public Book getBookFromSlug(String slugBook) throws BookstoreApiWrongParameterException {

        if (slugBook.equals("") || slugBook.length() <= 1 ){
            throw new BookstoreApiWrongParameterException("Wrong values passed to one or more parameters");
        } else {
            Book book =  bookRepository.findBookBySlug(slugBook);
            if (book != null){
                return book;
            } else {
                throw new BookstoreApiWrongParameterException("No data found with specified parameters...");
            }
        }
    }
    //-------------------------------------------------------------------------YEA----------------------------------------------------------------
    public BooksPageDto getBookFromTagNotAuthorized(String slugTag, Integer offset, Integer limit, String cartContents, String keptContents) throws BookstoreApiWrongParameterException {
        if (slugTag.equals("") || slugTag.length() <= 1 || offset == null || limit == null){
            throw new BookstoreApiWrongParameterException("Wrong values passed to one or more parameters");
        } else {
            Pageable nextPage = PageRequest.of(offset, limit);
            Page<Book> data =  bookRepository.findDistinctBooksByBook2TagEntityList_Tag_SlugIsAndSlugNotInOrderByPubDateDesc(slugTag, getSlugsBooksInKeptCartHistoryNotAuthorized(cartContents, keptContents, null).get(1), nextPage);
            if (data.getContent().size() > 0){

                return getBooksPageDtoNotAuthorized(data.getContent(), offset, limit, (int) data.getTotalElements(), cartContents, keptContents);
            } else {
                throw new BookstoreApiWrongParameterException("No data found with specified parameters...");
            }
        }
    }

    public BooksPageDto getBookFromTagAuthorized(String slugTag, Integer offset, Integer limit) throws BookstoreApiWrongParameterException {
        if (slugTag.equals("") || slugTag.length() <= 1 || offset == null || limit == null){
            throw new BookstoreApiWrongParameterException("Wrong values passed to one or more parameters");
        } else {
            Pageable nextPage = PageRequest.of(offset, limit);
            Page<Book> data =  bookRepository.findBooksByBook2TagEntityList_Tag_SlugIsCustom(slugTag, userRegister.getCurrentUser().getId(), nextPage);
            if (data.getContent().size() > 0){
                return getBooksPageDtoAuthorized(data.getContent(), offset, limit, (int) data.getTotalElements());
            } else {
                throw new BookstoreApiWrongParameterException("No data found with specified parameters...");
            }
        }
    }


    public BooksPageDto getBooksBySearchAuthorized(String searchText, Integer offset, Integer limit) throws BookstoreApiWrongParameterException {
        if (searchText.equals("") || searchText.length() <= 1 || offset == null || limit == null ){
            throw new BookstoreApiWrongParameterException("Wrong values passed to one or more parameters");
        } else {
            Pageable nextPage = PageRequest.of(offset,limit);
            Page<Book> data = bookRepository.findAllByContainsAuthorizedCustom(searchText, userRegister.getCurrentUser().getId(), nextPage);
            if (data.getContent().size() > 0){
                return getBooksPageDtoAuthorized(data.getContent(), offset, limit, (int) data.getTotalElements());
            } else {
                throw new BookstoreApiWrongParameterException("No data found with specified parameters...");
            }
        }
    }

    public BooksPageDto getBooksBySearchNotAuthorized(String searchText, Integer offset, Integer limit, String cartContents, String keptContents) throws BookstoreApiWrongParameterException {
        if (searchText.equals("") || searchText.length() <= 1 || offset == null || limit == null ){
            throw new BookstoreApiWrongParameterException("Wrong values passed to one or more parameters");
        } else {
            Pageable nextPage = PageRequest.of(offset,limit);
            Page<Book> data = bookRepository.findAllByContainsNotAuthorizedCustom(searchText, nextPage, getSlugsBooksInKeptCartHistoryNotAuthorized(cartContents, keptContents, null).get(1));
            if (data.getContent().size() > 0){
                return getBooksPageDtoNotAuthorized(data.getContent(), offset, limit, (int) data.getTotalElements(), cartContents, keptContents);
            } else {
                throw new BookstoreApiWrongParameterException("No data found with specified parameters...");
            }
        }
    }
    //-------------------------------------------------------------------------YEA----------------------------------------------------------------
    public void saveFile(MultipartFile file, String slug) throws IOException, BookstoreApiWrongParameterException {
        String savePath = storage.saveNewBookImage(file, slug);
        Book bookToUpdate = getBookFromSlug(slug);
        bookToUpdate.setImage(savePath);
        bookRepository.save(bookToUpdate);
    }


//    public List<Book> getBooksBySearch()
//    {
//        return bookRepository.findAllByIdBetween(0, 10);
//    }

    //-------------------------------------------------------------------------YEA----------------------------------------------------------------
    public byte[] getBookFile(String hash) throws IOException {
        Path path = storage.getBookFilePath(hash);
        MediaType mediaType = storage.getBookFileMime(hash);
        byte[] data = storage.getBookFileByteArray(hash);
        return data;
    }

    //-------------------------------------------------------------------------YEA----------------------------------------------------------------
    public Path getPathFile(String hash) {
        return storage.getBookFilePath(hash);
    }

    //-------------------------------------------------------------------------YEA----------------------------------------------------------------
    public MediaType getBookFileMime(String hash) {
        return storage.getBookFileMime(hash);
    }


    //-------------------------------------------------------------------------YEA----------------------------------------------------------------
    public ApiResponse<BooksPageDto> getResponseBooks(BooksPageDto booksPageDto){
        ApiResponse<BooksPageDto> response = new ApiResponse<>();
        response.setDebugMessage("successful request");
        response.setMessage("data size: " + booksPageDto.getCount() + " elements");
        response.setStatus(HttpStatus.OK);
        response.setTimeStamp(LocalDateTime.now());
        response.setData(booksPageDto);
        return response;
    }

    public ApiResponse<TransactionsDto> getResponseTransactions(TransactionsDto transactionsDto){
        ApiResponse<TransactionsDto> response = new ApiResponse<>();
        response.setDebugMessage("successful request");
        response.setMessage("data size: " + transactionsDto.getCount() + " elements");
        response.setStatus(HttpStatus.OK);
        response.setTimeStamp(LocalDateTime.now());
        response.setData(transactionsDto);
        return response;
    }
    //-------------------------------------------------------------------------YEA----------------------------------------------------------------
    public List<Book> getBooksInSlugs(List<String> slugs){
        if (slugs != null && slugs.size() > 0) {
            return bookRepository.findBooksBySlugIn(slugs);
        } else {
            return new ArrayList<>();
        }
    }
    //-------------------------------------------------------------------------YEA----------------------------------------------------------------
    public List<Book> getBooksInCart(Integer type){
        Pageable page = Pageable.unpaged();
        return bookRepository.findBooksByBook2UserEntityList_TypeIdAndBook2UserEntityList_User_Email(type, userRegister.getCurrentUser().getEmail(), page).getContent();
    }



    //-------------------------------------------------------------------------YEA----------------------------------------------------------------
//    public BooksPageDto getPageOfGoogleBooksApiSearchResult(String searchWord,Integer offset, Integer limit)
//    {
//        RestTemplate restTemplate = new RestTemplate();
//        String REQUEST_URL = "https://www.googleapis.com/books/v1/volumes" +
//                "?q=" + searchWord +
//                "&key =" + apiKey +
//                "&filter=paid-ebooks" +
//                "&startIndex=" + offset * limit +
//                "&maxResults=" + limit;
//        Root root = restTemplate.getForEntity(REQUEST_URL, Root.class).getBody();
//        ArrayList<Book> list = new ArrayList<>();
//        if (root != null){
//            for (Item item : root.getItems()){
//                Book book = new Book();
//                if (item.getVolumeInfo() != null && item.getSaleInfo().getSaleability().equals("FOR_SALE")){
//                    if (item.getVolumeInfo().getAuthors() != null) {
//                        book.setBook2AuthorEntityList(item.getVolumeInfo().getAuthors().stream().map(e -> {
//                            Book2AuthorEntity book2AuthorEntity = new Book2AuthorEntity();
//                            Author author = new Author();
//                            author.setName(e);
//                            book2AuthorEntity.setBook(book);
//                            book2AuthorEntity.setAuthor(author);
//                            return book2AuthorEntity;
//                        }).collect(Collectors.toList()));
//                    }
//                    book.setTitle(item.getVolumeInfo().getTitle());
//                    book.setImage(item.getVolumeInfo().getImageLinks().getThumbnail());
//
//                }
//                if (item.getSaleInfo() != null && item.getSaleInfo().getSaleability().equals("FOR_SALE"))
//                {
//                    book.setPrice(item.getSaleInfo().getListPrice().getAmount());
//                    book.setDiscount(1 - (item.getSaleInfo().getRetailPrice().getAmount() / item.getSaleInfo().getListPrice().getAmount()));
//                }
//                if (item.getSaleInfo().getSaleability().equals("FOR_SALE")) {
//                    list.add(book);
//                }
//            }
//
//        }
//        return getBooksPageDtoNotAuthorized(list, offset, limit, list.size(), null, null);
//    }
    //-------------------------------------------------------------------------YEA----------------------------------------------------------------
    public boolean checkBookPresence(String slug)
    {
        if (bookRepository.findBookBySlug(slug) != null){
            return true;
        } else {
            return false;
        }
    }
    //-------------------------------------------------------------------------YEA----------------------------------------------------------------
    public String getCookieForRatingBook(String booksRating, String slug, Integer value)
    {
        if (booksRating == null || booksRating.equals("")) {
            return slug + "=" + value;
        } else if (booksRating.contains(slug)){
            StringJoiner stringJoiner = new StringJoiner("/");
            for (String rating : booksRating.split("/")) {
                if (rating.contains(slug)) {
                    stringJoiner.add(slug + "=" + value);
                } else {
                    stringJoiner.add(rating);
                }
            }
            return stringJoiner.toString();
        } else {
            StringJoiner stringJoiner = new StringJoiner("/");
            stringJoiner.add(booksRating).add(slug + "=" + value);
            return stringJoiner.toString();
        }
    }
    //-------------------------------------------------------------------------YEA----------------------------------------------------------------


    //-----------------------------------------------------------------YEA---------------------------------------------------------------------------------------------
    public BooksPageDto getMyBooks(int offset, int limit) {
        Pageable nextPage = PageRequest.of(offset,limit);
        Page<Book> bookPage =  bookRepository.findBooksByBook2UserEntityList_TypeIdInAndBook2UserEntityList_User_Email(List.of(3), userRegister.getCurrentUser().getEmail(), nextPage);
        return getBooksPageDtoAuthorized(bookPage.getContent(), offset, limit, (int) bookPage.getTotalElements());
    }
    //-----------------------------------------------------------------YEA---------------------------------------------------------------------------------------------
    public BooksPageDto getMyBooksInArchive(int offset, int limit) {
        Pageable nextPage = PageRequest.of(offset,limit);
        Page<Book> bookPage = bookRepository.findBooksByBook2UserEntityList_TypeIdAndBook2UserEntityList_User_Email(4, userRegister.getCurrentUser().getEmail(), nextPage);
        return getBooksPageDtoAuthorized(bookPage.getContent(), offset, limit, (int) bookPage.getTotalElements());
    }
    //-----------------------------------------------------------------YEA---------------------------------------------------------------------------------------------
    public BooksPageDto getMyHistoryBooks(int offset, int limit) {
        Pageable nextPage = PageRequest.of(offset,limit);
        Page<Book> bookPage = bookRepository.findBooksByBook2UserHistoryList_User_EmailOrderByBook2UserHistoryList_Time(userRegister.getCurrentUser().getEmail(), nextPage);
        return getBooksPageDtoAuthorized(bookPage.getContent(), offset, limit, (int) bookPage.getTotalElements());
    }
    //-----------------------------------------------------------------YEA---------------------------------------------------------------------------------------------
    private BooksPageDto getBooksPageDtoNotAuthorized(List<Book> books, Integer offset, Integer limit, Integer count, String cartContents, String keptContents){
        books.forEach(e -> {
            if (cartContents != null && cartContents.contains(e.getSlug())) {
                e.setStatus("CART");
            } else if (keptContents != null && keptContents.contains(e.getSlug())) {
                e.setStatus("KEPT");
            } else {
                e.setStatus("");
            }
        });
        return new BooksPageDto(books, offset * limit + books.size()  >= count, count);
    }
    //-----------------------------------------------------------------YEA---------------------------------------------------------------------------------------------
    private BooksPageDto getBooksPageDtoAuthorized(List<Book> books, Integer offset, Integer limit, Integer count){
        books.forEach(e->e.setUser(userRegister.getCurrentUser()));
        return new BooksPageDto(books, offset * limit + books.size()  >= count, count);
    }
    //-----------------------------------------------------------------YEA---------------------------------------------------------------------------------------------
    private List<Set<String>> getSlugsBooksInKeptCartHistoryNotAuthorized(String cartContents, String keptContents, String historyVisit) {
        List<Set<String>> slugs = new ArrayList<>();
        slugs.add(new HashSet<>());
        slugs.add(new HashSet<>());
        if (cartContents != null && !cartContents.equals("")){
            Collections.addAll(slugs.get(0), cartContents.split("/"));
            Collections.addAll(slugs.get(1), cartContents.split("/"));
        }
        if (keptContents != null && !keptContents.equals("")) {
            Collections.addAll(slugs.get(0), keptContents.split("/"));
            Collections.addAll(slugs.get(1), keptContents.split("/"));
        }
        if (historyVisit != null && !historyVisit.equals("")) {
            Collections.addAll(slugs.get(0), historyVisit.split("/"));
        }
        if (slugs.get(0).size() == 0){
            slugs.get(0).add("");
        }
        if (slugs.get(1).size() == 0){
            slugs.get(1).add("");
        }
        return slugs;
    }

    public Long getBookFileSize(String path) {
        return storage.getBookFileSize(path);
    }


    public void incDownloadCount(String hash) {
        Book book = bookRepository.findBookByBookFileList_HashIs(hash);
        FileDownloadEntity fileDownload = fileDownloadRepository.findByBookAndUser(book, userRegister.getCurrentUser());
        if (fileDownload != null){
            fileDownload.setCount(fileDownload.getCount() + 1);
            fileDownloadRepository.save(fileDownload);
        } else {
            fileDownloadRepository.save(new FileDownloadEntity(userRegister.getCurrentUser(), book));
        }
    }

    public void botIncDownloadCount(String hash, BookstoreUser user) {
        Book book = bookRepository.findBookByBookFileList_HashIs(hash);
        FileDownloadEntity fileDownload = fileDownloadRepository.findByBookAndUser(book, user);
        if (fileDownload != null){
            fileDownload.setCount(fileDownload.getCount() + 1);
            fileDownloadRepository.save(fileDownload);
        } else {
            fileDownloadRepository.save(new FileDownloadEntity(user, book));
        }
    }


    public Boolean checkDownloadExceedance(String slugBook) {
        FileDownloadEntity fileDownload = fileDownloadRepository.findByBook_SlugAndUser_Id(slugBook, userRegister.getCurrentUser().getId());
        return fileDownload == null || fileDownload.getCount() < 5;
    }

    public Boolean checkBotkDownloadExceedance(Integer userId, Integer bookId) {
        FileDownloadEntity fileDownload = fileDownloadRepository.findByBook_IdAndUser_Id(bookId, userId);
        return fileDownload == null || fileDownload.getCount() < 5;
    }


    public BotBooksResponse getBotSearchBooksByGenre(BotUser botUser, Integer limit){
        return bookRepositoryJDBC.getBotSearchBooksByGenre(botUser, limit);
    }

    public BotBooksResponse getBotSearchBooksByAuthors(BotUser botUser, Integer limit){
        return bookRepositoryJDBC.getBotSearchBooksByAuthors(botUser, limit);
    }

    public BotBooksResponse getBotSearchBooksByTitle(BotUser botUser, Integer limit){
        return bookRepositoryJDBC.getBotSearchBooksByTitle(botUser, limit);
    }
    public BotBooksResponse getBotSearchBooksByTag(BotUser botUser, Integer limit){
        return bookRepositoryJDBC.getBotSearchBooksByTag(botUser, limit);
    }

    public BotBooksResponse getBotSearchBooksByAll(BotUser botUser, Integer limit){
        return bookRepositoryJDBC.getBotSearchBooksByAll(botUser, limit);
    }

    public BotBooksResponse getBotBooksMain(BotUser botUser, Integer limit, Integer type) {
        return bookRepositoryJDBC.getBotBooksMain(botUser, limit, type);
    }


    public Book getBookById(Integer bookId){
        return bookRepository.findBookById(bookId);
    }

    public BotBooksResponse getBotBookById(Integer bookId){
        return bookRepositoryJDBC.getBotBookById(bookId);
    }

    public HashMap<String,String> parseBotHash(String hash) {
        HashMap<String,String> slugAndBook = new HashMap<>();
        List<Integer>  numbers = new ArrayList<>();
        int index = 1;
        long time = 0;
        for (int i = 0; i < 4; i++) {
            int lengNumber = Integer.parseInt(hash.substring(index, index + 1));
            int valueNumber = Integer.parseInt(hash.substring(index + 1, index + lengNumber + 1));
            index += lengNumber + 1;
            numbers.add(valueNumber);
            if (i == 3){
                time = Long.parseLong(hash.substring(index, hash.length()));
            }
        }
        Integer userId = numbers.get(1) - numbers.get(0);
        int bookId = numbers.get(3) - numbers.get(2);
        slugAndBook.put("slug", getBookById(bookId).getSlug());
        slugAndBook.put("userId", userId.toString());
        if ((System.currentTimeMillis() - time) / 1000 /60 < 5 && !checkBotkDownloadExceedance(userId, bookId)){
            return slugAndBook;
        } else {
            return null;
        }
    }
}
