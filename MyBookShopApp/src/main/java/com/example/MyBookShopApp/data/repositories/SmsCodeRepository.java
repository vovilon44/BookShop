package com.example.MyBookShopApp.data.repositories;

import com.example.MyBookShopApp.data.CodesFromUsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsCodeRepository extends JpaRepository<CodesFromUsersEntity, Long>
{
    public CodesFromUsersEntity findByContact(String contact);
}
