package com.example.MyBookShopApp.security;

public class AddMoneyDto
{
    private Integer sum;

    public Integer getSum() {
        return sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

    @Override
    public String toString() {
        return "AddMoneyDto{" +
                "sum=" + sum +
                '}';
    }
}
