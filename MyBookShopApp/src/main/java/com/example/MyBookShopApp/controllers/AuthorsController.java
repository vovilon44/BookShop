package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.Author;
import com.example.MyBookShopApp.data.services.AuthorService;
import com.example.MyBookShopApp.data.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class AuthorsController {

    private final AuthorService authorService;
    private final BookService bookService;
    @Value("${books.slider.limit.size}")
    private Integer limitSlider;
    @Value("${books.slider.offset.size}")
    private Integer offsetSlider;
    @Value("${books.pool.limit.size}")
    private Integer limitPool;
    @Value("${books.pool.offset.size}")
    private Integer offsetPool;

    @Autowired
    public AuthorsController(AuthorService authorService, BookService bookService) {
        this.authorService = authorService;
        this.bookService = bookService;
    }

    @ModelAttribute("authorsMap")
    public Map<String,List<Author>> authorsMap(){
        return authorService.getAuthorsMap();
    }

    @GetMapping("/authors")
    public String authorsPage(){
        return "/authors/index";
    }

    @GetMapping("/authors/{slugAuthor}")
    public String authorPage(@PathVariable String slugAuthor, Model model, HttpServletRequest request,
                             @CookieValue(name = "cartContents", required = false) String cartContents,
                             @CookieValue(name = "keptContents", required = false) String keptContents){
        model.addAttribute("author", authorService.getAuthorBySlug(slugAuthor));
        model.addAttribute("textForBlankBooks", "Нет новых книг для данного автора");
        if (request.isUserInRole("ROLE_USER")) {
            model.addAttribute("books",bookService.getBooksFromAuthorAuthorized(slugAuthor, offsetSlider, limitSlider));
        } else {
            model.addAttribute("books",bookService.getBooksFromAuthorNotAuthorized(slugAuthor, offsetSlider, limitSlider, cartContents, keptContents));
        }

        return "authors/slug";
    }

    @GetMapping("/books/author/{slugAuthor}")
    public String booksForAuthorPage(@PathVariable String slugAuthor, Model model,HttpServletRequest request,
                                     @CookieValue(name = "cartContents", required = false) String cartContents,
                                     @CookieValue(name = "keptContents", required = false) String keptContents){
        model.addAttribute("author", authorService.getAuthorBySlug(slugAuthor));
        model.addAttribute("textForBlankBooks", "Нет новых книг для данного автора");
        if (request.isUserInRole("ROLE_USER")) {
            model.addAttribute("books",bookService.getBooksFromAuthorAuthorized(slugAuthor, offsetPool, limitPool));
        } else {
            model.addAttribute("books",bookService.getBooksFromAuthorNotAuthorized(slugAuthor, offsetPool, limitPool, cartContents, keptContents));
        }
        return "books/author";
    }


}
