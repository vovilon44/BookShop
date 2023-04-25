package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.data.BooksPageDto;
import com.example.MyBookShopApp.data.struct.genre.GenreEntity;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.security.BookstoreUser;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import com.example.MyBookShopApp.security.ContactConfirmationPayload;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookServiceTest {

    private final BookService bookService;

    @MockBean
    private BookstoreUserRegister userRegisterMock;



    @Autowired
    public BookServiceTest(BookService bookService) {
        this.bookService = bookService;
    }



    @Test
    void getPageOfRecommendedBooksNotAuthorized() {
        String cart = "book-lnt-240";
        String kept = "book-rah-494";
        String history = "book-rjy-389";
        BooksPageDto books = bookService.getPageOfRecommendedBooksNotAuthorized(0, 40, cart, kept, history);
        for (int i = 0; i < books.getBooks().size(); i++) {
            if (i < 19) {
                assertTrue(books.getBooks().get(i).getId() >= 3 && books.getBooks().get(i).getId() < 22);
            } else {
                assertTrue(books.getBooks().get(i).getId() > 21);
            }
        }
        assertTrue(books.getBooks().size() == 29);
    }

    @Test
    void getPageOfRecommendedBooksNotAuthorizedOneFieldNull() {
        String cart = "book-lnt-240/book-rjy-389";
        String kept = "book-rah-494";
        BooksPageDto books = bookService.getPageOfRecommendedBooksNotAuthorized(0, 40, cart, kept, null);
        for (int i = 0; i < books.getBooks().size(); i++) {
            if (i < 18) {
                assertTrue(books.getBooks().get(i).getId() > 3 && books.getBooks().get(i).getId() < 22);
            } else {
                assertTrue(books.getBooks().get(i).getId() > 21);
            }
        }
        assertTrue(books.getBooks().size() == 28);
    }

    @Test
    void getPageOfRecommendedBooksNotAuthorizedWithoutMyBook() {
        BooksPageDto books = bookService.getPageOfRecommendedBooksNotAuthorized(0, 40, null, null, null);
        assertTrue(books.getBooks().size() == 31);
    }

    @Test
    void getPageOfRecommendedBooksAuthorized() {
        BookstoreUser user = new BookstoreUser();
        user.setId(1);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getPageOfRecommendedBooksAuthorized(0, 30);
        for (int i = 0; i < books.getBooks().size(); i++) {
            if (i < 18) {
                assertTrue(books.getBooks().get(i).getId() >= 3 && books.getBooks().get(i).getId() < 22);
            } else {
                assertTrue(books.getBooks().get(i).getId() > 21);
            }
        }
        assertTrue(books.getBooks().size() == 12);
    }
    @Test
    void getPageOfRecommendedBooksAuthorizedWithoutMyBook() {
        BookstoreUser user = new BookstoreUser();
        user.setId(3);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getPageOfRecommendedBooksAuthorized(0, 30);
        assertTrue(books.getBooks().size() == 0);
    }

    @Test
    void getListOfPopularBooksAuthorizedWithoutCartKept() {
        BookstoreUser user = new BookstoreUser();
        user.setId(3);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getListOfPopularBooksAuthorized(0, 40);
        for (int i = 0; i < 3; i++) {
            assertTrue(books.getBooks().get(i).getId() >= 1 && books.getBooks().get(i).getId() < 4);
        }
        assertTrue(books.getBooks().size() == 31);
    }

    @Test
    void getListOfPopularBooksAuthorizedWithCartKept() {
        BookstoreUser user = new BookstoreUser();
        user.setId(1);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getListOfPopularBooksAuthorized(0, 40);
        for (int i = 0; i < books.getBooks().size(); i++) {
            if (i == 0){
                assertTrue(books.getBooks().get(i).getId() == 3);
            } else {
                assertTrue(books.getBooks().get(i).getId() != 1 && books.getBooks().get(i).getId() != 2);
            }
        }
        assertTrue(books.getBooks().size() == 29);
    }

    @Test
    void getListOfPopularBooksNotAuthorizedWithCartKept() {
        BooksPageDto books = bookService.getListOfPopularBooksNotAuthorized(0, 40, "book-lnt-240", "book-rah-494");
        for (int i = 0; i < books.getBooks().size(); i++) {
            if (i == 0){
                assertTrue(books.getBooks().get(i).getId() == 3);
            } else {
                assertTrue(books.getBooks().get(i).getId() != 1 && books.getBooks().get(i).getId() != 2);
            }
        }
        assertTrue(books.getBooks().size() == 29);
    }

    @Test
    void getListOfPopularBooksNotAuthorizedWithoutCartKept() {
        BooksPageDto books = bookService.getListOfPopularBooksNotAuthorized(0, 40, "", "");
        for (int i = 0; i < 3; i++) {
            assertTrue(books.getBooks().get(i).getId() >= 1 && books.getBooks().get(i).getId() < 4);
        }
        assertTrue(books.getBooks().size() == 31);
    }

    @Test
    void getListOfPopularBooksNotAuthorizedCartKeptNull() {
        BooksPageDto books = bookService.getListOfPopularBooksNotAuthorized(0, 40, null, null);
        for (int i = 0; i < 3; i++) {
            assertTrue(books.getBooks().get(i).getId() >= 1 && books.getBooks().get(i).getId() < 4);
        }
        assertTrue(books.getBooks().size() == 31);
    }

    @Test
    void getListOfRecentBooksAuthorized() throws ParseException {
        BookstoreUser user = new BookstoreUser();
        user.setId(1);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getListOfRecentBooksAuthorized(0, 40, "06.01.2016", "17.11.2018");
        assertTrue(books.getBooks().size() == 13);
    }

    @Test
    void getListOfRecentBooksAuthorizedWithoutMain() throws ParseException {
        BookstoreUser user = new BookstoreUser();
        user.setId(3);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getListOfRecentBooksAuthorized(0, 40, "06.01.2016", "17.11.2018");
        assertTrue(books.getBooks().size() == 14);
    }

    @Test
    void getListOfRecentBooksAuthorizedChangeFromAndTo() throws ParseException {
        BookstoreUser user = new BookstoreUser();
        user.setId(3);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getListOfRecentBooksAuthorized(0, 40, "07.01.2016", "16.11.2018");
        assertTrue(books.getBooks().size() == 12);
    }

    @Test
    void getListOfRecentBooksAuthorizedWithoutFrom() throws ParseException {
        BookstoreUser user = new BookstoreUser();
        user.setId(3);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getListOfRecentBooksAuthorized(0, 40, null, "16.11.2018");
        assertTrue(books.getBooks().size() == 23);
    }

    @Test
    void getListOfRecentBooksAuthorizedWithoutTo() throws ParseException {
        BookstoreUser user = new BookstoreUser();
        user.setId(3);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getListOfRecentBooksAuthorized(0, 40, "07.01.2016", null);
        assertTrue(books.getBooks().size() == 20);
    }

    @Test
    void getListOfRecentBooksAuthorizedWithoutFromAndTo() throws ParseException {
        BookstoreUser user = new BookstoreUser();
        user.setId(3);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getListOfRecentBooksAuthorized(0, 40, null, null);
        assertTrue(books.getBooks().size() == 31);
    }

    @Test
    void getListOfRecentBooksNotAuthorized() throws ParseException {
        BooksPageDto books = bookService.getListOfRecentBooksNotAuthorized(0, 40, "06.01.2016", "17.11.2018", "book-lnt-240", "book-rah-494");
        assertTrue(books.getBooks().size() == 13);
    }

    @Test
    void getListOfRecentBooksNotAuthorizedWithoutMain() throws ParseException {
        BooksPageDto books = bookService.getListOfRecentBooksNotAuthorized(0, 40, "06.01.2016", "17.11.2018", null, null);
        assertTrue(books.getBooks().size() == 14);
    }

    @Test
    void getListOfRecentBooksNotAuthorizedChangeFromAndTo() throws ParseException {
        BooksPageDto books = bookService.getListOfRecentBooksNotAuthorized(0, 40, "07.01.2016", "16.11.2018", null, null);
        assertTrue(books.getBooks().size() == 12);
    }

    @Test
    void getListOfRecentBooksNotAuthorizedWithoutFrom() throws ParseException {
        BooksPageDto books = bookService.getListOfRecentBooksNotAuthorized(0, 40, null, "16.11.2018", null, null);
        assertTrue(books.getBooks().size() == 23);
    }

    @Test
    void getListOfRecentBooksNotAuthorizedWithoutTo() throws ParseException {
        BooksPageDto books = bookService.getListOfRecentBooksNotAuthorized(0, 40, "07.01.2016", null, null, null);
        assertTrue(books.getBooks().size() == 20);
    }

    @Test
    void getListOfRecentBooksNotAuthorizedWithoutFromAndTo() throws ParseException {
        BooksPageDto books = bookService.getListOfRecentBooksNotAuthorized(0, 40, null, null, null, null);
        assertTrue(books.getBooks().size() == 31);
    }

    @Test
    void getBookFromGenreAuthorizedWithMain() {
        BookstoreUser user = new BookstoreUser();
        user.setId(1);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getBookFromGenreAuthorized("genre-dfg-345", 0, 40);
        for (int i = 0; i < books.getBooks().size(); i++) {
            assertTrue(books.getBooks().get(i).getId() > 15 && books.getBooks().get(i).getId() < 18);
        }
        assertTrue(books.getBooks().size() == 2);
    }

    @Test
    void getBookFromGenreAuthorizedWithoutMain() {
        BookstoreUser user = new BookstoreUser();
        user.setId(3);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getBookFromGenreAuthorized("genre-dfg-345", 0, 40);
        for (int i = 0; i < books.getBooks().size(); i++) {
            assertTrue((books.getBooks().get(i).getId() > 15 && books.getBooks().get(i).getId() < 18) || books.getBooks().get(i).getId() == 1);
        }
        assertTrue(books.getBooks().size() == 3);
    }

    @Test
    void getBookFromGenreNotAuthorizedWithMain() {
        BooksPageDto books = bookService.getBookFromGenreNotAuthorized("genre-dfg-345", 0, 40, "book-lnt-240", "book-rah-494");
        for (int i = 0; i < books.getBooks().size(); i++) {
            assertTrue(books.getBooks().get(i).getId() > 15 && books.getBooks().get(i).getId() < 18);
        }
        assertTrue(books.getBooks().size() == 2);
    }

    @Test
    void getBookFromGenreNotAuthorizedWithoutMain() {
        BooksPageDto books = bookService.getBookFromGenreNotAuthorized("genre-dfg-345", 0, 40, null, null);
        for (int i = 0; i < books.getBooks().size(); i++) {
            assertTrue((books.getBooks().get(i).getId() > 15 && books.getBooks().get(i).getId() < 18) || books.getBooks().get(i).getId() == 1);
        }
        assertTrue(books.getBooks().size() == 3);
    }

    @Test
    void getBooksFromAuthorAuthorized() {
        BookstoreUser user = new BookstoreUser();
        user.setId(1);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getBooksFromAuthorAuthorized("author-fzo-628", 0, 40);
        for (int i = 0; i < books.getBooks().size(); i++) {
            assertTrue((books.getBooks().get(i).getId() > 3 && books.getBooks().get(i).getId() < 6) || books.getBooks().get(i).getId() == 1);
        }
        assertTrue(books.getBooks().size() == 3);
    }

    @Test
    void getBooksFromAuthorNotAuthorizedWithMainNull() {
        BooksPageDto books = bookService.getBooksFromAuthorNotAuthorized("author-fzo-628", 0, 40, null, null);
        for (int i = 0; i < books.getBooks().size(); i++) {
            assertTrue((books.getBooks().get(i).getId() > 3 && books.getBooks().get(i).getId() < 6) || books.getBooks().get(i).getId() == 1);
        }
        assertTrue(books.getBooks().size() == 3);
    }

    @Test
    void getBooksFromAuthorNotAuthorizedWithMain() {
        BooksPageDto books = bookService.getBooksFromAuthorNotAuthorized("author-fzo-628", 0, 40, "book-lnt-240", "book-rah-494");
        for (int i = 0; i < books.getBooks().size(); i++) {
            assertTrue((books.getBooks().get(i).getId() > 3 && books.getBooks().get(i).getId() < 6) || books.getBooks().get(i).getId() == 1);
        }
        assertTrue(books.getBooks().size() == 3);
    }

    @Test
    void getBookFromSlug() throws BookstoreApiWrongParameterException {
        Book book = bookService.getBookFromSlug("book-lnt-240");
        assertTrue(book.getId() == 1);

    }

    @Test
    void getBookFromTagNotAuthorizedWithMain() throws BookstoreApiWrongParameterException {
        BooksPageDto books = bookService.getBookFromTagNotAuthorized("tag-gdd-642", 0, 40, "book-lnt-240", "book-rah-494");
        for (int i = 0; i < books.getBooks().size(); i++) {
            assertTrue(books.getBooks().get(i).getId() > 9 && books.getBooks().get(i).getId() < 12);
        }
        assertTrue(books.getBooks().size() == 2);
    }

    @Test
    void getBookFromTagNotAuthorizedWithoutMain() throws BookstoreApiWrongParameterException {
        BooksPageDto books = bookService.getBookFromTagNotAuthorized("tag-gdd-642", 0, 40, null, null);
        for (int i = 0; i < books.getBooks().size(); i++) {
            assertTrue((books.getBooks().get(i).getId() > 9 && books.getBooks().get(i).getId() < 12) || books.getBooks().get(i).getId() == 1);
        }
        assertTrue(books.getBooks().size() == 3);
    }

    @Test
    void getBookFromTagAuthorizedWithMain() throws BookstoreApiWrongParameterException {
        BookstoreUser user = new BookstoreUser();
        user.setId(1);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getBookFromTagAuthorized("tag-gdd-642", 0, 40);
        for (int i = 0; i < books.getBooks().size(); i++) {
            assertTrue(books.getBooks().get(i).getId() > 9 && books.getBooks().get(i).getId() < 12);
        }
        assertTrue(books.getBooks().size() == 2);
    }

    @Test
    void getBookFromTagAuthorizedWithoutMain() throws BookstoreApiWrongParameterException {
        BookstoreUser user = new BookstoreUser();
        user.setId(3);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getBookFromTagAuthorized("tag-gdd-642", 0, 40);
        for (int i = 0; i < books.getBooks().size(); i++) {
            assertTrue((books.getBooks().get(i).getId() > 9 && books.getBooks().get(i).getId() < 12) || books.getBooks().get(i).getId() == 1);
        }
        assertTrue(books.getBooks().size() == 3);
    }

    @Test
    void getBooksBySearchAuthorizedWithoutMain() throws BookstoreApiWrongParameterException {
        BookstoreUser user = new BookstoreUser();
        user.setId(3);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getBooksBySearchAuthorized("ill", 0, 40);
        assertTrue(books.getBooks().size() == 2);
    }

    @Test
    void getBooksBySearchAuthorizedWithMain() throws BookstoreApiWrongParameterException {
        BookstoreUser user = new BookstoreUser();
        user.setId(1);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getBooksBySearchAuthorized("ill", 0, 40);
        assertTrue(books.getBooks().size() == 1);
    }

    @Test
    void getBooksBySearchNotAuthorizedWithMain() throws BookstoreApiWrongParameterException {
        BooksPageDto books = bookService.getBooksBySearchNotAuthorized("ill", 0, 40, "book-lnt-240", "book-rah-494");
        assertTrue(books.getBooks().size() == 1);
    }

    @Test
    void getBooksBySearchNotAuthorizedWithoutMain() throws BookstoreApiWrongParameterException {
        BooksPageDto books = bookService.getBooksBySearchNotAuthorized("ill", 0, 40, null, null);
        assertTrue(books.getBooks().size() == 2);
    }

    @Test
    void getBooksInSlugs() {
        List<String> slugs = List.of("book-lnt-240", "book-rah-494");
        List<Book> books = bookService.getBooksInSlugs(slugs);
        books.forEach(e->assertTrue(slugs.contains(e.getSlug())));
        assertTrue(books.size() == 2);

    }

    @Test
    void getBooksInMyType() {
        BookstoreUser user = new BookstoreUser();
        user.setId(1);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        List<Book> books = bookService.getBooksInMyType(1);
        assertTrue(books.size() == 1);
    }




    @Test
    void checkBookPresence() {
        assertTrue(bookService.checkBookPresence("book-lnt-240"));
    }


    @Test
    void getMyBooks() {
        BookstoreUser user = new BookstoreUser();
        user.setId(9);
        user.setEmail("belialpw2@bk.ru");
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getMyBooks(0, 40);
        assertTrue(books.getBooks().size() == 2);
    }

    @Test
    void getMyBooksInArchive() {
        BookstoreUser user = new BookstoreUser();
        user.setId(9);
        user.setEmail("belialpw2@bk.ru");
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getMyBooksInArchive(0, 40);
        assertTrue(books.getBooks().size() == 1);
    }

    @Test
    void getMyHistoryBooks() {
        BookstoreUser user = new BookstoreUser();
        user.setId(9);
        user.setEmail("belialpw2@bk.ru");
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getMyBooksInArchive(0, 40);
        assertTrue(books.getBooks().size() == 1);
    }

}