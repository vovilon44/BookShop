package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.BookStatusDto;
import com.example.MyBookShopApp.data.repositories.Book2UserRepository;
import com.example.MyBookShopApp.data.services.Book2UserService;
import com.example.MyBookShopApp.data.services.BookService;
import com.example.MyBookShopApp.data.struct.book.links.Book2UserEntity;
import com.example.MyBookShopApp.security.BookstoreUserDetailsService;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import com.example.MyBookShopApp.security.ContactConfirmationPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
class BookshopCartControllerTest {

    private final MockMvc mockMvc;
    private BookStatusDto bookStatusDto;
    private final ObjectMapper objectMapper;

    private final ContactConfirmationPayload emailPayload;
    private final BookstoreUserRegister bookstoreUserRegister;
    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final BookService bookService;
    private final Book2UserService book2UserService;
    private final Book2UserRepository book2UserRepository;

    @Autowired
    public BookshopCartControllerTest(MockMvc mockMvc, ObjectMapper objectMapper, BookstoreUserRegister bookstoreUserRegister, BookstoreUserDetailsService bookstoreUserDetailsService, BookService bookService, Book2UserService book2UserService, Book2UserRepository book2UserRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.bookstoreUserRegister = bookstoreUserRegister;
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.bookService = bookService;
        this.book2UserService = book2UserService;
        this.book2UserRepository = book2UserRepository;
        bookStatusDto = new BookStatusDto();
        emailPayload = new ContactConfirmationPayload();
        emailPayload.setContact("lcodner1@email.ru");
        emailPayload.setCode("332233");
    }

//    @BeforeEach
//    void setUp() {
//        bookStatusDto.setBookId("book-yts-780");
//        bookStatusDto.setStatus("CART");
//    }


    @Test
    public void addBookInBlankCartNotAuthorized() throws Exception {
        mockMvc.perform(post("/books/changeBookStatus/book-yts-780")
                        .content(objectMapper.writeValueAsString(bookStatusDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(cookie().value("cartContents", "book-yts-780"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/book-yts-780"));
    }

    @Test
    public void addBookInFilledCartNotAuthorized() throws Exception {
        mockMvc.perform(post("/books/changeBookStatus/book-yts-780")
                        .cookie(new Cookie("cartContents", "book-kvg-186/book-vsh-475"))
                        .content(objectMapper.writeValueAsString(bookStatusDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(cookie().value("cartContents", "book-kvg-186/book-vsh-475/book-yts-780"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/book-yts-780"));

    }

//    @Test
//    public void addBookInCartAuthorized() throws Exception {
//        Book2UserEntity book2User = book2UserService.getBook2User("book-yts-780");
//        if (book2User != null){
//            book2UserRepository.delete(book2User);
//        }
//        mockMvc.perform(post("/books/changeBookStatus/book-yts-780")
//                        .cookie(new Cookie("token", bookstoreUserRegister.jwtLogin(emailPayload).getResult()))
//                        .content(objectMapper.writeValueAsString(bookStatusDto))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/books/book-yts-780"));
//        Book2UserEntity book2UserTest = book2UserService.getBook2User("book-yts-780");
//        assertTrue(book2UserTest != null);
//        if (book2UserTest != null) {
//            book2UserRepository.delete(book2UserTest);
//        }
//    }

    @Test
    public void removeBookFromCartNotAuthorized() throws Exception {
        mockMvc.perform(post("/books/changeBookStatus/cart/remove/book-yts-780")
                        .cookie(new Cookie("cartContents", "book-kvg-186/book-vsh-475/book-yts-780")))
                .andDo(print())
                .andExpect(cookie().value("cartContents", "book-kvg-186/book-vsh-475"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/cart"));
    }

//    @Test
//    public void removeBookFromCartAuthorized() throws Exception {
//        Book2UserEntity book2User = new Book2UserEntity();
//        book2User.setBook(bookService.getBookFromSlug("book-yts-780"));
//        book2User.setTime(LocalDate.now());
//        book2User.setUser(bookstoreUserDetailsService.loadUserByUsername("lcodner1@email.ru").getBookstoreUser());
//        book2User.setTypeId(2);
//        book2UserRepository.save(book2User);
//
//        mockMvc.perform(post("/books/changeBookStatus/cart/remove/book-yts-780")
//                        .cookie(new Cookie("token", bookstoreUserRegister.jwtLogin(emailPayload).getResult())))
//                .andDo(print())
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/books/cart"));
//        assertTrue(book2UserService.getBook2User("book-yts-780") == null);
//    }


}