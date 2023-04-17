package com.example.MyBookShopApp.data.repositories;

import com.example.MyBookShopApp.data.struct.book.links.Book2UserEntity;
import com.example.MyBookShopApp.security.BookstoreUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface Book2UserRepository extends JpaRepository<Book2UserEntity, Integer>
{
    List<Book2UserEntity> findBook2UserEntityByBook_SlugInAndUser_Email(List<String> slugs, String email);

    Book2UserEntity findBook2UserEntitiesByBookIdAndUser(Integer bookId, BookstoreUser user);

    Book2UserEntity findBook2UserEntitiesByBook_SlugAndUser(String slug, BookstoreUser user);
    Integer countAllByUserAndTypeIdIs(BookstoreUser user, Integer type);

    List<Book2UserEntity> findBook2UserEntitiesByUser_EmailAndTypeIdIs(String email, Integer type);

    Book2UserEntity findBook2UserEntityByBook_BookFileList_HashIsAndUser_IdIsAndTypeIdIs(String hash, Integer userId, Integer type);

    @Query(value = "select sum(price - price * discount) - (select balance from users where id = :userId ) from books where id in (select book_id from book2user where user_id = :userId and type_id = 2)",
            nativeQuery = true)
    Double getSummCustom(Integer userId);
}
