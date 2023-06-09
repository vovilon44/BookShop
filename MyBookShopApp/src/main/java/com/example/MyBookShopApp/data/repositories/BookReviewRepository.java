package com.example.MyBookShopApp.data.repositories;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.data.struct.book.review.BookReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookReviewRepository extends JpaRepository<BookReviewEntity, Integer>
{


    BookReviewEntity findBookReviewEntityById(Integer id);
}
