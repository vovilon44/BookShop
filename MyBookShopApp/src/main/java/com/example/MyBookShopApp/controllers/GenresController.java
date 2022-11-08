package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.BookService;
import com.example.MyBookShopApp.data.GenresDtoFirst;
import com.example.MyBookShopApp.data.GenresService;
import com.example.MyBookShopApp.data.SearchWordDto;
import com.example.MyBookShopApp.data.struct.genre.GenreEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class GenresController {

    private final GenresService genreService;
    private final BookService bookService;

    @Autowired
    public GenresController(GenresService genreService,BookService bookService) {

        this.genreService = genreService;
        this.bookService = bookService;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto(){
        return new SearchWordDto();
    }

    @GetMapping("/genres")
    public String mainPage(){
        return "genres/index";
    }

    @ModelAttribute("mainGenres")
    public List<GenresDtoFirst> mainGenres(){
        return genreService.getAllGenres();
    }


    @GetMapping("/genres/{slugGenre}")
    public String genrePage(@PathVariable String slugGenre, Model model){
        model.addAttribute("genre", genreService.getGenreEntityFromSlug(slugGenre));
        model.addAttribute("genreTree", genreService.getGenreTree(slugGenre));
        model.addAttribute("books", bookService.getBookFromGenre(slugGenre, 0, 20).getContent());
        return "genres/slug";
    }

}
