package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.security.BookstoreUser;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
public class SessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate dateCreation;

    private LocalDate dateLastSession;

    private Integer countDay;

    private String token;

    private String phoneModel;
    private String platform;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private BookstoreUser user;

    public SessionEntity(BookstoreUser user, String token) {
        this.token = token;
        this.user = user;
        this.countDay = 0;
        this.dateCreation = LocalDate.now();
        this.dateLastSession = LocalDate.now();
    }



    public SessionEntity() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDate getDateLastSession() {
        return dateLastSession;
    }

    public void setDateLastSession(LocalDate dateLastSession) {
        this.dateLastSession = dateLastSession;
    }

    public Integer getCountDay() {
        return countDay;
    }

    public void setCountDay(Integer countDay) {
        this.countDay = countDay;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public BookstoreUser getUser() {
        return user;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setUser(BookstoreUser user) {
        this.user = user;
    }
}
