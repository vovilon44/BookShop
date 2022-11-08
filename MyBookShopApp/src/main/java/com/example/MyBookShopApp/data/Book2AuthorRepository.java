package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.data.struct.book.links.Book2AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Book2AuthorRepository extends JpaRepository<Book2AuthorEntity, Integer> {

    List<Book2AuthorEntity> findBook2AuthorEntityByBook_IdIs(Integer id);
}
