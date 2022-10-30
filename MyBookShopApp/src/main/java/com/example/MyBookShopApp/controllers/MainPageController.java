package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Controller
public class MainPageController {

    private final BookService bookService;
    private final TagService tagService;



    @Autowired
    public MainPageController(BookService bookService, TagService tagService) {
        this.bookService = bookService;
        this.tagService = tagService;
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
    public List<Book> recommendedBooks(){
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
    public String mainPage(Model model){
        Logger.getLogger("MainPageController").info("recentBooks " + model.getAttribute("recentBooks"));
        return "index";
    }

    @GetMapping(value = {"/search", "/search/{searchWord}"})
    public String SearchResultsPage(@PathVariable(value = "searchWord", required = false) SearchWordDto searchWordDto, Model model){
        model.addAttribute("searchWordDto", searchWordDto);
        model.addAttribute("books", bookService.getBooksBySearch(searchWordDto.getExample(), 0, 20));
        return "/search/index";
    }


}
