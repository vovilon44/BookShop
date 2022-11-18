package com.example.MyBookShopApp.data.repositories;

import com.example.MyBookShopApp.data.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;


public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findAllByIdBetween(Integer from, Integer to);

    Page<Book> findAll(Pageable page);


    Page<Book> findBooksByPubDateBetweenOrderByPubDateDesc(Date from, Date to, Pageable page);
    Page<Book> findBooksByOrderByPubDateDesc(Pageable page);
    Page<Book> findBooksByBook2GenreEntityList_Genre_SlugIs(String slugGenre, Pageable page);

    Page<Book> findAllByBook2AuthorEntityList_Author_SlugIs(String slugBook, Pageable page);
    Book findBookBySlug(String slugBook);


    Page<Book> findBooksByBook2TagEntityList_Tag_SlugIs(String slug, Pageable page);
    Page<Book> findAllByTitleContains(String searchText, Pageable page);

    List<Book> findBooksBySlugIn(String[] slugs);

    List<Book> findBooksByBook2UserEntityList_TypeIdAndBook2UserEntityList_User_Email(Integer type, String email);



}
