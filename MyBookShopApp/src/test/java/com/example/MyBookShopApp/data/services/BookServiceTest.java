package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.security.ContactConfirmationPayload;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;


@AutoConfigureMockMvc
@SpringBootTest
class BookServiceTest {

    private final BookService bookService;
    private ContactConfirmationPayload payload;



    @Autowired
    public BookServiceTest(BookService bookService) {
        this.bookService = bookService;
    }

    @BeforeEach
    void setUp() {
        payload = new ContactConfirmationPayload();
        payload.setContact("aaaa@aa.aa");
        payload.setCode("332233");

    }

    @AfterEach
    void tearDown()
    {

    }

    @Test
    void getPageOfRecommendedBooksOffsetIsNull()
    {
        List<Book> bookList = bookService.getPageOfRecommendedBooks(null, 6);
        assertTrue(CoreMatchers.is(new ArrayList<Book>()).matches(bookList));
    }

    @Test
    void getPageOfRecommendedBooksLimitIsNull()
    {
        List<Book> bookList = bookService.getPageOfRecommendedBooks(0, null);
        assertTrue(CoreMatchers.is(new ArrayList<Book>()).matches(bookList));
    }

    @Test
    void getPageOfRecommendedBooksBigLimit()
    {
        List<Book> bookList = bookService.getPageOfRecommendedBooks(0, 999999);
        assertTrue(CoreMatchers.is(1000).matches(bookList.size()));
        for (int i = 0; i < bookList.size() - 1; i++) {
            assertTrue(bookList.get(i).getRating()>=bookList.get(i+1).getRating());
        }
    }

    @Test
    void getPageOfRecommendedBooksBigOffset()
    {

        List<Book> bookList = bookService.getPageOfRecommendedBooks(999999, 6);
        assertTrue(CoreMatchers.is(new ArrayList<Book>()).matches(bookList));
    }

    @Test
    void getPageOfRecommendedBooks()
    {
        List<Book> bookList = bookService.getPageOfRecommendedBooks(0, 6);
        assertTrue(CoreMatchers.is(6).matches(bookList.size()));
        for (int i = 0; i < bookList.size() - 1; i++) {
            assertTrue(bookList.get(i).getRating()>=bookList.get(i+1).getRating());
        }
    }

    @Test
    void getPageOfRecommendedBooksAuthorizedOffsetIsNull()
    {
        List<Book> bookList = bookService.getPageOfRecommendedBooksAuthorized(null, 6, "lcodner1@email.ru");
        assertTrue(CoreMatchers.is(new ArrayList<Book>()).matches(bookList));
    }

    @Test
    void getPageOfRecommendedBooksAuthorizedLimitIsNull()
    {
        List<Book> bookList = bookService.getPageOfRecommendedBooksAuthorized(0, null, "lcodner1@email.ru");
        assertTrue(CoreMatchers.is(new ArrayList<Book>()).matches(bookList));
    }

    @Test
    void getPageOfRecommendedBooksAuthorizedBigLimit()
    {
        List<Book> bookList = bookService.getPageOfRecommendedBooksAuthorized(0, 999999, "lcodner1@email.ru");
        assertTrue(bookList.size() > 6);
    }

    @Test
    void getPageOfRecommendedBooksAuthorizedBigOffset()
    {
        List<Book> bookList = bookService.getPageOfRecommendedBooksAuthorized(999999, 6, "lcodner1@email.ru");
        assertTrue(CoreMatchers.is(new ArrayList<Book>()).matches(bookList));
    }

    @Test
    void getPageOfRecommendedBooksAuthorized()
    {
        List<Book> bookList = bookService.getPageOfRecommendedBooksAuthorized(1, 6, "lcodner1@email.ru");
        assertTrue(CoreMatchers.is(6).matches(bookList.size()));
    }

    @Test
    void getListOfPopularBooksOffsetIsNull()
    {
        List<Book> bookList = bookService.getListOfPopularBooks(null, 6);
        assertTrue(CoreMatchers.is(new ArrayList<Book>()).matches(bookList));


    }

    @Test
    void getListOfPopularBooksLimitIsNull()
    {
        List<Book> bookList = bookService.getListOfPopularBooks(0, null);
        assertTrue(CoreMatchers.is(new ArrayList<Book>()).matches(bookList));
    }

    @Test
    void getListOfPopularBooksBigLimit()
    {
        List<Book> bookList = bookService.getListOfPopularBooks(0, 99999);
        assertTrue(CoreMatchers.is(1000).matches(bookList.size()));
    }

    @Test
    void getListOfPopularBooksBigOffset()
    {
        List<Book> bookList = bookService.getListOfPopularBooks(9999999, 6);
        assertTrue(CoreMatchers.is(new ArrayList<Book>()).matches(bookList));

    }

    @Test
    void getListOfPopularBooksCheckRang()
    {
        List<Book> bookList = bookService.getListOfPopularBooks(0, 6);
        assertTrue(CoreMatchers.is(6).matches(bookList.size()));
        for (int i = 0; i < bookList.size() - 1; i++) {
            assertTrue(bookList.get(i).rangBook()>=bookList.get(i+1).rangBook());
        }
    }
}