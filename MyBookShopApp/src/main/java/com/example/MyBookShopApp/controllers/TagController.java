package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.TagWithRangDto;
import com.example.MyBookShopApp.data.services.BookService;
import com.example.MyBookShopApp.data.services.TagService;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TagController {
    private TagService tagService;
    private BookService bookService;
    @Value("${books.pool.limit.size}")
    private Integer limit;
    @Value("${books.pool.offset.size}")
    private Integer offset;

    @Autowired
    public TagController(TagService tagService, BookService bookService) {
        this.tagService = tagService;
        this.bookService = bookService;
    }

    @ModelAttribute("tagsWithRang")
    public List<TagWithRangDto> tags()
    {
        List<TagWithRangDto> list = new ArrayList<>(tagService.getAllTags());
        return list;
    }

    @GetMapping("/tags/{tagSlug}")
    public String booksForTagPage(Model model, HttpServletRequest request,
                                  @PathVariable String tagSlug,
                                  @CookieValue(name = "cartContents", required = false) String cartContents,
                                  @CookieValue(name = "keptContents", required = false) String keptContents) throws BookstoreApiWrongParameterException {
        model.addAttribute("textForBlankBooks", "Нет книг для данного тэга");
        model.addAttribute("tag", tagService.getTagEntityBySlug(tagSlug));
        if (request.isUserInRole("ROLE_USER")) {
            model.addAttribute("books", bookService.getBookFromTagAuthorized(tagSlug, offset, limit));
        } else {
            model.addAttribute("books", bookService.getBookFromTagNotAuthorized(tagSlug, offset, limit, cartContents, keptContents));
        }
        return "/tags/index";
    }
}
