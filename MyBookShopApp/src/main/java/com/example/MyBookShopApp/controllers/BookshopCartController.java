package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.data.BookRepository;
import com.example.MyBookShopApp.data.SearchWordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

@Controller
@RequestMapping("/books")
public class BookshopCartController
{
    @ModelAttribute(name = "bookCart")
    public List<Book> bookCart(){
        return new ArrayList<>();
    }

    private final BookRepository bookRepository;

    @Autowired
    public BookshopCartController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto(){
        return new SearchWordDto();
    }

    @GetMapping("/postponed")
    public String handleKeptRequest(@CookieValue(value = "keptContents", required = false) String keptContents, Model model){
        if (keptContents == null || keptContents.equals("")){
            model.addAttribute("isKeptEmpty", true);
        } else {
            model.addAttribute("isKeptEmpty", false);
            keptContents = keptContents.startsWith("/") ? keptContents.substring(1) : keptContents;
            keptContents = keptContents.endsWith("/") ? keptContents.substring(0, keptContents.length() - 1) : keptContents;
            String[] cookieSlugs = keptContents.split("/");
            List<Book> booksFromCookieSlugs = bookRepository.findBooksBySlugIn(cookieSlugs);
            model.addAttribute("bookKept", booksFromCookieSlugs);

        }
        return "postponed";
    }

    @GetMapping("/cart")
    public String handleCartRequest(@CookieValue(value = "cartContents", required = false) String cartContents, Model model){
        if (cartContents == null || cartContents.equals("")){
            model.addAttribute("isCartEmpty", true);
        } else {
            model.addAttribute("isCartEmpty", false);
            cartContents = cartContents.startsWith("/") ? cartContents.substring(1) : cartContents;
            cartContents = cartContents.endsWith("/") ? cartContents.substring(0, cartContents.length() - 1) : cartContents;
            String[] cookieSlugs = cartContents.split("/");
            List<Book> booksFromCookieSlugs = bookRepository.findBooksBySlugIn(cookieSlugs);
            model.addAttribute("bookCart", booksFromCookieSlugs);

        }
        return "cart";
    }

    @PostMapping("/changeBookStatus/cart/remove/{slug}")
    public String handleRemoveBookFromCartRequest(@PathVariable("slug") String slug, @CookieValue(name = "cartContents", required = false) String cartContents, HttpServletResponse response, Model model){
        if (cartContents != null && !cartContents.equals("")){
            ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(cartContents.split("/")));
            cookieBooks.remove(slug);
            Cookie cookie = new Cookie("cartContents", String.join("/", cookieBooks));
            cookie.setPath("/books");
            response.addCookie(cookie);
            model.addAttribute("isCartEmpty", false);
        } else {
            model.addAttribute("isCartEmpty", true);
        }
        return "redirect:/books/cart";
    }

    @PostMapping("/changeBookStatus/kept/remove/{slug}")
    public String handleRemoveBookFromKeptRequest(@PathVariable("slug") String slug, @CookieValue(name = "keptContents", required = false) String keptContents, HttpServletResponse response, Model model){
        if (keptContents != null && !keptContents.equals("")){
            ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(keptContents.split("/")));
            cookieBooks.remove(slug);
            Cookie cookie = new Cookie("keptContents", String.join("/", cookieBooks));
            cookie.setPath("/books");
            response.addCookie(cookie);
            model.addAttribute("isKeptEmpty", false);
        } else {
            model.addAttribute("isKeptEmpty", true);
        }
        return "redirect:/books/postponed";
    }

    @PostMapping("/changeBookStatus/{slug}")
    public String handleChangeBookStatus(@PathVariable("slug") String slug, @CookieValue(name = "cartContents", required = false) String cartContents, @CookieValue(name = "keptContents", required = false) String keptContents, @CookieValue(name = "booksRating", required = false) String booksRating, @RequestBody String status,HttpServletResponse response, Model model){
        if (status.contains("value")){
            if (booksRating == null || booksRating.equals("")){
                String[] value = status.split("value=");
                Cookie cookie = new Cookie("booksRating", slug + "=" + value[1]);
                cookie.setPath("/books");
                response.addCookie(cookie);
            } else if (!booksRating.contains(slug)) {
                StringJoiner stringJoiner = new StringJoiner("/");
                String[] booksRatingArray = status.split("value=");
                stringJoiner.add(booksRating).add(slug + "=" + booksRatingArray[1]);
                Cookie cookie = new Cookie("booksRating", stringJoiner.toString());
                cookie.setPath("/books");
                response.addCookie(cookie);
            }
        }
        if (status.contains("KEPT")){
            if (keptContents == null || keptContents.equals("")) {
                Cookie cookie = new Cookie("keptContents", slug);
                cookie.setPath("/books");
                response.addCookie(cookie);
                model.addAttribute("isKeptEmpty", false);
            } else if (!keptContents.contains(slug)){
                StringJoiner stringJoiner = new StringJoiner("/");
                stringJoiner.add(keptContents).add(slug);
                Cookie cookie = new Cookie("keptContents", stringJoiner.toString());
                cookie.setPath("/books");
                response.addCookie(cookie);
                model.addAttribute("isCartEmpty", false);
            }
        } else if (status.contains("CART")) {
            if (cartContents == null || cartContents.equals("")) {
                Cookie cookie = new Cookie("cartContents", slug);
                cookie.setPath("/books");
                response.addCookie(cookie);
                model.addAttribute("isCartEmpty", false);
            } else if (!cartContents.contains(slug)) {
                StringJoiner stringJoiner = new StringJoiner("/");
                stringJoiner.add(cartContents).add(slug);
                Cookie cookie = new Cookie("cartContents", stringJoiner.toString());
                cookie.setPath("/books");
                response.addCookie(cookie);
                model.addAttribute("isCartEmpty", false);
            }
        }
        return "redirect:/books/" + slug;
    }
}
