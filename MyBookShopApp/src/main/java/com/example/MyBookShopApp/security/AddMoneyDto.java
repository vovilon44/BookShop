package com.example.MyBookShopApp.security;

public class AddMoneyDto
{
    private Double sum;

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    @Override
    public String toString() {
        return "AddMoneyDto{" +
                "sum=" + sum +
                '}';
    }
}
