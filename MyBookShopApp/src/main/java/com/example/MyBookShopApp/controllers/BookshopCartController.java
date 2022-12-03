package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.data.BookRatingDto;
import com.example.MyBookShopApp.data.BookStatusDto;
import com.example.MyBookShopApp.data.SearchWordDto;
import com.example.MyBookShopApp.data.services.Book2UserService;
import com.example.MyBookShopApp.data.services.BookLike2UserService;
import com.example.MyBookShopApp.data.services.BookService;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

@Controller
@RequestMapping("/books")
public class BookshopCartController {
    @ModelAttribute(name = "bookCart")
    public List<Book> bookCart() {
        return new ArrayList<>();
    }

    private final BookService bookService;
    private final BookLike2UserService bookLike2UserService;
    private final Book2UserService book2UserService;
    private final BookstoreUserRegister bookstoreUserRegister;


    @Autowired
    public BookshopCartController(BookService bookService, BookLike2UserService bookLike2UserService, Book2UserService book2UserService, BookstoreUserRegister bookstoreUserRegister) {
        this.bookService = bookService;
        this.bookLike2UserService = bookLike2UserService;
        this.book2UserService = book2UserService;
        this.bookstoreUserRegister = bookstoreUserRegister;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }


    @GetMapping("/postponed")
    public String handleKeptRequest(@CookieValue(value = "keptContents", required = false) String keptContents, @CookieValue(value = "token", required = false) String token, Model model) {
        if (token != null){
            List<Book> books = bookService.getBooksInCart(1);
            if (books == null){
                model.addAttribute("isKeptEmpty", true);
            } else {
                model.addAttribute("isKeptEmpty", false);
                model.addAttribute("bookKept", books);
            }
        } else {
            if (keptContents == null || keptContents.equals("")) {
                model.addAttribute("isKeptEmpty", true);
            } else {
                model.addAttribute("isKeptEmpty", false);
                model.addAttribute("bookKept", bookService.getBooksInSlugs(keptContents));
            }
        }
        return "postponed";
    }

    @GetMapping("/cart")
    public String handleCartRequest(@CookieValue(value = "cartContents", required = false) String cartContents, @CookieValue(value = "token", required = false) String token, Model model) {
        if (token != null){
            List<Book> books = bookService.getBooksInCart(2);
            if (books == null || books.size() == 0){
                model.addAttribute("isCartEmpty", true);
            } else {
                model.addAttribute("isCartEmpty", false);
                model.addAttribute("bookCart", books);
            }
        } else {
            if (cartContents == null || cartContents.equals("")) {
                model.addAttribute("isCartEmpty", true);
            } else {
                model.addAttribute("isCartEmpty", false);
                model.addAttribute("bookCart", bookService.getBooksInSlugs(cartContents));
            }
        }
        return "cart";
    }

