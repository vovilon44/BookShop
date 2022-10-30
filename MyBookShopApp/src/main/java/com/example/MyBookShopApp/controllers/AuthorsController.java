package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.Author;
import com.example.MyBookShopApp.data.AuthorService;
import com.example.MyBookShopApp.data.BookService;
import com.example.MyBookShopApp.data.SearchWordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@Controller
public class AuthorsController {

    private final AuthorService authorService;
    private final BookService bookService;

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

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto(){
        return new SearchWordDto();
    }


    @GetMapping("/authors/{slugAuthor}")
    public String authorPage(@PathVariable String slugAuthor, Model model){
        int textLimit = 500;
        Author author = authorService.getAuthorBySlug(slugAuthor);
        String showText = "";
        String hideText = "";
        for(String text : author.getDescription().split("\\.")){
            if (showText.length() + text.length() < textLimit){
                showText+= text + ".";
            } else {
                hideText += text + ".";
            }
        }
        model.addAttribute("author", author);
        model.addAttribute("showText", showText);
        model.addAttribute("hideText", hideText);
        model.addAttribute("books",bookService.getBooksFromAuthor(slugAuthor, 0, 6));
        return "authors/slug";
    }

    @GetMapping("/books/author/{slugAuthor}")
    public String booksForAuthorPage(@PathVariable String slugAuthor, Model model){
        model.addAttribute("author", authorService.getAuthorBySlug(slugAuthor));
        model.addAttribute("books",bookService.getBooksFromAuthor(slugAuthor, 0, 20));
        return "books/author";
    }


}
