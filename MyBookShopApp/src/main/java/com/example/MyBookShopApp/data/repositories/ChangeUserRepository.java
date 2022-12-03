package com.example.MyBookShopApp.data.repositories;

import com.example.MyBookShopApp.data.ChangeUserEntity;
import com.example.MyBookShopApp.data.struct.book.review.BookReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeUserRepository extends JpaRepository<ChangeUserEntity, Integer>
{
    ChangeUserEntity findByCodeIs(String code);
}