    @PostMapping("/changeBookStatus/cart/remove/{slug}")
    public String handleRemoveBookFromCartRequest(@PathVariable("slug") String slug,
                                                  @CookieValue(name = "cartContents", required = false) String cartContents,
                                                  @CookieValue(name = "token", required = false) String token,
                                                  HttpServletResponse response, Model model) throws BookstoreApiWrongParameterException
    {
        if (token != null){
            book2UserService.removeBook2User(slug, 2);
        } else if (cartContents != null && !cartContents.equals("")) {
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
    public String handleRemoveBookFromKeptRequest(@PathVariable("slug") String slug,
                                                  @CookieValue(name = "keptContents", required = false) String keptContents,
                                                  @CookieValue(name = "token", required = false) String token,
                                                  HttpServletResponse response, Model model) throws BookstoreApiWrongParameterException
    {
        if (token != null){
            book2UserService.removeBook2User(slug, 1);
        } else if (keptContents != null && !keptContents.equals("")) {
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
    public String handleChangeBookStatus(@PathVariable("slug") String slug,
                                         @CookieValue(name = "cartContents", required = false) String cartContents,
                                         @CookieValue(name = "keptContents", required = false) String keptContents,
                                         @CookieValue(name = "token", required = false) String token,
                                         @RequestBody BookStatusDto bookStatusDto, HttpServletResponse response, Model model) throws BookstoreApiWrongParameterException {
        if (token != null && bookStatusDto.getStatus() != null && bookStatusDto.getStatus() != "") {
            book2UserService.addBook2User(slug, bookStatusDto.getStatus().equals("KEPT") ? 1 : 2);
        } else {
            if (bookStatusDto.getStatus() != null){
                switch (bookStatusDto.getStatus()){
                    case ("KEPT"):
                        if (keptContents == null || keptContents.equals("")){
                            Cookie cookie = new Cookie("keptContents", slug);
                            cookie.setPath("/books");
                            response.addCookie(cookie);
                            model.addAttribute("isKeptEmpty", false);
                        } else if (!keptContents.contains(slug)) {
                            StringJoiner stringJoiner = new StringJoiner("/");
                            stringJoiner.add(keptContents).add(slug);
                            Cookie cookie = new Cookie("keptContents", stringJoiner.toString());
                            cookie.setPath("/books");
                            response.addCookie(cookie);
                            model.addAttribute("isKeptEmpty", false);
                        }
                        break;
                        case ("CART"):
                            if (cartContents == null || cartContents.equals("")){
                                Cookie cookie = new Cookie("cartContents", slug);
                                cookie.setPath("/books");
                                response.addCookie(cookie);
                                model.addAttribute("isCartEmpty", false);
                            } else if (!cartContents.contains(slug)){
                                StringJoiner stringJoiner = new StringJoiner("/");
                                stringJoiner.add(cartContents).add(slug);
                                Cookie cookie = new Cookie("cartContents", stringJoiner.toString());
                                cookie.setPath("/books");
                                response.addCookie(cookie);
                                model.addAttribute("isCartEmpty", false);
                            }
                        break;

                }
            }
        }
        return "redirect:/books/" + slug;

    }


    @PostMapping("/changeBookStatus/rating/{slug}")
    public String handleChangeBookRating(@PathVariable("slug") String slug,
                                         @CookieValue(name = "booksRating", required = false) String booksRating,
                                         @CookieValue(name = "token", required = false) String token,
                                         @RequestBody BookRatingDto bookRatingDto, HttpServletResponse response, Model model) throws BookstoreApiWrongParameterException {
        if (token != null) {
            if (bookRatingDto.getValue() != null) {
                bookLike2UserService.updateBookLike2UserLikeValue(slug, bookRatingDto.getValue());
            }
        } else {
            if (bookRatingDto.getValue() != null) {
                if (booksRating != null && booksRating.contains(slug)) {
                    StringJoiner stringJoiner = new StringJoiner("/");
                    for (String rating : booksRating.split("/")) {
                        if (rating.contains(slug)) {
                            stringJoiner.add(slug + "=" + bookRatingDto.getValue());
                        } else {
                            stringJoiner.add(rating);
                        }
                    }
                    Cookie cookie = new Cookie("booksRating", stringJoiner.toString());
                    cookie.setPath("/books");
                    response.addCookie(cookie);

                } else {
                    if (booksRating == null || booksRating.equals("")){
                        Cookie cookie = new Cookie("booksRating", slug + "=" + bookRatingDto.getValue());
                        cookie.setPath("/books");
                        response.addCookie(cookie);
                    } else if (!booksRating.contains(slug)) {
                        StringJoiner stringJoiner = new StringJoiner("/");
                        stringJoiner.add(booksRating).add(slug + "=" + bookRatingDto.getValue());
                        Cookie cookie = new Cookie("booksRating", stringJoiner.toString());
                        cookie.setPath("/books");
                        response.addCookie(cookie);
                    }
                }
            }
        }
        return "redirect:/books/" + slug;
    }

    @GetMapping("/pay")
    public String handleOrder(@CookieValue(name = "token", required = false) String token, Model model) throws NoSuchAlgorithmException {
        if (book2UserService.payBooks()) {
            return "redirect:/books/cart";
        } else {
            return "redirect:/profile";
        }

    }

}
