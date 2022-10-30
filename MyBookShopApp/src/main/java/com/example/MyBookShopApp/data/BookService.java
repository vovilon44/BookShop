package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.data.struct.tag.TagEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.List;

@Service
public class BookService {

    private BookRepository bookRepository;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public BookService(BookRepository bookRepository, NamedParameterJdbcTemplate jdbcTemplate) {
        this.bookRepository = bookRepository;
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<Book> getPageOfRecommendedBooks(Integer offset, Integer limit){
        return jdbcTemplate.query("select *, (select authors.name from authors inner join book2author on book2author.author_id = authors.id where book2author.book_id=books.id limit 1) as author from books offset " + offset * limit + " limit " + limit + " ;", (ResultSet rs, int rowNum) -> {
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
            book.setAuthor(rs.getString("author"));
            return book;
        });

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
            book.setAuthor(rs.getString("author"));
            book.setRating(rs.getDouble("rating"));
            return book;
        });
    }


    public List<Book> getListOfRecentBooks(Integer offset, Integer limit, String from, String to){
        return jdbcTemplate.query("select *, (select authors.name from authors inner join book2author on book2author.author_id = authors.id where book2author.book_id=books.id limit 1) as author from books where pub_date > '" + from + "' and pub_date < '" + to +"' order by pub_date desc offset " + offset * limit + " limit " + limit + " ;", (ResultSet rs, int rowNum) -> {
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
            book.setAuthor(rs.getString("author"));
            return book;
        });
    }



    public List<Book> getListOfRecentBooksWithoutDate(Integer offset, Integer limit){
        return jdbcTemplate.query("select *, (select authors.name from authors inner join book2author on book2author.author_id = authors.id where book2author.book_id=books.id limit 1) as author from books order by pub_date desc offset " + offset * limit + " limit " + limit + " ;", (ResultSet rs, int rowNum) -> {
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
            book.setAuthor(rs.getString("author"));
            return book;
        });
    }

    public List<Book> getBookFromGenre(String slugGenre, Integer offset, Integer limit){
        return jdbcTemplate.query("select *, (select authors.name from authors inner join book2author on book2author.author_id = authors.id where book2author.book_id=books.id limit 1) as author from books inner join book2genre gen on books.id = gen.book_id where gen.genre_id = (select id from genre where slug='" + slugGenre + "') offset " + offset * limit + " limit " + limit + " ;", (ResultSet rs, int rowNum) -> {
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
            book.setAuthor(rs.getString("author"));
            return book;
        });
    }

    public List<Book> getBooksFromAuthor(String slugAuthor, Integer offset, Integer limit){
        return jdbcTemplate.query("select books.id, books.description, books.discount, books.image, books.is_bestseller, books.price, books.pub_date, books.slug, books.title, authors.name as author from books inner join book2author on books.id = book2author.book_id inner join authors on book2author.author_id=authors.id where authors.slug='" + slugAuthor + "' offset " + offset * limit + " limit " + limit + " ;", (ResultSet rs, int rowNum) -> {
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
            book.setAuthor(rs.getString("author"));
            return book;
        });
    }

    public Book getBookFromSlug(String slugBook){
        return jdbcTemplate.query("select *, (select authors.name from authors inner join book2author on book2author.author_id = authors.id where book2author.book_id=books.id limit 1) as author from books where slug = '" + slugBook + "' ;", (ResultSet rs, int rowNum) -> {
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
            book.setAuthor(rs.getString("author"));
            book.setTags(jdbcTemplate.query("select * from tag inner join book2tag on book2tag.tag_id = tag.id where book2tag.book_id=" + rs.getInt("id") +";", (ResultSet res, int numRow) -> {
                TagEntity tag = new TagEntity();
                tag.setId(res.getInt("id"));
                tag.setName(res.getString("name"));
                tag.setSlug(res.getString("slug"));
                return tag;
            }));
            return book;
        }).get(0);
    }

    public List<Book> getBookFromTag(String slugTag, Integer offset, Integer limit){
        return jdbcTemplate.query("select *, (select authors.name from authors inner join book2author on book2author.author_id = authors.id where book2author.book_id=books.id limit 1) as author from books inner join book2tag on books.id = book2tag.book_id where book2tag.tag_id = (select id from tag where slug='" + slugTag + "') offset " + offset * limit + " limit " + limit + " ;", (ResultSet rs, int rowNum) -> {
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
            book.setAuthor(rs.getString("author"));
            return book;
        });
    }

    public List<Book> getBooksBySearch(String searchText, Integer offset, Integer limit)
    {
        return jdbcTemplate.query("select *, (select authors.name from authors inner join book2author on book2author.author_id = authors.id where book2author.book_id=books.id limit 1) as author from books where title like '%" + searchText + "%' offset " + offset * limit + " limit " + limit + " ;", (ResultSet rs, int rowNum) -> {
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
            book.setAuthor(rs.getString("author"));
            return book;
        });
    }


}
