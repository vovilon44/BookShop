package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.DepositTransactionEntity;
import com.example.MyBookShopApp.data.repositories.DepositRepository;
import com.example.MyBookShopApp.security.BookstoreUser;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DepositService {

    private final DepositRepository depositRepository;
    private final BookstoreUserRegister userRegister;

    @Autowired
    public DepositService(DepositRepository depositRepository, BookstoreUserRegister userRegister) {
        this.depositRepository = depositRepository;
        this.userRegister = userRegister;
    }

    public void addTransaction(String signature,String signatureSecond, Double sum, Integer invId){
        depositRepository.save(new DepositTransactionEntity(signature, signatureSecond, sum, userRegister.getCurrentUser(), invId));
    }
    public void addTransaction(String signature,String signatureSecond, Double sum, BookstoreUser user, Integer invId){
        depositRepository.save(new DepositTransactionEntity(signature, signatureSecond, sum, user, invId));
    }

    public DepositTransactionEntity checkDepositTransaction(String signature){
        DepositTransactionEntity transaction = depositRepository.findDepositTransactionEntityBySignatureSecondAndApprove(signature, false);
        if (transaction != null){
            transaction.setApprove(true);
            depositRepository.save(transaction);
            return transaction;
        } else {
            return null;
        }
    }


}
