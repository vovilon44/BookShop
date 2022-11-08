package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.BookService;
import com.example.MyBookShopApp.data.SearchWordDto;
import com.example.MyBookShopApp.data.TagService;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;

import java.util.logging.Logger;

@Controller
public class TagController
{
    private TagService tagService;
    private BookService bookService;

    @Autowired
    public TagController(TagService tagService, BookService bookService) {
        this.tagService = tagService;
        this.bookService = bookService;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto(){
        return new SearchWordDto();
    }
    @GetMapping("/tag/{tagSlug}")
    public String booksForTagPage(@PathVariable String tagSlug, Model model) throws BookstoreApiWrongParameterException {
        model.addAttribute("tag", tagService.getTagEntityBySlug(tagSlug)); // получаем интересующий нас тэг и добавляем в модель
        model.addAttribute("books", bookService.getBookFromTag(tagSlug, 0, 20).getContent()); // получаем книги для тэга и добавляем в модель
        Logger.getLogger("TagController").info("direct - /tag/{tagSlug}"); // выводим в лог текст(ендпоинт)
        Logger.getLogger("TagController").info("attribute tag: " + model.getAttribute("tag")); // проверяем тэг
        Logger.getLogger("TagController").info("attribute books: " + model.getAttribute("books")); // проверяем книги
        return "/tags/index";
    }
}
