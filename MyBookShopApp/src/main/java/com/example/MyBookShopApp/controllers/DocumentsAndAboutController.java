package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DocumentsAndAboutController {

private final DocumentService documentService;

@Autowired
    public DocumentsAndAboutController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/documents")
    public String documents(Model model){
        model.addAttribute("documents", documentService.getAllDocuments());
        return "documents/index";
    }

    @GetMapping("/documents/{slug}")
    public String documents(@PathVariable("slug") String slug, Model model){
        model.addAttribute("document", documentService.getDocumentBySlug(slug));
        return "documents/slug";
    }

    @GetMapping("/about")
    public String about(){
        return "about";
    }
}
