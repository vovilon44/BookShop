package com.example.MyBookShopApp.data.struct.user;

import com.example.MyBookShopApp.data.struct.book.file.FileDownloadEntity;
import com.example.MyBookShopApp.data.struct.book.links.Book2UserEntity;
import com.example.MyBookShopApp.data.struct.book.review.BookReviewEntity;
import com.example.MyBookShopApp.data.struct.book.review.BookReviewLikeEntity;
import com.example.MyBookShopApp.data.struct.book.review.MessageEntity;
import com.example.MyBookShopApp.data.struct.payments.BalanceTransactionEntity;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String hash;

    @Column(columnDefinition = "DATE NOT NULL")
    private LocalDate regTime;

    @Column(columnDefinition = "INT NOT NULL")
    private double balance;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @Transient
    @OneToMany(mappedBy = "user")
    private List<Book2UserEntity> book2UserEntityList = new ArrayList<>();
    @Transient
    @OneToMany(mappedBy = "user")
    private List<FileDownloadEntity> fileDownloadEntityList = new ArrayList<>();

    @Transient
    @OneToMany(mappedBy = "user")
    private List<BalanceTransactionEntity> balanceTransactionEntityList = new ArrayList<>();
    @Transient
    @OneToMany(mappedBy = "user")
    private List<BookReviewEntity> bookReviewEntityList = new ArrayList<>();

    @Transient
    @OneToMany(mappedBy = "user")
    private List<MessageEntity> messageEntityList = new ArrayList<>();

    @Transient
    @OneToMany(mappedBy = "user")
    private List<BookReviewLikeEntity> bookReviewLikeEntityList = new ArrayList<>();

    @Transient
    @OneToMany(mappedBy = "user")
    private List<UserContactEntity> userContactEntityList = new ArrayList<>();


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public LocalDate getRegTime() {
        return regTime;
    }

    public void setRegTime(LocalDate regTime) {
        this.regTime = regTime;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<UserContactEntity> getUserContactEntityList() {
        return userContactEntityList;
    }

    public void setUserContactEntityList(List<UserContactEntity> userContactEntityList) {
        this.userContactEntityList = userContactEntityList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<BalanceTransactionEntity> getBalanceTransactionEntityList() {
        return balanceTransactionEntityList;
    }

    public void setBalanceTransactionEntityList(List<BalanceTransactionEntity> balanceTransactionEntityList) {
        this.balanceTransactionEntityList = balanceTransactionEntityList;
    }

    public List<BookReviewEntity> getBookReviewEntityList() {
        return bookReviewEntityList;
    }

    public void setBookReviewEntityList(List<BookReviewEntity> bookReviewEntityList) {
        this.bookReviewEntityList = bookReviewEntityList;
    }

    public List<MessageEntity> getMessageEntityList() {
        return messageEntityList;
    }

    public void setMessageEntityList(List<MessageEntity> messageEntityList) {
        this.messageEntityList = messageEntityList;
    }

    public List<BookReviewLikeEntity> getBookReviewLikeEntityList() {
        return bookReviewLikeEntityList;
    }

    public void setBookReviewLikeEntityList(List<BookReviewLikeEntity> bookReviewLikeEntityList) {
        this.bookReviewLikeEntityList = bookReviewLikeEntityList;
    }
}
