package com.example.MyBookShopApp.data.repositories;

import com.example.MyBookShopApp.data.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Integer>
{
    List<TransactionEntity> findAllByUser_EmailIs(String email);
}
