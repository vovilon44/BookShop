package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.data.services.Book2UserService;
import com.example.MyBookShopApp.data.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookshopCartController {
    @ModelAttribute(name = "bookCart")
    public List<Book> bookCart() {
        return new ArrayList<>();
    }

    private final BookService bookService;
    private final Book2UserService book2UserService;


    @Autowired
    public BookshopCartController(BookService bookService, Book2UserService book2UserService) {
        this.bookService = bookService;
        this.book2UserService = book2UserService;
    }

    @GetMapping("/postponed")
    public String handleKeptRequest(@CookieValue(value = "keptContents", required = false) String keptContents,
                                    @CookieValue(value = "token", required = false) String token, Model model) {
        if (token != null){
            List<Book> books = bookService.getBooksInMyType(1);
            if (books == null){
                model.addAttribute("isKeptEmpty", true);
            } else {
                model.addAttribute("isKeptEmpty", false);
                model.addAttribute("bookKept", books);
            }
        } else {
            if (keptContents == null || keptContents.equals("")) {
                model.addAttribute("isKeptEmpty", true);
                model.addAttribute("bookKept", new ArrayList<>());
            } else {
                model.addAttribute("isKeptEmpty", false);
                model.addAttribute("bookKept", bookService.getBooksInSlugs(Arrays.asList(keptContents.split("/"))));
            }
        }
        return "postponed";
    }

    @GetMapping("/cart")
    public String handleCartRequest(@CookieValue(value = "cartContents", required = false) String cartContents,
                                    @CookieValue(value = "token", required = false) String token, Model model) {
        if (token != null){
            List<Book> books = bookService.getBooksInMyType(2);
            if (books == null || books.size() == 0){
                model.addAttribute("isCartEmpty", true);
            } else {
                model.addAttribute("isCartEmpty", false);
                model.addAttribute("bookCart", books);
            }
        } else {
            if (cartContents == null || cartContents.equals("")) {
                model.addAttribute("isCartEmpty", true);
                model.addAttribute("bookCart", new ArrayList<>());
            } else {
                model.addAttribute("isCartEmpty", false);
                model.addAttribute("bookCart", bookService.getBooksInSlugs(Arrays.asList(cartContents.split("/"))));

            }
        }
        return "cart";
    }


    @GetMapping("/pay")
    public String handleOrder(@CookieValue(name = "token", required = false) String token, Model model) throws NoSuchAlgorithmException {
        if (book2UserService.payBooks()) {
            return "redirect:/books/cart";
        } else {
            return "redirect:/profile?result=false";
        }

    }

}
