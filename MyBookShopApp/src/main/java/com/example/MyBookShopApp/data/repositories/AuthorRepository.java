package com.example.MyBookShopApp.data.repositories;

import com.example.MyBookShopApp.data.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface AuthorRepository extends JpaRepository<Author, Integer> {

    Author findBySlug(String slug);

}
