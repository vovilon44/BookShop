package com.example.MyBookShopApp.data.repositories;

import com.example.MyBookShopApp.data.SmsCode;
import com.example.MyBookShopApp.data.struct.genre.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsCodeRepository extends JpaRepository<SmsCode, Long>
{
    public SmsCode findByCode(String Code);
}
