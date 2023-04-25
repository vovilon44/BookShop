package com.example.MyBookShopApp.data.repositories;

import com.example.MyBookShopApp.data.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;


public interface BookRepository extends JpaRepository<Book, Integer> {
    @Query(value = "select * from books where id not in (select book_id from book2user where user_id = :userId) and pub_date between :from and :to order by pub_date, books.id desc",
            nativeQuery = true)
    Page<Book> findBooksByPubDateBetweenOrderByPubDateDescCustom(@Param("from") Date from, @Param("to") Date to, @Param("userId") Integer userId,Pageable page);
    @Query(value = "select * from books where id not in (select book_id from book2user where user_id = :userId) and pub_date < :to order by pub_date, books.id desc",
            nativeQuery = true)
    Page<Book> findBooksByPubDateBeforeOrderByPubDateDescCustom(@Param("to") Date to, @Param("userId") Integer userId, Pageable page);

    @Query(value = "select * from books where id not in (select book_id from book2user where user_id = :userId) and pub_date > :from order by pub_date, books.id desc",
            nativeQuery = true)
    Page<Book> findBooksByPubDateAfterOrderByPubDateDescCustom(@Param("from") Date from, @Param("userId") Integer userId, Pageable page);

    @Query(value = "select * from books where id not in (select book_id from book2user where user_id = :userId) order by pub_date, books.id desc",
            nativeQuery = true)
    Page<Book> findByOrderByPubDateDescCustom(@Param("userId") Integer userId, Pageable page);
    Page<Book> findBooksByPubDateBetweenAndSlugIsNotInOrderByPubDateDesc(Date from, Date to, Set<String> slugs, Pageable page);
    Page<Book> findBooksByPubDateBeforeAndSlugIsNotInOrderByPubDateDesc(Date to, Set<String> slugs, Pageable page);
    Page<Book> findBooksByPubDateAfterAndSlugIsNotInOrderByPubDateDesc(Date from, Set<String> slugs, Pageable page);
    Page<Book> findBooksBySlugNotInOrderByPubDateDesc(Set<String> slugs, Pageable page);
    @Query(value = "select distinct books.* from books inner join book2genre on books.id = book2genre.book_id " +
            "inner join genre on book2genre.genre_id = genre.id " +
            "where genre.slug = :slug and books.id not in (select book_id from book2user where user_id = :userId) order by books.pub_date, books.id desc",
            nativeQuery = true)
    Page<Book> findBooksByBook2GenreEntityList_Genre_SlugIsCustom(String slug, Integer userId, Pageable page);

    Page<Book> findDistinctByBook2GenreEntityList_Genre_SlugIsAndSlugNotInOrderByPubDate(String slug, Set<String> slugs, Pageable page);

    Page<Book> findAllByBook2AuthorEntityList_Author_SlugIs(String slugAuthor, Pageable page);
    Book findBookBySlug(String slugBook);
    @Query(value = "select distinct books.* from books inner join book2tag on books.id = book2tag.book_id " +
            "inner join tag on book2tag.tag_id = tag.id " +
            "where tag.slug = :slug and books.id not in (select book_id from book2user where user_id = :userId) order by books.pub_date, books.id desc",
//            countQuery = "select count(*) from books inner join book2tag on books.id = book2tag.book_id " +
//                    "inner join tag on book2tag.tag_id = tag.id " +
//                    "where tag.slug = :slug and books.id not in (select book_id from book2user where user_id = :userId) group by books.id",
            nativeQuery = true)
    Page<Book> findBooksByBook2TagEntityList_Tag_SlugIsCustom(String slug, Integer userId, Pageable page);

    Page<Book> findDistinctBooksByBook2TagEntityList_Tag_SlugIsAndSlugNotInOrderByPubDateDesc(String slug, Set<String> slugs,Pageable page);

