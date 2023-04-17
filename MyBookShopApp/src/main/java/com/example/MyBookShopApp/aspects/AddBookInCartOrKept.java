package com.example.MyBookShopApp.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AddBookInCartOrKept
{


//    @Around(value = "execution(* handleChangeBookStatus(..))")
//    public void around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

//        Object[] arguments = proceedingJoinPoint.getArgs();
//        String slug = (String) arguments[0];
//        String cartContents = (String) arguments[1];
//        String keptContents = (String) arguments[2];
//        BookStatusDto  bookStatusDto = (BookStatusDto) arguments[4];
//
//        if (bookStatusDto.getStatus() != null){
//            switch (bookStatusDto.getStatus()){
//                case ("KEPT"):
//                if (keptContents == null || keptContents.equals("")){
//                    arguments[2] = "" + slug;
//                } else if (!keptContents.contains(slug)){
//                    StringJoiner stringJoiner = new StringJoiner("/");
//                    stringJoiner.add(keptContents).add(slug);
//                    arguments[2] = stringJoiner.toString();
//                } break;
//                case ("CART"):
//                    if (cartContents == null || cartContents.equals("")){
//                        arguments[1] = "" + slug;
//                    } else if (!cartContents.contains(slug)){
//                        StringJoiner stringJoiner = new StringJoiner("/");
//                        stringJoiner.add(cartContents).add(slug);
//                        arguments[1] = stringJoiner.toString();
//                    } break;
//            }
//        }
//        proceedingJoinPoint.proceed(arguments);
//    }
}
