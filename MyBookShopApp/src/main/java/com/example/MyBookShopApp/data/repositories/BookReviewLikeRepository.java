package com.example.MyBookShopApp.data.repositories;

import com.example.MyBookShopApp.data.struct.book.review.BookReviewLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookReviewLikeRepository extends JpaRepository<BookReviewLikeEntity, Integer>
{
    BookReviewLikeEntity findByReview_IdAndUser_Email(Integer reviewId, String email);
}
