package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.BooksPageDto;
import com.example.MyBookShopApp.data.SearchWordDto;
import com.example.MyBookShopApp.data.TagWithRangDto;
import com.example.MyBookShopApp.data.services.BookService;
import com.example.MyBookShopApp.data.services.TagService;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.errs.EmptySearchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainPageController {

    private final BookService bookService;
    private final TagService tagService;
    @Value("${books.slider.limit.size}")
    private Integer sliderLimit;
    @Value("${books.slider.offset.size}")
    private Integer sliderOffset;
    @Value("${books.pool.limit.size}")
    private Integer poolLimit;
    @Value("${books.pool.offset.size}")
    private Integer poolOffset;




    @Autowired
    public MainPageController(BookService bookService, TagService tagService) {
        this.bookService = bookService;
        this.tagService = tagService;
    }



    @ModelAttribute("recommendedBooks")
    public BooksPageDto recommendedBooks(HttpServletRequest request, Model model,
                                         @CookieValue(name = "cartContents", required = false) String cartContents,
                                         @CookieValue(name = "keptContents", required = false) String keptContents,
                                         @CookieValue(name = "historyVisit", required = false) String historyVisit){

        model.addAttribute("textForBlankRecommendedBooks", "Рекомендуемых книг нет");
        if (request.isUserInRole("ROLE_USER")){
            return bookService.getPageOfRecommendedBooksAuthorized(sliderOffset, sliderLimit);
        } else {
            if (cartContents == null && keptContents == null && historyVisit == null){
                return new BooksPageDto();
//                return bookService.getPageOfRecommendedBooksWithoutMain(sliderOffset, sliderLimit);
            }
            return bookService.getPageOfRecommendedBooksNotAuthorized(sliderOffset, sliderLimit, cartContents, keptContents, historyVisit);
        }
    }

    @ModelAttribute("recentBooks")
    public BooksPageDto recentBooks(HttpServletRequest request, Model model,
                                  @CookieValue(name = "cartContents", required = false) String cartContents,
                                  @CookieValue(name = "keptContents", required = false) String keptContents) throws ParseException {
        model.addAttribute("textForBlankRecentBooks", "Новых книг нет");
        if (request.isUserInRole("ROLE_USER")) {
            return bookService.getListOfRecentBooksAuthorized(sliderOffset, sliderLimit, null, null);
        } else {
            return bookService.getListOfRecentBooksNotAuthorized(sliderOffset, sliderLimit, null, null, cartContents, keptContents);
        }
    }

    @ModelAttribute("popularBooks")
    public BooksPageDto popularBooks(HttpServletRequest request, Model model,
                                   @CookieValue(name = "cartContents", required = false) String cartContents,
                                   @CookieValue(name = "keptContents", required = false) String keptContents){
        model.addAttribute("textForBlankPopularBooks", "Популярных книг нет");
        if (request.isUserInRole("ROLE_USER")) {
            return bookService.getListOfPopularBooksAuthorized(sliderOffset, sliderLimit);
        } else {

            return bookService.getListOfPopularBooksNotAuthorized(sliderOffset, sliderLimit, cartContents, keptContents);
        }
    }

    @ModelAttribute("tagsWithRang")
    public List<TagWithRangDto> tags()
    {

        List<TagWithRangDto> list = new ArrayList<>(tagService.getAllTags());

        return list;
    }

    @GetMapping("/")
    public String mainPage(){
        return "index";
    }

    @GetMapping(value = {"/search", "/search/{searchWord}"})
    public String SearchResultsPage(Model model, HttpServletRequest request,
                                    @PathVariable(value = "searchWord", required = false) SearchWordDto searchWordDto,
                                    @CookieValue(name = "cartContents", required = false) String cartContents,
                                    @CookieValue(name = "keptContents", required = false) String keptContents) throws BookstoreApiWrongParameterException, EmptySearchException {
        if (searchWordDto != null) {
            model.addAttribute("searchWordDto", searchWordDto);
            if (request.isUserInRole("ROLE_USER")) {
                model.addAttribute("books", bookService.getBooksBySearchAuthorized(searchWordDto.getExample(), poolOffset, poolLimit));
            } else {
                model.addAttribute("books", bookService.getBooksBySearchNotAuthorized(searchWordDto.getExample(), poolOffset, poolLimit, cartContents, keptContents));
            }
            return "/search/index";
        } else {
            throw new EmptySearchException("Поисковый запрос не задан");
        }
    }


}
