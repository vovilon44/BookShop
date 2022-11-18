package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.data.services.BookService;
import com.example.MyBookShopApp.data.SearchWordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
public class PopularController {

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto(){
        return new SearchWordDto();
    }


    private final BookService bookService;

    @Autowired
    public PopularController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("books")
    public List<Book> popularBooks(){
        return bookService.getListOfPopularBooks(0,20);
    }


    @GetMapping("/popular")
    public String mainPage(){
        return "/books/popular";
    }



}
