package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.TransactionEntity;
import com.example.MyBookShopApp.data.struct.book.file.FileDownloadEntity;
import com.example.MyBookShopApp.data.struct.book.links.Book2UserEntity;
import com.example.MyBookShopApp.data.struct.book.links.BookLike2UserEntity;
import com.example.MyBookShopApp.data.struct.book.review.BookReviewEntity;
import com.example.MyBookShopApp.data.struct.book.review.BookReviewLikeEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
public class BookstoreUser
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String email;

    private Date regTime;

    private double balance;
    private String phone;
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<BookLike2UserEntity> bookLike2UserEntityList = new ArrayList<>();

    @Transient
    @OneToMany(mappedBy = "user")
    private List<Book2UserEntity> book2UserEntityList = new ArrayList<>();

    @Transient
    @OneToMany(mappedBy = "user")
    private List<FileDownloadEntity> fileDownloadEntityList = new ArrayList<>();

    @Transient
    @OneToMany(mappedBy = "user")
    private List<TransactionEntity> balanceTransactionEntityList = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<BookReviewEntity> bookReviewEntityList = new ArrayList<>();


    @Transient
    @OneToMany(mappedBy = "user")
    private List<BookReviewLikeEntity> bookReviewLikeEntityList = new ArrayList<>();




    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getRegTime() {
        return regTime;
    }

    public void setRegTime(Date regTime) {
        this.regTime = regTime;
    }

    public double getBalance() {
        return Math.round(balance * 100) / 100;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<BookLike2UserEntity> getBookLike2UserEntityList() {
        return bookLike2UserEntityList;
    }

    public void setBookLike2UserEntityList(List<BookLike2UserEntity> bookLike2UserEntityList) {
        this.bookLike2UserEntityList = bookLike2UserEntityList;
    }

    public List<Book2UserEntity> getBook2UserEntityList() {
        return book2UserEntityList;
    }

    public void setBook2UserEntityList(List<Book2UserEntity> book2UserEntityList) {
        this.book2UserEntityList = book2UserEntityList;
    }

    public List<FileDownloadEntity> getFileDownloadEntityList() {
        return fileDownloadEntityList;
    }

    public void setFileDownloadEntityList(List<FileDownloadEntity> fileDownloadEntityList) {
        this.fileDownloadEntityList = fileDownloadEntityList;
    }

    public List<TransactionEntity> getBalanceTransactionEntityList() {
        return balanceTransactionEntityList;
    }

    public void setBalanceTransactionEntityList(List<TransactionEntity> balanceTransactionEntityList) {
        this.balanceTransactionEntityList = balanceTransactionEntityList;
    }

    public List<BookReviewEntity> getBookReviewEntityList() {
        return bookReviewEntityList;
    }

    public void setBookReviewEntityList(List<BookReviewEntity> bookReviewEntityList) {
        this.bookReviewEntityList = bookReviewEntityList;
    }




    public List<BookReviewLikeEntity> getBookReviewLikeEntityList() {
        return bookReviewLikeEntityList;
    }

    public void setBookReviewLikeEntityList(List<BookReviewLikeEntity> bookReviewLikeEntityList) {
        this.bookReviewLikeEntityList = bookReviewLikeEntityList;
    }


    @Override
    public String toString() {
        return "BookstoreUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", regTime=" + regTime +
                ", balance=" + balance +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
