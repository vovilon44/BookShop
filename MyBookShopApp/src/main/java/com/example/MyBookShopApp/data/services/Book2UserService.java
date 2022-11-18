package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.repositories.Book2UserRepository;
import com.example.MyBookShopApp.data.struct.book.links.Book2UserEntity;
import com.example.MyBookShopApp.data.struct.book.links.BookLike2UserEntity;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import java.time.LocalDate;
import java.util.Date;
import java.util.StringJoiner;

@Service
public class Book2UserService
{
    private final Book2UserRepository book2UserRepository;

    private BookService bookService;

    private  final BookstoreUserRegister userRegister;

    @Autowired
    public Book2UserService(Book2UserRepository book2UserRepository, BookService bookService, BookstoreUserRegister userRegister) {
        this.book2UserRepository = book2UserRepository;
        this.bookService = bookService;
        this.userRegister = userRegister;
    }

    public void addBook2User(String slug,Integer type) throws BookstoreApiWrongParameterException {
        Book2UserEntity book2User = book2UserRepository.findBook2UserEntityByBook_SlugAndUser_EmailAndTypeId(slug, userRegister.getCurrentUser().getEmail(), type);
        if (book2User == null){
            book2User = new Book2UserEntity();
            book2User.setBook(bookService.getBookFromSlug(slug));
            book2User.setUser(userRegister.getCurrentUser());
            book2User.setTime(LocalDate.now());
            book2User.setTypeId(type);
            book2UserRepository.save(book2User);
        }

    }

    public Cookie addBook2UserFromCookie(String slug, String content,String cookieName){
        if (content == null || content.equals("")){
            Cookie cookie = new Cookie(cookieName, slug);
            cookie.setPath("/books");
            return cookie;
        } else if (!content.contains(slug)){
            StringJoiner stringJoiner = new StringJoiner("/");
            stringJoiner.add(content).add(slug);
            Cookie cookie = new Cookie(cookieName, stringJoiner.toString());
            cookie.setPath("/books");
            return cookie;
        } else {
            return new Cookie(cookieName, content);
        }
    }


}
