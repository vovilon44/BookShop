package com.example.MyBookShopApp.security;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BlackListRepository extends JpaRepository<TokenEntity, Integer>
{
    TokenEntity findByTokenKey(String token);
}
