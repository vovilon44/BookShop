package com.example.MyBookShopApp.data.repositories;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.data.telegram.BotBook;
import com.example.MyBookShopApp.data.telegram.BotBooksResponse;
import com.example.MyBookShopApp.data.telegram.BotUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class BookRepositoryJDBC {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public BookRepositoryJDBC(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final String RECOMENDED_AUTHORIZED_SQL_STRING = "select *, " +
            "(select name as authors from authors inner join book2author as t1 on authors.id = t1.author_id where t1.book_id = books.id limit 1), " +
            "(select count (*) from book2author inner join authors on book2author.author_id = authors.id where book2author.book_id = books.id group by book_id ) from books" +
            " where books.id not in (select book_id from book2user where user_id = :userId) and " +
            "(books.id in (select book_id from book2author where author_id in (select author_id from book2author where book_id in (select book_id from book2user where user_id = :userId))) or id in " +
            "(select book_id from book2tag where tag_id in (select tag_id from book2tag where book_id in (select book_id from book2user where user_id = :userId))) or id in " +
            "(select book_id from book2genre where genre_id in (select genre_id from book2genre where book_id in (select book_id from book2user where user_id = :userId))) or id in " +
            "(select book_id from book2user_history where user_id = :userId and time > current_date - 14)) " +
            "  order by pub_date desc offset :offset limit :limit";

    private final String RECOMENDED_NOT_AUTHORIZED_SQL_STRING = "select *, case when books.id in " +
            "(select book_id from book2author where author_id in (select author_id from book2author where book_id in (select id from books where slug in (:slugs)))) or id in " +
            "(select book_id from book2tag where tag_id in (select tag_id from book2tag where book_id in (select id from books where slug in (:slugs)))) or id in " +
            "(select book_id from book2genre where genre_id in (select genre_id from book2genre where book_id in (select id from books where slug in (:slugs)))) " +
            "then true else false end as recomended," +
            "(select name as authors from authors inner join book2author as t1 on authors.id = t1.author_id where t1.book_id = books.id limit 1), " +
            "(select count (*) from book2author inner join authors on book2author.author_id = authors.id where book2author.book_id = books.id group by book_id ) " +
            "from books where books.slug not in (:slugsMy) order by recomended desc, pub_date desc offset :offset limit :limit";

    private final String SQL_FOR_BOT_BOOK = "select distinct books.*, (select AVG(like_value) as rating from book_like2user where book_like2user.book_id = books.id group by book_like2user.book_id), " +
            "COALESCE((select string_agg(tag.ru_name, ', ') from book2tag as b2t inner join tag on b2t.tag_id = tag.id where b2t.book_id = books.id group by b2t.book_id), 'отсутствует') as tags, " +
            "COALESCE((select string_agg(genre.ru_name, ', ') from book2genre as b2g inner join genre on b2g.genre_id = genre.id where b2g.book_id = books.id group by b2g.book_id), 'отсутствует') as genres, " +
            "COALESCE((select string_agg(authors.name, ', ') from book2author as b2a inner join authors on b2a.author_id = authors.id where b2a.book_id = books.id group by b2a.book_id), 'отсутствует') as authors " +
            "from books ";

//    private final String RECOMENDED_AUTHORIZED_SQL_STRING_COUNT = "select count(*) from books" +
//            " where books.id not in (select book_id from book2user where user_id = :userId) and " +
//            "(books.id in (select book_id from book2author where author_id in (select author_id from book2author where book_id in (select book_id from book2user where user_id = :userId))) or id in " +
//            "(select book_id from book2tag where tag_id in (select tag_id from book2tag where book_id in (select book_id from book2user where user_id = :userId))) or id in " +
//            "(select book_id from book2genre where genre_id in (select genre_id from book2genre where book_id in (select book_id from book2user where user_id = :userId))) or id in " +
//            "(select book_id from book2user_history where user_id = :userId and time > current_date - 14))";

//    private final String RECOMENDED_AUTHORIZED_SQL_STRING = "select *, case when books.id in " +
//            "(select book_id from book2author where author_id in (select author_id from book2author where book_id in (select book_id from book2user where user_id = :userId))) or id in " +
//            "(select book_id from book2tag where tag_id in (select tag_id from book2tag where book_id in (select book_id from book2user where user_id = :userId))) or id in " +
//            "(select book_id from book2genre where genre_id in (select genre_id from book2genre where book_id in (select book_id from book2user where user_id = :userId))) or id in " +
//            "(select book_id from book2user_history where user_id = :userId and time > current_date - 14)  then true else false end as recomended, " +
//            "(select name as authors from authors inner join book2author as t1 on authors.id = t1.author_id where t1.book_id = books.id limit 1), " +
//            "(select count (*) from book2author inner join authors on book2author.author_id = authors.id where book2author.book_id = books.id group by book_id ) from books" +
//            " where books.id not in (select book_id from book2user where user_id = :userId) order by recomended desc, pub_date desc offset :offset limit :limit";


//    private final String RECOMENDED_NOT_AUTHORIZED_SQL_STRING_COUNT = "select count(*) from books where books.id in " +
//            "(select book_id from book2author where author_id in (select author_id from book2author where book_id in (select id from books where slug in (:slugs)))) or id in " +
//            "(select book_id from book2tag where tag_id in (select tag_id from book2tag where book_id in (select id from books where slug in (:slugs)))) or id in " +
//            "(select book_id from book2genre where genre_id in (select genre_id from book2genre where book_id in (select id from books where slug in (:slugs)))) " +
//            " and books.slug not in (:slugsMy) order by pub_date desc offset :offset limit :limit";


//        private final String RECOMENDED_WITHOUT_MAIN_BOOKS_AUTHORIZED = "select books.id, books.pub_date, books.is_bestseller, books.slug, books.title, books.image, books.description, books.price, books.discount, " +
//            "AVG(like_value) as rating, (select name as authors from authors inner join book2author as t1 on authors.id = t1.author_id where t1.book_id = books.id limit 1)," +
//            "(select count (*) from book2author inner join authors on book2author.author_id = authors.id where book2author.book_id = books.id group by book_id ) from " +
//            "books inner join book_like2user on books.id = book_like2user.book_id group by books.id order by rating desc offset :offset limit :limit";



    private final String SQL_FOR_BOT_BOOK_COUNT = "select count(distinct books.id) from books ";




    public List<Book> getPageOfRecommendedBooksNotAuthorized(Integer offset, Integer limit, List<Set<String>> slugs) {
//        Integer count = jdbcTemplate.queryForObject(RECOMENDED_NOT_AUTHORIZED_SQL_STRING_COUNT, new HashMap<>(), Integer.class);
        return jdbcTemplate.query(RECOMENDED_NOT_AUTHORIZED_SQL_STRING,
                Map.of("slugs", slugs.get(0), "slugsMy", slugs.get(1), "offset", offset * limit, "limit", limit),
                (ResultSet rs, int rowNum) -> getBooksResult(false, rs));
    }


    public List<Book> getPageOfRecommendedBooksAuthorized(Integer userId, Integer offset, Integer limit) {
//        Integer count = jdbcTemplate.queryForObject(RECOMENDED_AUTHORIZED_SQL_STRING_COUNT, Map.of("userId", userRegister.getCurrentUser().getId()), Integer.class);
        return jdbcTemplate.query(RECOMENDED_AUTHORIZED_SQL_STRING,
                Map.of("userId", userId, "offset", offset * limit, "limit", limit),
                (ResultSet rs, int rowNum) -> getBooksResult(false, rs));
    }

    public BotBooksResponse getBotSearchBooksByGenre(BotUser botUser, Integer limit){
        List<BotBook> books = new ArrayList<>();
        if (botUser.getPageNumber() >= 0) {
            books = jdbcTemplate.query(SQL_FOR_BOT_BOOK + " inner join book2genre on books.id = book2genre.book_id inner join genre on book2genre.genre_id = genre.id" +
                            " where books.id not in (select book_id from book2user where user_id = :userId) and genre.ru_name ilike :textSearch order by pub_date desc offset :offset limit :limit",
                    Map.of("textSearch", "%" + botUser.getWordForSearch() + "%", "offset", botUser.getPageNumber() * limit, "limit", limit, "userId", botUser.getUser().getId()),
                    (ResultSet rs, int rowNum) -> getBotBooksResult(rs, rowNum + botUser.getPageNumber() * limit));
        }
        Integer count = jdbcTemplate.queryForObject(SQL_FOR_BOT_BOOK_COUNT +" inner join book2genre on books.id = book2genre.book_id inner join genre on book2genre.genre_id = genre.id where" +
                        " books.id not in (select book_id from book2user where user_id = :userId) and genre.ru_name ilike :textSearch ",
                Map.of("textSearch","%" + botUser.getWordForSearch() + "%","userId", botUser.getUser().getId()), Integer.class);
        return new BotBooksResponse(books, count, botUser, limit);
    }

    public BotBooksResponse getBotSearchBooksByAuthors(BotUser botUser, Integer limit){
        List<BotBook> books = new ArrayList<>();
        if (botUser.getPageNumber() >= 0) {
            books = jdbcTemplate.query(SQL_FOR_BOT_BOOK + " inner join book2author as b2a on books.id = b2a.book_id inner join authors on b2a.author_id = authors.id" +
                            " where books.id not in (select book_id from book2user where user_id = :userId) and authors.name ilike :textSearch order by pub_date desc offset :offset limit :limit",
                    Map.of("textSearch", "%" + botUser.getWordForSearch() + "%", "offset", botUser.getPageNumber() * limit, "limit", limit, "userId", botUser.getUser().getId()),
                    (ResultSet rs, int rowNum) -> getBotBooksResult(rs, rowNum + botUser.getPageNumber() * limit));
        }
        Integer count = jdbcTemplate.queryForObject(SQL_FOR_BOT_BOOK_COUNT +" inner join book2author as b2a on books.id = b2a.book_id inner join authors on b2a.author_id = authors.id where" +
                        " books.id not in (select book_id from book2user where user_id = :userId) and authors.name ilike :textSearch ",
                Map.of("textSearch","%" + botUser.getWordForSearch() + "%","userId", botUser.getUser().getId()), Integer.class);
        return new BotBooksResponse(books, count, botUser, limit);
    }
    public BotBooksResponse getBotSearchBooksByTitle(BotUser botUser, Integer limit){
        List<BotBook> books = new ArrayList<>();
        if (botUser.getPageNumber() >= 0) {
            books = jdbcTemplate.query(SQL_FOR_BOT_BOOK + " where books.id not in (select book_id from book2user where user_id = :userId) and books.title ilike :textSearch order by pub_date desc offset :offset limit :limit",
                    Map.of("textSearch", "%" + botUser.getWordForSearch() + "%", "offset", botUser.getPageNumber() * limit, "limit", limit, "userId", botUser.getUser().getId()),
                    (ResultSet rs, int rowNum) -> getBotBooksResult(rs, rowNum + botUser.getPageNumber() * limit));
        }
        Integer count = jdbcTemplate.queryForObject(SQL_FOR_BOT_BOOK_COUNT +" where books.id not in (select book_id from book2user where user_id = :userId) and books.title ilike :textSearch ",
                Map.of("textSearch","%" + botUser.getWordForSearch() + "%","userId", botUser.getUser().getId()), Integer.class);
        return new BotBooksResponse(books, count, botUser, limit);
    }
    public BotBooksResponse getBotSearchBooksByTag(BotUser botUser, Integer limit){
        List<BotBook> books = new ArrayList<>();
        if (botUser.getPageNumber() >= 0) {
            books = jdbcTemplate.query(SQL_FOR_BOT_BOOK + " inner join book2tag as b2t on books.id = b2t.book_id inner join tag on b2t.tag_id = tag.id where" +
                            " books.id not in (select book_id from book2user where user_id = :userId) and  tag.ru_name ilike :textSearch order by pub_date desc offset :offset limit :limit",
                    Map.of("textSearch", "%" + botUser.getWordForSearch() + "%", "offset", botUser.getPageNumber() * limit, "limit", limit, "userId", botUser.getUser().getId()),
                    (ResultSet rs, int rowNum) -> getBotBooksResult(rs, rowNum + botUser.getPageNumber() * limit));
        }
        Integer count = jdbcTemplate.queryForObject(SQL_FOR_BOT_BOOK_COUNT +" inner join book2tag as b2t on books.id = b2t.book_id inner join tag on b2t.tag_id = tag.id where" +
                        " books.id not in (select book_id from book2user where user_id = :userId) and tag.ru_name ilike :textSearch ",
                Map.of("textSearch","%" + botUser.getWordForSearch() + "%","userId", botUser.getUser().getId()), Integer.class);
        return new BotBooksResponse(books, count, botUser, limit);
    }
    public BotBooksResponse getBotSearchBooksByAll(BotUser botUser, Integer limit){
        List<BotBook> books = new ArrayList<>();
        if (botUser.getPageNumber() >= 0) {
            books = jdbcTemplate.query(SQL_FOR_BOT_BOOK +
                            " inner join book2author as b2a on books.id = b2a.book_id inner join authors as aut on aut.id = b2a.author_id " +
                            "inner join book2tag as b2t on books.id = b2t.book_id inner join tag as tg on tg.id = b2t.tag_id " +
                            "inner join book2genre as b2g on books.id = b2g.book_id inner join genre as gen on gen.id = b2g.genre_id " +
                            "where books.id not in (select book_id from book2user where user_id = :userId) and (books.title ilike :textSearch or aut.name ilike :textSearch or gen.ru_name ilike :textSearch or tg.ru_name ilike :textSearch) order by pub_date desc offset :offset limit :limit",
                    Map.of("textSearch", "%" + botUser.getWordForSearch() + "%", "offset", botUser.getPageNumber() * limit, "limit", limit, "userId", botUser.getUser().getId()),
                    (ResultSet rs, int rowNum) -> getBotBooksResult(rs, rowNum + botUser.getPageNumber() * limit));
        }
        Integer count = jdbcTemplate.queryForObject(SQL_FOR_BOT_BOOK_COUNT +
                        " inner join book2author as b2a on books.id = b2a.book_id inner join authors as aut on aut.id = b2a.author_id " +
                        "inner join book2tag as b2t on books.id = b2t.book_id inner join tag as tg on tg.id = b2t.tag_id " +
                        "inner join book2genre as b2g on books.id = b2g.book_id inner join genre as gen on gen.id = b2g.genre_id " +
                        "where books.id not in (select book_id from book2user where user_id = :userId) and (books.title ilike :textSearch or aut.name ilike :textSearch or gen.ru_name ilike :textSearch or tg.ru_name ilike :textSearch)"
                , Map.of("textSearch","%" + botUser.getWordForSearch() + "%","userId", botUser.getUser().getId()), Integer.class);
        return new BotBooksResponse(books, count, botUser, limit);
    }
    public BotBooksResponse getBotBooksMain(BotUser botUser, Integer limit, Integer type) {
        List<BotBook> books = new ArrayList<>();
        if (botUser.getPageNumber() >= 0) {
            books = jdbcTemplate.query(SQL_FOR_BOT_BOOK +
                            " right join book2user on books.id = book2user.book_id where book2user.user_id = :userId and book2user.type_id = :type order by pub_date desc offset :offset limit :limit",
                    Map.of("userId", botUser.getUser().getId(), "type", type, "offset", botUser.getPageNumber() * limit, "limit", limit),
                    (ResultSet rs, int rowNum) -> getBotBooksResult(rs, rowNum + botUser.getPageNumber() * limit));
        }
        Integer count = jdbcTemplate.queryForObject(SQL_FOR_BOT_BOOK_COUNT +" right join book2user on books.id = book2user.book_id where book2user.user_id = :userId and book2user.type_id = :type", Map.of("userId", botUser.getUser().getId(), "type", type), Integer.class);
        return new BotBooksResponse(books, count, botUser, limit);
    }
    public BotBooksResponse getBotBookById(Integer bookId){
        List<BotBook> books = jdbcTemplate.query(SQL_FOR_BOT_BOOK +
                        " where books.id = :bookId",
                Map.of("bookId", bookId),
                (ResultSet rs, int rowNum) -> getBotBooksResult(rs, 1));

        return new BotBooksResponse(books.get(0));
    }


    private Book getBooksResult(boolean with_Rating, ResultSet rs) throws SQLException {
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
        String authors = rs.getString("authors");
        if (authors != null) {
            book.setAuthors(rs.getInt("count") > 1 ? authors + " и другие" : authors);
        } else {
            book.setAuthors("отсутствует");
        }
        return book;
    }
    private BotBook getBotBooksResult(ResultSet rs, int rowNumber) throws SQLException {
        BotBook bookData = new BotBook();
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
        bookData.setBook(book);
        bookData.setRating(rs.getDouble("rating"));
        bookData.setGenres(rs.getString("genres"));
        bookData.setTags(rs.getString("tags"));
        bookData.setAuthors(rs.getString("authors"));
        return bookData;
    }

//    public BooksPageDto getPageOfRecommendedBooksWithoutMain(Integer offset, Integer limit) {
//        Integer count = jdbcTemplate.queryForObject("select count(*) from books ", new HashMap<>(), Integer.class);
//        List<Book> books = jdbcTemplate.query(RECOMENDED_WITHOUT_MAIN_BOOKS_AUTHORIZED,
//                Map.of("offset", offset * limit, "limit" , limit),
//                (ResultSet rs, int rowNum) -> getBooksResult(false, rs));
//        return new BooksPageDto(books, offset * limit + books.size()  == count, count);
//
//    }
}
