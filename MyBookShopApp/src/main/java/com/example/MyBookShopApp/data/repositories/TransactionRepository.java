package com.example.MyBookShopApp.data.repositories;

import com.example.MyBookShopApp.data.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Integer>
{
    Page<TransactionEntity> findAllByUser_IdOrderByTimeDesc(Integer userId, Pageable page);
}
