package com.example.MyBookShopApp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearhController {

    @GetMapping("/search")
    public String search(){
        return "search/index";
    }
}
