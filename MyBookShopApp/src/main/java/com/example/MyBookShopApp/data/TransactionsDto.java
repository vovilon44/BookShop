package com.example.MyBookShopApp.data;


import java.util.List;

public class TransactionsDto {
    private Integer count;
    private List<TransactionEntity> transactions;
    private boolean endList;

    public TransactionsDto(List<TransactionEntity> transactions, Integer count, Boolean endList) {
        this.count = count;
        this.transactions = transactions;
        this.endList = endList;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public boolean isEndList() {
        return endList;
    }

    public void setEndList(boolean endList) {
        this.endList = endList;
    }

    public List<TransactionEntity> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionEntity> transactions) {
        this.transactions = transactions;
    }
}
