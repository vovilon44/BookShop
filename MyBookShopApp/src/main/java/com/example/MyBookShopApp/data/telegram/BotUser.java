package com.example.MyBookShopApp.data.telegram;

import com.example.MyBookShopApp.security.BookstoreUser;

import javax.persistence.*;

@Entity
@Table(name = "bot_user")
public class BotUser {
    @Id
    private Long id;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status_id")
    private BotUserStatus status;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private BookstoreUser user;

    public BotUser() {
    }

    public BotUser(Long id, String phoneNumber) {
        this.id = id;
        this.phoneNumber = phoneNumber;
    }

    private String wordForSearch;
    private Integer pageNumber;

    private String phoneNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BotUserStatus getStatus() {
        return status;
    }

    public void setStatus(BotUserStatus status) {
        this.status = status;
    }

    public BookstoreUser getUser() {
        return user;
    }

    public void setUser(BookstoreUser user) {
        this.user = user;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWordForSearch() {
        return wordForSearch;
    }

    public void setWordForSearch(String wordForSearch) {
        this.wordForSearch = wordForSearch;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }
}
