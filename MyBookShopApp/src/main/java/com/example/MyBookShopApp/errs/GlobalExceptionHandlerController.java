package com.example.MyBookShopApp.errs;

import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.errs.EmptySearchException;
import com.example.MyBookShopApp.security.ContactConfirmationResponse;
import org.springframework.mail.MailException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.logging.Logger;

@ControllerAdvice
public class GlobalExceptionHandlerController
{
    @ExceptionHandler(EmptySearchException.class)
        public String handleEmptySearchException(EmptySearchException e, RedirectAttributes redirectAttributes){
        Logger.getLogger(this.getClass().getSimpleName()).warning(e.getLocalizedMessage());
        redirectAttributes.addFlashAttribute("searchError", e);
        return "redirect:/";
    }

    @ExceptionHandler(BookstoreApiWrongParameterException.class)
    public String handleEmptySearchException(BookstoreApiWrongParameterException e){
        Logger.getLogger(this.getClass().getSimpleName()).warning(e.getLocalizedMessage());
        return "redirect:/";
    }


    @ExceptionHandler(MailException.class)
    public String handleMailException(MailException e){
        Logger.getLogger(this.getClass().getSimpleName()).warning(e.getLocalizedMessage());
        return "redirect:/";
    }



}
