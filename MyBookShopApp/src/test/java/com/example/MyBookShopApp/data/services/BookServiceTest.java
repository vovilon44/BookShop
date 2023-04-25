package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.data.BooksPageDto;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.security.BookstoreUser;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.text.ParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        assertEquals(books.getBooks().size(), 29);
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
        assertEquals(books.getBooks().size(), 28);
    }

    @Test
    void getPageOfRecommendedBooksNotAuthorizedWithoutMyBook() {
        BooksPageDto books = bookService.getPageOfRecommendedBooksNotAuthorized(0, 40, null, null, null);
        assertEquals(31, books.getBooks().size());
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
        assertEquals(12, books.getBooks().size());
    }
    @Test
    void getPageOfRecommendedBooksAuthorizedWithoutMyBook() {
        BookstoreUser user = new BookstoreUser();
        user.setId(3);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getPageOfRecommendedBooksAuthorized(0, 30);
        assertEquals(0, books.getBooks().size());
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
        assertEquals(31, books.getBooks().size());
    }

    @Test
    void getListOfPopularBooksAuthorizedWithCartKept() {
        BookstoreUser user = new BookstoreUser();
        user.setId(1);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getListOfPopularBooksAuthorized(0, 40);
        for (int i = 0; i < books.getBooks().size(); i++) {
            if (i == 0){
                assertEquals(3, (int) books.getBooks().get(i).getId());
            } else {
                assertTrue(books.getBooks().get(i).getId() != 1 && books.getBooks().get(i).getId() != 2);
            }
        }
        assertEquals(29, books.getBooks().size());
    }

    @Test
    void getListOfPopularBooksNotAuthorizedWithCartKept() {
        BooksPageDto books = bookService.getListOfPopularBooksNotAuthorized(0, 40, "book-lnt-240", "book-rah-494");
        for (int i = 0; i < books.getBooks().size(); i++) {
            if (i == 0){
                assertEquals(3, (int) books.getBooks().get(i).getId());
            } else {
                assertTrue(books.getBooks().get(i).getId() != 1 && books.getBooks().get(i).getId() != 2);
            }
        }
        assertEquals(29, books.getBooks().size());
    }

    @Test
    void getListOfPopularBooksNotAuthorizedWithoutCartKept() {
        BooksPageDto books = bookService.getListOfPopularBooksNotAuthorized(0, 40, "", "");
        for (int i = 0; i < 3; i++) {
            assertTrue(books.getBooks().get(i).getId() >= 1 && books.getBooks().get(i).getId() < 4);
        }
        assertEquals(31, books.getBooks().size());
    }

    @Test
    void getListOfPopularBooksNotAuthorizedCartKeptNull() {
        BooksPageDto books = bookService.getListOfPopularBooksNotAuthorized(0, 40, null, null);
        for (int i = 0; i < 3; i++) {
            assertTrue(books.getBooks().get(i).getId() >= 1 && books.getBooks().get(i).getId() < 4);
        }
        assertEquals(31, books.getBooks().size());
    }

    @Test
    void getListOfRecentBooksAuthorized() throws ParseException {
        BookstoreUser user = new BookstoreUser();
        user.setId(1);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getListOfRecentBooksAuthorized(0, 40, "06.01.2016", "17.11.2018");
        assertEquals(13, books.getBooks().size());
    }

    @Test
    void getListOfRecentBooksAuthorizedWithoutMain() throws ParseException {
        BookstoreUser user = new BookstoreUser();
        user.setId(3);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getListOfRecentBooksAuthorized(0, 40, "06.01.2016", "17.11.2018");
        assertEquals(14, books.getBooks().size());
    }

    @Test
    void getListOfRecentBooksAuthorizedChangeFromAndTo() throws ParseException {
        BookstoreUser user = new BookstoreUser();
        user.setId(3);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getListOfRecentBooksAuthorized(0, 40, "07.01.2016", "16.11.2018");
        assertEquals(12, books.getBooks().size());
    }

    @Test
    void getListOfRecentBooksAuthorizedWithoutFrom() throws ParseException {
        BookstoreUser user = new BookstoreUser();
        user.setId(3);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getListOfRecentBooksAuthorized(0, 40, null, "16.11.2018");
        assertEquals(23, books.getBooks().size());
    }

    @Test
    void getListOfRecentBooksAuthorizedWithoutTo() throws ParseException {
        BookstoreUser user = new BookstoreUser();
        user.setId(3);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getListOfRecentBooksAuthorized(0, 40, "07.01.2016", null);
        assertEquals(20, books.getBooks().size());
    }

    @Test
    void getListOfRecentBooksAuthorizedWithoutFromAndTo() throws ParseException {
        BookstoreUser user = new BookstoreUser();
        user.setId(3);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getListOfRecentBooksAuthorized(0, 40, null, null);
        assertEquals(31, books.getBooks().size());
    }

    @Test
    void getListOfRecentBooksNotAuthorized() throws ParseException {
        BooksPageDto books = bookService.getListOfRecentBooksNotAuthorized(0, 40, "06.01.2016", "17.11.2018", "book-lnt-240", "book-rah-494");
        assertEquals(13, books.getBooks().size());
    }

    @Test
    void getListOfRecentBooksNotAuthorizedWithoutMain() throws ParseException {
        BooksPageDto books = bookService.getListOfRecentBooksNotAuthorized(0, 40, "06.01.2016", "17.11.2018", null, null);
        assertEquals(14, books.getBooks().size());
    }

    @Test
    void getListOfRecentBooksNotAuthorizedChangeFromAndTo() throws ParseException {
        BooksPageDto books = bookService.getListOfRecentBooksNotAuthorized(0, 40, "07.01.2016", "16.11.2018", null, null);
        assertEquals(12, books.getBooks().size());
    }

    @Test
    void getListOfRecentBooksNotAuthorizedWithoutFrom() throws ParseException {
        BooksPageDto books = bookService.getListOfRecentBooksNotAuthorized(0, 40, null, "16.11.2018", null, null);
        assertEquals(23, books.getBooks().size());
    }

    @Test
    void getListOfRecentBooksNotAuthorizedWithoutTo() throws ParseException {
        BooksPageDto books = bookService.getListOfRecentBooksNotAuthorized(0, 40, "07.01.2016", null, null, null);
        assertEquals(20, books.getBooks().size());
    }

    @Test
    void getListOfRecentBooksNotAuthorizedWithoutFromAndTo() throws ParseException {
        BooksPageDto books = bookService.getListOfRecentBooksNotAuthorized(0, 40, null, null, null, null);
        assertEquals(31, books.getBooks().size());
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
        assertEquals(2, books.getBooks().size());
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
        assertEquals(3, books.getBooks().size());
    }

    @Test
    void getBookFromGenreNotAuthorizedWithMain() {
        BooksPageDto books = bookService.getBookFromGenreNotAuthorized("genre-dfg-345", 0, 40, "book-lnt-240", "book-rah-494");
        for (int i = 0; i < books.getBooks().size(); i++) {
            assertTrue(books.getBooks().get(i).getId() > 15 && books.getBooks().get(i).getId() < 18);
        }
        assertEquals(2, books.getBooks().size());
    }

    @Test
    void getBookFromGenreNotAuthorizedWithoutMain() {
        BooksPageDto books = bookService.getBookFromGenreNotAuthorized("genre-dfg-345", 0, 40, null, null);
        for (int i = 0; i < books.getBooks().size(); i++) {
            assertTrue((books.getBooks().get(i).getId() > 15 && books.getBooks().get(i).getId() < 18) || books.getBooks().get(i).getId() == 1);
        }
        assertEquals(3, books.getBooks().size());
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
        assertEquals(3, books.getBooks().size());
    }

    @Test
    void getBooksFromAuthorNotAuthorizedWithMainNull() {
        BooksPageDto books = bookService.getBooksFromAuthorNotAuthorized("author-fzo-628", 0, 40, null, null);
        for (int i = 0; i < books.getBooks().size(); i++) {
            assertTrue((books.getBooks().get(i).getId() > 3 && books.getBooks().get(i).getId() < 6) || books.getBooks().get(i).getId() == 1);
        }
        assertEquals(3, books.getBooks().size());
    }

    @Test
    void getBooksFromAuthorNotAuthorizedWithMain() {
        BooksPageDto books = bookService.getBooksFromAuthorNotAuthorized("author-fzo-628", 0, 40, "book-lnt-240", "book-rah-494");
        for (int i = 0; i < books.getBooks().size(); i++) {
            assertTrue((books.getBooks().get(i).getId() > 3 && books.getBooks().get(i).getId() < 6) || books.getBooks().get(i).getId() == 1);
        }
        assertEquals(3, books.getBooks().size());
    }

    @Test
    void getBookFromSlug() throws BookstoreApiWrongParameterException {
        Book book = bookService.getBookFromSlug("book-lnt-240");
        assertEquals(1, (int) book.getId());

    }

    @Test
    void getBookFromTagNotAuthorizedWithMain() throws BookstoreApiWrongParameterException {
        BooksPageDto books = bookService.getBookFromTagNotAuthorized("tag-gdd-642", 0, 40, "book-lnt-240", "book-rah-494");
        for (int i = 0; i < books.getBooks().size(); i++) {
            assertTrue(books.getBooks().get(i).getId() > 9 && books.getBooks().get(i).getId() < 12);
        }
        assertEquals(2, books.getBooks().size());
    }

    @Test
    void getBookFromTagNotAuthorizedWithoutMain() throws BookstoreApiWrongParameterException {
        BooksPageDto books = bookService.getBookFromTagNotAuthorized("tag-gdd-642", 0, 40, null, null);
        for (int i = 0; i < books.getBooks().size(); i++) {
            assertTrue((books.getBooks().get(i).getId() > 9 && books.getBooks().get(i).getId() < 12) || books.getBooks().get(i).getId() == 1);
        }
        assertEquals(3, books.getBooks().size());
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
        assertEquals(2, books.getBooks().size());
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
        assertEquals(3, books.getBooks().size());
    }

    @Test
    void getBooksBySearchAuthorizedWithoutMain() throws BookstoreApiWrongParameterException {
        BookstoreUser user = new BookstoreUser();
        user.setId(3);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getBooksBySearchAuthorized("ill", 0, 40);
        assertEquals(2, books.getBooks().size());
    }

    @Test
    void getBooksBySearchAuthorizedWithMain() throws BookstoreApiWrongParameterException {
        BookstoreUser user = new BookstoreUser();
        user.setId(1);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getBooksBySearchAuthorized("ill", 0, 40);
        assertEquals(1, books.getBooks().size());
    }

    @Test
    void getBooksBySearchNotAuthorizedWithMain() throws BookstoreApiWrongParameterException {
        BooksPageDto books = bookService.getBooksBySearchNotAuthorized("ill", 0, 40, "book-lnt-240", "book-rah-494");
        assertEquals(1, books.getBooks().size());
    }

    @Test
    void getBooksBySearchNotAuthorizedWithoutMain() throws BookstoreApiWrongParameterException {
        BooksPageDto books = bookService.getBooksBySearchNotAuthorized("ill", 0, 40, null, null);
        assertEquals(2, books.getBooks().size());
    }

    @Test
    void getBooksInSlugs() {
        List<String> slugs = List.of("book-lnt-240", "book-rah-494");
        List<Book> books = bookService.getBooksInSlugs(slugs);
        books.forEach(e->assertTrue(slugs.contains(e.getSlug())));
        assertEquals(2, books.size());

    }

    @Test
    void getBooksInMyType() {
        BookstoreUser user = new BookstoreUser();
        user.setId(1);
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        List<Book> books = bookService.getBooksInMyType(1);
        assertEquals(1, books.size());
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
        assertEquals(2, books.getBooks().size());
    }

    @Test
    void getMyBooksInArchive() {
        BookstoreUser user = new BookstoreUser();
        user.setId(9);
        user.setEmail("belialpw2@bk.ru");
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getMyBooksInArchive(0, 40);
        assertEquals(1, books.getBooks().size());
    }

    @Test
    void getMyHistoryBooks() {
        BookstoreUser user = new BookstoreUser();
        user.setId(9);
        user.setEmail("belialpw2@bk.ru");
        Mockito.doReturn(user).when(userRegisterMock).getCurrentUser();
        BooksPageDto books = bookService.getMyBooksInArchive(0, 40);
        assertEquals(1, books.getBooks().size());
    }

}