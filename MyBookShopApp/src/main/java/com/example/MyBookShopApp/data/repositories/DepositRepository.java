package com.example.MyBookShopApp.data.repositories;

import com.example.MyBookShopApp.data.DepositTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositRepository extends JpaRepository<DepositTransactionEntity, Long> {

    DepositTransactionEntity findDepositTransactionEntityBySignatureSecondAndApprove(String signature, boolean approve);
}
