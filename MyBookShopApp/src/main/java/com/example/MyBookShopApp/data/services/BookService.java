package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.ApiResponse;
import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.data.BooksPageDto;
import com.example.MyBookShopApp.data.repositories.Book2AuthorRepository;
import com.example.MyBookShopApp.data.repositories.Book2UserRepository;
import com.example.MyBookShopApp.data.repositories.BookLike2UserRepository;
import com.example.MyBookShopApp.data.repositories.BookRepository;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final Book2AuthorRepository book2AuthorRepository;
    private final BookLike2UserRepository bookLike2UserRepository;
    private final Book2UserRepository book2UserRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ResourceStorage storage;
    private final BookstoreUserRegister userRegister;
//    private final JWTUtil jwtUtil;

    @Autowired
    public BookService(BookRepository bookRepository, NamedParameterJdbcTemplate jdbcTemplate, ResourceStorage storage, Book2AuthorRepository book2AuthorRepository, BookLike2UserRepository bookLike2UserRepository, Book2UserRepository book2UserRepository, BookstoreUserRegister userRegister) {
        this.bookRepository = bookRepository;
        this.bookLike2UserRepository = bookLike2UserRepository;
        this.book2AuthorRepository = book2AuthorRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.storage = storage;
        this.book2UserRepository = book2UserRepository;

        this.userRegister = userRegister;
    }


    public List<Book> getPageOfRecommendedBooks(Integer offset, Integer limit){
        if (offset != null && limit != null) {
            List<Book> books = bookRepository.findBooksByIdIn(jdbcTemplate.query("select book_like2user_entity.book_id, (sum(like_value) / count(*)) as range from book_like2user_entity group by book_id order by range desc, book_id offset " + offset * limit + " limit " + limit + " ;", (ResultSet rs, int rowNum) -> rs.getInt("book_id")));
            if (books == null){
            }
            return books;
        } else {
            return new ArrayList<>();
        }
    }

    @Transactional
    public List<Book> getPageOfRecommendedBooksAuthorized(Integer offset, Integer limit, String user){
        if (offset != null && limit != null) {
        TreeSet<Book> bookList = new TreeSet<>(Comparator.comparing(Book::getPubDate).reversed());
//        List<Book> mainBooks = bookRepository.findBooksByBook2UserEntityList_User_EmailIs(userRegister.getCurrentUser().getEmail());
            List<Book> mainBooks = bookRepository.findBooksByBook2UserEntityList_User_EmailIs(user);
        if (mainBooks == null){

            return new ArrayList<>();
        }

        mainBooks.forEach(e->{
            e.getBook2TagEntityList().forEach(x->{
                x.getTag().getBook2TagEntityList().forEach(r->bookList.add(r.getBook()));
            });
            e.getBook2AuthorEntityList().forEach(y->{
                y.getAuthor().getBook2AuthorEntityList().forEach(s->bookList.add(s.getBook()));
            });
            e.getBook2GenreEntityList().forEach(z->{
                z.getGenre().getBook2GenreEntityList().forEach(t->bookList.add(t.getBook()));
            });
        });
        mainBooks.forEach(e->bookList.remove(e));
        List<Book> books= new ArrayList<>(bookList);
        mainBooks.clear();
        if (books.size() > offset * limit + limit){
            for (int i = offset * limit; i < offset * limit + limit; i++) {
                mainBooks.add(books.get(i));
            }
        } else {
            for (int i = offset * limit; i < books.size(); i++){
                mainBooks.add(books.get(i));
            }
        }
        return mainBooks;
        } else {
            return new ArrayList<>();
        }
    }



    public List<Book> getListOfPopularBooks(Integer offset, Integer limit){
        if (offset != null && limit != null) {
            List<Book> books = jdbcTemplate.query("select *,(select count(*) from book2user where book2user.type_id=1 and book2user.book_id=books.id) * 0.4 + (select count(*) from book2user where book2user.type_id=2 and book2user.book_id=books.id) * 0.7 + (select count(*) from book2user where book2user.type_id=3 and book2user.book_id=books.id) as rating from books order by rating desc offset " + offset * limit + " limit " + limit + " ;", (ResultSet rs, int rowNum) -> {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setPubDate(rs.getDate("pub_date"));
                book.setIsBestseller(rs.getInt("is_bestseller"));
                book.setSlug(rs.getString("slug"));
                book.setTitle(rs.getString("title"));
                book.setImage(rs.getString("image"));
                book.setDescription(rs.getString("description"));
                book.setPrice(rs.getInt("price"));
                book.setDiscount(rs.getDouble("discount"));
                book.setBook2AuthorEntityList(book2AuthorRepository.findBook2AuthorEntityByBook_IdIs(rs.getInt("id")));
                book.setBookLike2UserEntityList(bookLike2UserRepository.findBookLike2UserEntitiesByBookIdIs(rs.getInt("id")));
                return book;
            });
            if (books == null){
                return new ArrayList<>();
            } else {
                return books;
            }
        } else {
            return new ArrayList<>();
        }
    }


    public List<Book> getListOfRecentBooks(Integer offset, Integer limit, String from, String to) throws ParseException {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        Pageable nextPage = PageRequest.of(offset,limit);
        return bookRepository.findBooksByPubDateBetweenOrderByPubDateDesc(df.parse(from), df.parse(to),nextPage).getContent();
    }



    public List<Book> getListOfRecentBooksWithoutDate(Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset,limit);
        return bookRepository.findBooksByOrderByPubDateDesc(nextPage).getContent();
    }

    public Page<Book> getBookFromGenre(String slugGenre, Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset,limit);
        return bookRepository.findBooksByBook2GenreEntityList_Genre_SlugIs(slugGenre, nextPage);
    }

    public Page<Book> getBooksFromAuthor(String slugAuthor, Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset,limit);
        return bookRepository.findAllByBook2AuthorEntityList_Author_SlugIs(slugAuthor, nextPage);
    }

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

    public Page<Book> getBookFromTag(String slugTag, Integer offset, Integer limit) throws BookstoreApiWrongParameterException {
        if (slugTag.equals("") || slugTag.length() <= 1 || offset == null || limit == null){
            throw new BookstoreApiWrongParameterException("Wrong values passed to one or more parameters");
        } else {
            Pageable nextPage = PageRequest.of(offset, limit);
            Page<Book> data =  bookRepository.findBooksByBook2TagEntityList_Tag_SlugIs(slugTag, nextPage);
            if (data.getContent().size() > 0){
                return data;
            } else {
                throw new BookstoreApiWrongParameterException("No data found with specified parameters...");
            }
        }
    }

    public Page<Book> getBooksBySearch(String searchText, Integer offset, Integer limit) throws BookstoreApiWrongParameterException {
        if (searchText.equals("") || searchText.length() <= 1 || offset == null || limit == null ){
            throw new BookstoreApiWrongParameterException("Wrong values passed to one or more parameters");
        } else {
            Pageable nextPage = PageRequest.of(offset,limit);
            Page<Book> data = bookRepository.findAllByTitleContains(searchText, nextPage);
            if (data.getContent().size() > 0){
                return data;
            } else {
                throw new BookstoreApiWrongParameterException("No data found with specified parameters...");
            }
        }
    }

    public void saveFile(MultipartFile file, String slug) throws IOException, BookstoreApiWrongParameterException {
        String savePath = storage.saveNewBookImage(file, slug);
        Book bookToUpdate = getBookFromSlug(slug);
        bookToUpdate.setImage(savePath);
        bookRepository.save(bookToUpdate);
    }


    public List<Book> getBooksBySearch()
    {
        return bookRepository.findAllByIdBetween(0, 10);
    }

    public byte[] getBookFile(String hash) throws IOException {
        Path path = storage.getBookFilePath(hash);
        MediaType mediaType = storage.getBookFileMime(hash);
        byte[] data = storage.getBookFileByteArray(hash);
        return data;
    }

    public Path getPathFile(String hash) {
        return storage.getBookFilePath(hash);
    }

    public MediaType getBookFileMime(String hash) {
        return storage.getBookFileMime(hash);
    }

    public ApiResponse<BooksPageDto> getResponseBooks(List<Book> books){
        ApiResponse<BooksPageDto> response = new ApiResponse<>();
        BooksPageDto data = new BooksPageDto(books);
        response.setDebugMessage("successful request");
        response.setMessage("data size: " + data.getCount() + " elements");
        response.setStatus(HttpStatus.OK);
        response.setTimeStamp(LocalDateTime.now());
        response.setData(data);
        return response;
    }

    public List<Book> getBooksInSlugs(String contents){
        contents = contents.startsWith("/") ? contents.substring(1) : contents;
        contents = contents.endsWith("/") ? contents.substring(0, contents.length() - 1) : contents;
        String[] cookieSlugs = contents.split("/");
        return bookRepository.findBooksBySlugIn(cookieSlugs);
    }

    public List<Book> getBooksInCart(Integer type){
        List<Book> list = bookRepository.findBooksByBook2UserEntityList_TypeIdAndBook2UserEntityList_User_Email(type, userRegister.getCurrentUser().getEmail());
        return list;
    }




}
