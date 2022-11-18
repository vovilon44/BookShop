package com.example.MyBookShopApp.data.repositories;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.data.struct.book.links.Book2AuthorEntity;
import com.example.MyBookShopApp.data.struct.book.links.Book2UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Book2UserRepository extends JpaRepository<Book2UserEntity, Integer>
{
    Book2UserEntity findBook2UserEntityByBook_SlugAndUser_EmailAndTypeId(String book, String email, Integer type);

}
