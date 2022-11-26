package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.data.SearchWordDto;
import com.example.MyBookShopApp.data.TagWithRangDto;
import com.example.MyBookShopApp.data.services.BookService;
import com.example.MyBookShopApp.data.services.TagService;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.errs.EmptySearchException;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainPageController {

    private final BookService bookService;
    private final TagService tagService;
    private final JWTUtil jwtUtil;




    @Autowired
    public MainPageController(BookService bookService, TagService tagService, JWTUtil jwtUtil) {
        this.bookService = bookService;
        this.tagService = tagService;
        this.jwtUtil = jwtUtil;
    }



    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto(){
        return new SearchWordDto();
    }

    @ModelAttribute("books")
    public List<Book> searchResults(){
        return new ArrayList<>();
    }


    @ModelAttribute("recommendedBooks")
    public List<Book> recommendedBooks(@CookieValue(value = "token", required = false) String token, Model model){
        if (token != null && !jwtUtil.isTokenExpired(token)){

            return bookService.getPageOfRecommendedBooksAuthorized(0, 6, "lcodner1@email.ru");
        }
        return bookService.getPageOfRecommendedBooks(0,6);
    }

    @ModelAttribute("recentBooks")
    public List<Book> recentBooks(){
        return bookService.getListOfRecentBooksWithoutDate(0,6);
    }

    @ModelAttribute("popularBooks")
    public List<Book> popularBooks(){
        return bookService.getListOfPopularBooks(0,6);
    }

    @ModelAttribute("tagsWithRang")
    public List<TagWithRangDto> tags()
    {
        ArrayList<TagWithRangDto> list = new ArrayList<>(tagService.getAllTags());
        return list;
    }

    @GetMapping("/")
    public String mainPage(){
        return "index";
    }

    @GetMapping(value = {"/search", "/search/{searchWord}"})
    public String SearchResultsPage(@PathVariable(value = "searchWord", required = false) SearchWordDto searchWordDto, Model model) throws BookstoreApiWrongParameterException, EmptySearchException {
        if (searchWordDto != null) {
            model.addAttribute("searchWordDto", searchWordDto);
            model.addAttribute("books", bookService.getBooksBySearch(searchWordDto.getExample(), 0, 20).getContent());
            return "/search/index";
        } else {
            throw new EmptySearchException("Поиск по null невозможен");
        }
    }


}
