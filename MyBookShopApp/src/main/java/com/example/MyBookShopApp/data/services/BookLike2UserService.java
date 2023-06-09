package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.data.repositories.BookLike2UserRepository;
import com.example.MyBookShopApp.data.struct.book.links.BookLike2UserEntity;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class BookLike2UserService
{
    private BookLike2UserRepository bookLike2UserRepository;

    private BookService bookService;

    private  final BookstoreUserRegister userRegister;

    @Autowired
    public BookLike2UserService(BookLike2UserRepository bookLike2UserRepository, BookService bookService, BookstoreUserRegister userRegister) {
        this.bookLike2UserRepository = bookLike2UserRepository;
        this.bookService = bookService;
        this.userRegister = userRegister;
    }

    public boolean updateBookLike2UserLikeValue(String slug, Integer value) throws BookstoreApiWrongParameterException {
        BookLike2UserEntity bookLike2User = bookLike2UserRepository.findByBook_SlugAndUser(slug, userRegister.getCurrentUser());
        if (bookLike2User == null){
            bookLike2User = new BookLike2UserEntity();
            Book book = bookService.getBookFromSlug(slug);
            if (book == null){
                return false;
            }
            bookLike2User.setBook(book);
            bookLike2User.setUser(userRegister.getCurrentUser());
            bookLike2User.setPubDate(new Date());
            bookLike2User.setLikeValue(value);
            bookLike2UserRepository.save(bookLike2User);
        }
        bookLike2User.setLikeValue(value);
        bookLike2User.setPubDate(new Date());
        bookLike2UserRepository.save(bookLike2User);
        return true;
    }

    public BookLike2UserEntity getRatingBookForUser(String slug)
    {
            return bookLike2UserRepository.findByBook_SlugAndUser(slug, userRegister.getCurrentUser());


    }
}
