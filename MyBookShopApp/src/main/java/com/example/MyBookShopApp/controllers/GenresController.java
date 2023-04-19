package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.services.BookService;
import com.example.MyBookShopApp.data.services.GenresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

@Controller
public class GenresController {

    private final GenresService genreService;
    private final BookService bookService;

    @Value("${books.pool.limit.size}")
    private Integer limit;
    @Value("${books.pool.offset.size}")
    private Integer offset;

    @Autowired
    public GenresController(GenresService genreService,BookService bookService) {

        this.genreService = genreService;
        this.bookService = bookService;
    }


    @ModelAttribute("mainGenres")

    public String mainGenres(Model model){
        return genreService.getElementsHtml(model.getAttribute("currentLocale").toString());
    }

    @GetMapping("/genres")
    public String mainPage(){
        return "genres/index";
    }



    @GetMapping("/genres/{slugGenre}")
    public String genrePage(@PathVariable String slugGenre, Model model, HttpServletRequest request,
                            @CookieValue(name = "cartContents", required = false) String cartContents,
                            @CookieValue(name = "keptContents", required = false) String keptContents){
        model.addAttribute("textForBlankBooks", "Нет книг для данного жанра");
        model.addAttribute("genre", genreService.getGenreEntityFromSlug(slugGenre));
        model.addAttribute("genreTree", genreService.getGenreTree(slugGenre));
        if (request.isUserInRole("ROLE_USER")) {
            model.addAttribute("books", bookService.getBookFromGenreAuthorized(slugGenre, offset, limit));
        } else {
            model.addAttribute("books", bookService.getBookFromGenreNotAuthorized(slugGenre, offset, limit, cartContents, keptContents));
        }
        return "genres/slug";
    }

}
