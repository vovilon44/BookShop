package com.example.MyBookShopApp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
public class BlackListService
{
    private BlackListRepository blackListRepository;

    @Autowired
    public BlackListService(BlackListRepository blackListRepository) {
        this.blackListRepository = blackListRepository;
    }

    public void addToken(String token) {
        if (token != null) {
            blackListRepository.save(new TokenEntity(token, new Date()));
        }
    }

    public boolean tokenIsBlackList(String token)
    {
        return !(blackListRepository.findByTokenKey(token) == null);
    }
}
