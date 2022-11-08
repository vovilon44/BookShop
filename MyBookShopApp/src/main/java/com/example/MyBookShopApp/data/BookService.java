package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.data.struct.book.links.BookLike2UserEntity;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Logger;

@Service
public class BookService {

    private BookRepository bookRepository;
    private Book2AuthorRepository book2AuthorRepository;
    private BookLike2UserRepository bookLike2UserRepository;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ResourceStorage storage;

    @Autowired
    public BookService(BookRepository bookRepository, NamedParameterJdbcTemplate jdbcTemplate, ResourceStorage storage, Book2AuthorRepository book2AuthorRepository, BookLike2UserRepository bookLike2UserRepository) {
        this.bookRepository = bookRepository;
        this.bookLike2UserRepository = bookLike2UserRepository;
        this.book2AuthorRepository = book2AuthorRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.storage = storage;
    }


    public Page<Book> getPageOfRecommendedBooks(Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset,limit);
        return bookRepository.findAll(nextPage);

    }



    public List<Book> getListOfPopularBooks(Integer offset, Integer limit){
        return jdbcTemplate.query("select *, (select authors.name from authors inner join book2author on book2author.author_id = authors.id where book2author.book_id=books.id limit 1) as author,(select count(*) from book2user where book2user.type_id=1 and book2user.book_id=books.id) * 0.4 + (select count(*) from book2user where book2user.type_id=2 and book2user.book_id=books.id) * 0.7 + (select count(*) from book2user where book2user.type_id=3 and book2user.book_id=books.id) as rating from books order by rating desc offset " + offset * limit+ " limit " + limit + " ;", (ResultSet rs, int rowNum) -> {
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
    }


    public Page<Book> getListOfRecentBooks(Integer offset, Integer limit, String from, String to) throws ParseException {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        Pageable nextPage = PageRequest.of(offset,limit);
        return bookRepository.findBooksByPubDateBetweenOrderByPubDateDesc(df.parse(from), df.parse(to),nextPage);
    }



    public Page<Book> getListOfRecentBooksWithoutDate(Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset,limit);
        return bookRepository.findBooksByOrderByPubDateDesc(nextPage);
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
            Logger.getLogger("LOOGGGG").info("BOOK: " + book);
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
        Logger.getLogger("BookService").info("saveFile...!!!");
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
        Logger.getLogger("BookServ").info("bookFilePath: " + path);
        MediaType mediaType = storage.getBookFileMime(hash);
        Logger.getLogger("BookServ").info("bookFileMedia: " + mediaType);
        byte[] data = storage.getBookFileByteArray(hash);
        Logger.getLogger("BookServ").info("bookFileLength: " + data.length);
        return data;
    }

    public Path getPathFile(String hash) {
        return storage.getBookFilePath(hash);
    }

    public MediaType getBookFileMime(String hash) {
        return storage.getBookFileMime(hash);
    }
}
