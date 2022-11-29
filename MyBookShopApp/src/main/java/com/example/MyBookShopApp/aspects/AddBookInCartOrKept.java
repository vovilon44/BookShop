package com.example.MyBookShopApp.aspects;

import com.example.MyBookShopApp.data.BookStatusDto;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.StringJoiner;
import java.util.logging.Logger;

@Aspect
@Component
public class AddBookInCartOrKept
{


    @Around(value = "execution(* handleChangeBookStatus(..))")
    public void around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Logger.getLogger("CartKept..!!!").info("slug1: ");
        Object[] arguments = proceedingJoinPoint.getArgs();
        String slug = (String) arguments[0];
        String cartContents = (String) arguments[1];
        String keptContents = (String) arguments[2];
        BookStatusDto  bookStatusDto = (BookStatusDto) arguments[4];

        if (bookStatusDto.getStatus() != null){
            switch (bookStatusDto.getStatus()){
                case ("KEPT"):
                if (keptContents == null || keptContents.equals("")){
                    arguments[2] = "" + slug;
                } else if (!keptContents.contains(slug)){
                    StringJoiner stringJoiner = new StringJoiner("/");
                    stringJoiner.add(keptContents).add(slug);
                    arguments[2] = stringJoiner.toString();
                } break;
                case ("CART"):
                    if (cartContents == null || cartContents.equals("")){
                        arguments[1] = "" + slug;
                    } else if (!cartContents.contains(slug)){
                        StringJoiner stringJoiner = new StringJoiner("/");
                        stringJoiner.add(cartContents).add(slug);
                        arguments[1] = stringJoiner.toString();
                    } break;
            }
        }
        for (Object obj : arguments){
            Logger.getLogger("CartKept..").info("obj: " + obj);
        }
        proceedingJoinPoint.proceed(arguments);
    }
}
