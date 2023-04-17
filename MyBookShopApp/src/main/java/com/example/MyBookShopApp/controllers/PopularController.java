package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.data.BooksPageDto;
import com.example.MyBookShopApp.data.services.BookService;
import com.example.MyBookShopApp.data.SearchWordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class PopularController {





    private final BookService bookService;
    @Value("${books.pool.limit.size}")
    private Integer limit;
    @Value("${books.pool.offset.size}")
    private Integer offset;

    @Autowired
    public PopularController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto(){
        return new SearchWordDto();
    }

    @ModelAttribute("books")
    public BooksPageDto popularBooks(HttpServletRequest request, Model model,
                                     @CookieValue(name = "cartContents", required = false) String cartContents,
                                     @CookieValue(name = "keptContents", required = false) String keptContents){
        model.addAttribute("textForBlankBooks", "Популярные книги отсутствуют");
        if (request.isUserInRole("ROLE_USER")) {
            return bookService.getListOfPopularBooksAuthorized(offset, limit);
        } else {
            return bookService.getListOfPopularBooksNotAuthorized(offset, limit, cartContents, keptContents);
        }
    }


    @GetMapping("/popular")
    public String mainPage(){
        return "/books/popular";
    }



}
