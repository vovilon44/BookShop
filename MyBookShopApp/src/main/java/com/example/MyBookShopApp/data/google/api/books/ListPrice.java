package com.example.MyBookShopApp.data.google.api.books;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ListPrice {
    @JsonProperty("amount")
    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    int amount;

    @JsonProperty("currencyCode")
    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    String currencyCode;

    @JsonProperty("amountInMicros")
    public long getAmountInMicros() {
        return this.amountInMicros;
    }

    public void setAmountInMicros(long amountInMicros) {
        this.amountInMicros =  amountInMicros;
    }

    long amountInMicros;

    @Override
    public String toString() {
        return "ListPrice{" +
                "amount=" + amount +
                ", currencyCode='" + currencyCode + '\'' +
                ", amountInMicros=" + amountInMicros +
                '}';
    }
}
