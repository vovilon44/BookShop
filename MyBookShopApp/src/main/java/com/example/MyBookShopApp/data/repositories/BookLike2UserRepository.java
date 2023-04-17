package com.example.MyBookShopApp.data.repositories;

import com.example.MyBookShopApp.data.struct.book.links.BookLike2UserEntity;
import com.example.MyBookShopApp.security.BookstoreUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookLike2UserRepository extends JpaRepository<BookLike2UserEntity, Integer>
{
    List<BookLike2UserEntity> findBookLike2UserEntitiesByBookIdIs(Integer id);

    BookLike2UserEntity findByBook_SlugAndUser(String book, BookstoreUser user);
}
