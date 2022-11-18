package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.repositories.BookRepository;
import com.example.MyBookShopApp.data.repositories.BookReviewRepository;
import com.example.MyBookShopApp.data.struct.book.review.BookReviewEntity;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookReviewService
{
    private final BookReviewRepository bookReviewRepository;
    private final BookRepository bookRepository;

    private  final BookstoreUserRegister userRegister;

    @Autowired
    public BookReviewService(BookReviewRepository bookReviewRepository, BookRepository bookRepository, BookstoreUserRegister userRegister) {
        this.bookReviewRepository = bookReviewRepository;
        this.bookRepository = bookRepository;
        this.userRegister = userRegister;
    }

    public List<BookReviewEntity> getBookReview(String slug){
        return bookReviewRepository.findByBook_Slug(slug);
    }

    public void addReview(String slug, String text) {
        BookReviewEntity bookReview = new BookReviewEntity();
        bookReview.setUser(userRegister.getCurrentUser());
        bookReview.setBook(bookRepository.findBookBySlug(slug));
        bookReview.setTime(LocalDate.now());
        bookReview.setText(text);
        bookReview.setBookReviewLikeEntityList(new ArrayList<>());
        bookReviewRepository.save(bookReview);
    }
}
