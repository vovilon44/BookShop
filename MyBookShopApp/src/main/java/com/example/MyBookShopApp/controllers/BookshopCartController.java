package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.data.BookRatingDto;
import com.example.MyBookShopApp.data.BookStatusDto;
import com.example.MyBookShopApp.data.SearchWordDto;
import com.example.MyBookShopApp.data.services.Book2UserService;
import com.example.MyBookShopApp.data.services.BookLike2UserService;
import com.example.MyBookShopApp.data.services.BookService;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
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
public class BookshopCartController {
    @ModelAttribute(name = "bookCart")
    public List<Book> bookCart() {
        return new ArrayList<>();
    }

    private final BookService bookService;
    private final BookLike2UserService bookLike2UserService;
    private final Book2UserService book2UserService;

    @Autowired
    public BookshopCartController(BookService bookService, BookLike2UserService bookLike2UserService, Book2UserService book2UserService) {
        this.bookService = bookService;
        this.bookLike2UserService = bookLike2UserService;
        this.book2UserService = book2UserService;
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
            if (books == null){
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
    public String handleRemoveBookFromCartRequest(@PathVariable("slug") String slug, @CookieValue(name = "cartContents", required = false) String cartContents, HttpServletResponse response, Model model) {
        if (cartContents != null && !cartContents.equals("")) {
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
    public String handleRemoveBookFromKeptRequest(@PathVariable("slug") String slug, @CookieValue(name = "keptContents", required = false) String keptContents, HttpServletResponse response, Model model) {
        if (keptContents != null && !keptContents.equals("")) {
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
            book2UserService.addBook2User(slug, bookStatusDto.getStatus().equals("KEPT") ? 2 : 1);
        } else {
            if (bookStatusDto.getStatus() != null){
                switch (bookStatusDto.getStatus()){
                    case ("KEPT"):
                        response.addCookie(book2UserService.addBook2UserFromCookie(slug, keptContents, "keptContents"));
                        model.addAttribute("isKeptEmpty");
                        break;
                    case ("CART"):
                        response.addCookie(book2UserService.addBook2UserFromCookie(slug, cartContents, "cartContents"));
                        model.addAttribute("isCartEmpty");
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
                    response.addCookie(book2UserService.addBook2UserFromCookie(slug, booksRating, "booksRating"));
                }
            }
        }
        return "redirect:/books/" + slug;
    }
}
