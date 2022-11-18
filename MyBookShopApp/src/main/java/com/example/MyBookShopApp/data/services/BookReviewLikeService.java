package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.repositories.BookReviewLikeRepository;
import com.example.MyBookShopApp.data.repositories.BookReviewRepository;
import com.example.MyBookShopApp.data.struct.book.review.BookReviewLikeEntity;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BookReviewLikeService
{
    private final BookReviewLikeRepository bookReviewLikeRepository;

    private final BookReviewRepository bookReviewRepository;
    private  final BookstoreUserRegister userRegister;

    @Autowired
    public BookReviewLikeService(BookReviewLikeRepository bookReviewLikeRepository, BookReviewRepository bookReviewRepository, BookstoreUserRegister userRegister) {
        this.bookReviewLikeRepository = bookReviewLikeRepository;
        this.bookReviewRepository = bookReviewRepository;
        this.userRegister = userRegister;
    }

    public void addLikeReview(Integer reviewId, Integer value){
        BookReviewLikeEntity bookReviewLike = bookReviewLikeRepository.findByReview_IdAndUser_Email(reviewId, userRegister.getCurrentUser().getEmail());
        if (bookReviewLike == null){
            bookReviewLike = new BookReviewLikeEntity();
            bookReviewLike.setUser(userRegister.getCurrentUser());
            bookReviewLike.setReview(bookReviewRepository.findBookReviewEntityById(reviewId));
            bookReviewLike.setValue(value);
            bookReviewLike.setTime(LocalDate.now());
            bookReviewLikeRepository.save(bookReviewLike);
        } else {
            bookReviewLike.setValue(value);
            bookReviewLike.setTime(LocalDate.now());
            bookReviewLikeRepository.save(bookReviewLike);
        }
    }
}
