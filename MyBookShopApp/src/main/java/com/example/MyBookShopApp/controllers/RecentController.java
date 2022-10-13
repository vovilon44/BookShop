package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.AuthorService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RecentController {
    @GetMapping("/recent")
    public String mainPage(){
        return "books/recent";
    }
}
