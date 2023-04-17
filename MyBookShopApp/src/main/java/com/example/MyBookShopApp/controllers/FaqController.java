package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.services.FaqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FaqController {


    private final FaqService faqService;

    @Autowired
    public FaqController(FaqService faqService) {
        this.faqService = faqService;
    }

    @GetMapping("/faq")
    public String faq(Model model){
        model.addAttribute("questions",  faqService.getAllFaqEntities());
        return "faq";
    }
}
