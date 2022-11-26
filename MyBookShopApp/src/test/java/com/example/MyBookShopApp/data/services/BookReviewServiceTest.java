package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.data.struct.book.review.BookReviewEntity;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class BookReviewServiceTest {

    private final BookReviewService bookReviewService;

    @Autowired
    public BookReviewServiceTest(BookReviewService bookReviewService) {
        this.bookReviewService = bookReviewService;
    }

    @Test
    void getBookReviewSlugIsNull()
    {
        List<BookReviewEntity> reviews = bookReviewService.getBookReview(null);
        assertTrue(CoreMatchers.is(new ArrayList<Book>()).matches(reviews));
    }

    @Test
    void getBookReviewSlugNotExist()
    {
        List<BookReviewEntity> reviews = bookReviewService.getBookReview("XXX");
        assertTrue(CoreMatchers.is(new ArrayList<Book>()).matches(reviews));
    }

    @Test
    void getBookReview()
    {
       List<BookReviewEntity> reviews = bookReviewService.getBookReview("book-qru-589");
        for (int i = 0; i < reviews.size() - 1; i++) {
            assertTrue(reviews.get(i).getRang()>=reviews.get(i+1).getRang());
        }
    }
}