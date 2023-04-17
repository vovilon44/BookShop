package com.example.MyBookShopApp.data;

public class CountCartKeptMainDto
{
    private Boolean result;
    private Integer main;
    private Integer cart;
    private Integer kept;
    private Double balance;
    private String name;

    public CountCartKeptMainDto() {

    }

    public boolean isResult() {
        return result;
    }

    public CountCartKeptMainDto(Integer main, Integer cart, Integer kept, Double balance, String name) {
        this.main = main;
        this.cart = cart;
        this.kept = kept;
        this.balance = balance;
        this.name = name;
        result = true;
    }

    public CountCartKeptMainDto(boolean result) {
        this.result = result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public Integer getMain() {
        return main;
    }

    public void setMain(Integer main) {
        this.main = main;
    }

    public Integer getCart() {
        return cart;
    }

    public void setCart(Integer cart) {
        this.cart = cart;
    }

    public Integer getKept() {
        return kept;
    }

    public void setKept(Integer kept) {
        this.kept = kept;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CountCartKeptMainDto{" +
                "result=" + result +
                ", main=" + main +
                ", cart=" + cart +
                ", kept=" + kept +
                '}';
    }
}
