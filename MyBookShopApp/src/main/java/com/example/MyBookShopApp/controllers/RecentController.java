package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.BooksPageDto;
import com.example.MyBookShopApp.data.SearchWordDto;
import com.example.MyBookShopApp.data.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class RecentController {

    private final BookService bookService;
    @Value("${books.pool.limit.size}")
    private Integer limit;
    @Value("${books.pool.offset.size}")
    private Integer offset;

    @Autowired
    public RecentController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("books")
    public BooksPageDto recommendedBooks(HttpServletRequest request, Model model,
                                         @CookieValue(name = "cartContents", required = false) String cartContents,
                                         @CookieValue(name = "keptContents", required = false) String keptContents) throws ParseException {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        model.addAttribute("textForBlankBooks", "Отсутсвуют книги для выбранного интервала");
        if (request.isUserInRole("ROLE_USER")) {
            return bookService.getListOfRecentBooksAuthorized(offset, limit, LocalDate.now().minusMonths(1).format(df), LocalDate.now().format(df));
        } else {
            return bookService.getListOfRecentBooksNotAuthorized(offset, limit, LocalDate.now().minusMonths(1).format(df), LocalDate.now().format(df), cartContents, keptContents);
        }
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto(){
        return new SearchWordDto();
    }


    @GetMapping("/recent")
    public String mainPage() {
        return "books/recent";
    }




}
