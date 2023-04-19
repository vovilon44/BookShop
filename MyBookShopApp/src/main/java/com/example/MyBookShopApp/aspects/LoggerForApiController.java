package com.example.MyBookShopApp.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.logging.Logger;

@Aspect
@Component
public class LoggerForApiController
{
    Logger logger = Logger.getLogger(this.getClass().getName());

    @Pointcut(value = "execution(* com.example.MyBookShopApp.controllers.BooksRestApiController..*(..))")
    public void pointcatForApiController(){}

    @Before("pointcatForApiController()")
    public void addLogBeforeRequest(JoinPoint joinPoint){
        logger.info("Try request " + joinPoint.toShortString());
        logger.info("Request param: " + Arrays.stream(joinPoint.getArgs()).reduce((x,y)->x + " "+ y).get());
    }

//    @Around("pointcatForApiController()")
//    public ResponseEntity<ApiResponse< BooksPageDto >> addLogAfterRequest(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//        ResponseEntity<ApiResponse< BooksPageDto >> res = (ResponseEntity<ApiResponse< BooksPageDto >>) proceedingJoinPoint.proceed();
//        logger.info("Request results " +  res.getBody().getData());
//        return res;
//    }
}