    @Query(value = "select distinct books.* from books " +
            "inner join book2author as b2a on books.id = b2a.book_id inner join authors as aut on aut.id = b2a.author_id " +
            "inner join book2tag as b2t on books.id = b2t.book_id inner join tag as tg on tg.id = b2t.tag_id " +
            "inner join book2genre as b2g on books.id = b2g.book_id inner join genre as gen on gen.id = b2g.genre_id " +
            "where (books.title ilike %:searchText% or aut.name ilike %:searchText% or books.description ilike %:searchText% or " +
            "tg.ru_name ilike %:searchText% or tg.en_name ilike %:searchText% or gen.ru_name ilike %:searchText% or gen.en_name ilike %:searchText%) " +
            "and books.id not in (select book_id from book2user where user_id = :userId) order by pub_date, books.id desc",
            nativeQuery = true)
    Page<Book> findAllByContainsAuthorizedCustom(String searchText, Integer userId, Pageable page);

    @Query(value = "select distinct books.* from books " +
            "inner join book2author as b2a on books.id = b2a.book_id inner join authors as aut on aut.id = b2a.author_id " +
            "inner join book2tag as b2t on books.id = b2t.book_id inner join tag as tg on tg.id = b2t.tag_id " +
            "inner join book2genre as b2g on books.id = b2g.book_id inner join genre as gen on gen.id = b2g.genre_id " +
            "where (books.title ilike %:searchText% or aut.name ilike %:searchText% or books.description ilike %:searchText% or " +
            "tg.ru_name ilike %:searchText% or tg.en_name ilike %:searchText% or tg.ru_name ilike %:searchText% or tg.en_name ilike %:searchText%) " +
            "and books.slug not in :slugs order by pub_date, books.id desc",
            nativeQuery = true)
    Page<Book> findAllByContainsNotAuthorizedCustom(String searchText, Pageable page, Set<String> slugs);

    List<Book> findBooksBySlugIn(List<String> slugs);

    Page<Book> findBooksByBook2UserEntityList_TypeIdAndBook2UserEntityList_User_Email(Integer type, String email, Pageable page);

    Page<Book> findBooksByBook2UserHistoryList_User_EmailOrderByBook2UserHistoryList_Time(String email, Pageable page);

    List<Book> findBooksByIdIn(List<Integer> ids);

    List<Book> findBooksByIdInOrderByPubDateDesc(List<Integer> ids);


    Book findBookById(Integer id);

    List<Book> findBooksByBook2UserEntityList_User_EmailIs(String username);

    @Query(value = "select *,(select count(*) from book2user where book2user.type_id=1 and book2user.book_id=books.id) * 0.4 +" +
            " (select count(*) from book2user where book2user.type_id=2 and book2user.book_id=books.id) * 0.7 +" +
            " (select count(*) from book2user where book2user.type_id=3 and book2user.book_id=books.id) +" +
            " (select count(*) from book2user_history where book2user_history.book_id=books.id) * 0.1 as rating " +
            "from books where id not in (select book_id from book2user where user_id = :userId) order by rating desc, books.pub_date desc",
            nativeQuery = true, countQuery = "select * from books where id not in (select book_id from book2user where user_id = :userId) ")
    Page<Book> getListOfPopularBooksAuthorized(@Param("userId") Integer userId, Pageable page);

    @Query(value = "select *,(select count(*) from book2user where book2user.type_id = 1 and book2user.book_id=books.id) * 0.4 + (select count(*) from book2user where book2user.type_id=2 and book2user.book_id=books.id) * 0.7 + (select count(*) from book2user where book2user.type_id=3 and book2user.book_id=books.id) + (select count(*) from book2user_history where book2user_history.book_id=books.id) * 0.1 as rating from books where slug not in :slugs order by rating desc, books.pub_date desc",
            nativeQuery = true, countQuery = "select count(*) from books where slug not in :slugs ")
    Page<Book> getListOfPopularBooksNotAuthorized(@Param("slugs") Set<String> slugs, Pageable page);

    Book findBookByBookFileList_HashIs(String hash);

    List<Book> findBooksByBook2GenreEntityList_Genre_RuNameContainingIgnoreCaseOrderByPubDateDesc(String searchText);

}
