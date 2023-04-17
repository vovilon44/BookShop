package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.CountCartKeptMainDto;
import com.example.MyBookShopApp.data.SearchWordDto;
import com.example.MyBookShopApp.data.services.Book2UserService;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalController {

    private final BookstoreUserRegister userRegister;
    private final Book2UserService book2UserService;

    private final LocaleResolver localeResolver;

    @Autowired
    public GlobalController(BookstoreUserRegister userRegister, Book2UserService book2UserService, LocaleResolver localeResolver) {
        this.userRegister = userRegister;
        this.book2UserService = book2UserService;
        this.localeResolver = localeResolver;

    }


    @ModelAttribute
    public void addAttributes(@CookieValue(name = "keptContents", required = false) String keptContents,
                              @CookieValue(name = "cartContents", required = false) String cartContents,
                              Model model, HttpServletRequest request) {
        if (request.isUserInRole("ROLE_USER")) {
            model.addAttribute("authorized", true);
            model.addAttribute("headerInfo", book2UserService.getHeaderInfoAuthUser());
        } else {
            CountCartKeptMainDto countCartKeptMainDto = new CountCartKeptMainDto();
            if (keptContents != null && !keptContents.equals("")){
                countCartKeptMainDto.setKept(keptContents.split("/").length);
            } else {
                countCartKeptMainDto.setKept(0);
            }
            if (cartContents != null && !cartContents.equals("")){
                countCartKeptMainDto.setCart(cartContents.split("/").length);
            } else {
                countCartKeptMainDto.setCart(0);
            }
            model.addAttribute("headerInfo", countCartKeptMainDto);
            model.addAttribute("authorized", false);
        }
        model.addAttribute("searchWordDto", new SearchWordDto());
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }


    @ModelAttribute("currentLocale")
    public String getCurrentLocale(HttpServletRequest request) {
        return localeResolver.resolveLocale(request).getLanguage().toString();
    }



}

