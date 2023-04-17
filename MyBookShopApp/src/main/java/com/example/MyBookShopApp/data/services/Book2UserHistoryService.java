package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.repositories.Book2UserHistoryRepository;
import com.example.MyBookShopApp.data.struct.book.links.Book2UserHistory;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class Book2UserHistoryService {

    private final Book2UserHistoryRepository book2UserHistoryRepository;
    private final BookService bookService;
    private final BookstoreUserRegister userRegister;

    @Autowired
    public Book2UserHistoryService(Book2UserHistoryRepository book2UserHistoryRepository, BookService bookService, BookstoreUserRegister userRegister) {
        this.book2UserHistoryRepository = book2UserHistoryRepository;
        this.bookService = bookService;
        this.userRegister = userRegister;
    }


    public void updateBook2UserHistory(String slugBook) throws BookstoreApiWrongParameterException {
        Book2UserHistory book2UserHistory = book2UserHistoryRepository.findByBook_SlugAndUser(slugBook, userRegister.getCurrentUser());
        if (book2UserHistory != null){
            book2UserHistory.setTime(LocalDate.now());
            book2UserHistoryRepository.save(book2UserHistory);
        } else {
            book2UserHistory = new Book2UserHistory();
            book2UserHistory.setBook(bookService.getBookFromSlug(slugBook));
            book2UserHistory.setUser(userRegister.getCurrentUser());
            book2UserHistory.setTime(LocalDate.now());
            book2UserHistoryRepository.save(book2UserHistory);
        }
    }
}
