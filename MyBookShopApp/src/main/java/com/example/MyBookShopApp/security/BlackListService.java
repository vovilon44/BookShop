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

    public void setToken(HttpServletRequest request){
        if (request.getCookies()!=null){
            for (Cookie cookie : request.getCookies()){
                if (cookie.getName().equals("token")){
                    TokenEntity tokenEntity = new TokenEntity();
                    tokenEntity.setTokenKey(cookie.getValue());
                    tokenEntity.setDate(new Date());
                    blackListRepository.save(tokenEntity);
                }

            }
        }
    }

    public boolean tokenIsBlackList(String token)
    {
        return !(blackListRepository.findByTokenKey(token) == null);
    }
}
