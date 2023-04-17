package com.example.MyBookShopApp.security;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "black_list")
public class TokenEntity
{
    @Id
    private String tokenKey;

    private Date date;

    public TokenEntity(String tokenKey, Date date) {
        this();
        this.tokenKey = tokenKey;
        this.date = date;
    }

    public TokenEntity() {

    }

    public String getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
