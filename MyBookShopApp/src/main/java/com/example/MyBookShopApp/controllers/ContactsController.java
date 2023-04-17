package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.services.MessageService;
import com.example.MyBookShopApp.data.struct.other.MessageEntity;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

@Controller
public class ContactsController {

    private final MessageService messageService;
    private final BookstoreUserRegister userRegister;

    @Autowired
    public ContactsController(MessageService messageService, BookstoreUserRegister userRegister) {
        this.messageService = messageService;
        this.userRegister = userRegister;
    }

    @GetMapping("/contacts")
    public String contacts(Model model){
         model.addAttribute("message", new MessageEntity());

        return "contacts";
    }

    @PostMapping("/contacts")
    public String getContacts(HttpServletRequest request, MessageEntity message, Model model) {
        if (request.isUserInRole("ROLE_USER")) {
            if (message.getSubject() != null && message.getText() != null && !message.getSubject().trim().equals("") && !message.getText().trim().equals("")) {
                message.setUser(userRegister.getCurrentUser());
                message.setTime(LocalDate.now());
                messageService.saveContactAuthorized(message);
                model.addAttribute("resultSuccess", true);
            } else {
                model.addAttribute("resultSuccess", false);
            }
        } else {
            if (message.getSubject() != null && message.getText() != null && message.getEmail() != null && message.getName() != null &&
                    !message.getSubject().trim().equals("") && !message.getText().trim().equals("") && !message.getEmail().trim().equals("") && !message.getName().trim().equals("")) {
                message.setTime(LocalDate.now());
                messageService.saveContactAuthorized(message);
                model.addAttribute("resultSuccess", true);
            } else {
                model.addAttribute("resultSuccess", false);
            }
        }
        model.addAttribute("message", message);
        return "contacts";
    }
}
