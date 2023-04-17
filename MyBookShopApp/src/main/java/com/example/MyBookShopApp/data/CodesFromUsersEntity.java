package com.example.MyBookShopApp.data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "codes_from_users")
public class CodesFromUsersEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private LocalDateTime expireTime;
    private String contact;
    @Column(columnDefinition = "INT NOT NULL")
    private Integer tryCount;

    public CodesFromUsersEntity(String contact, String code, Integer expireIn, Integer tryCount) {
        this.code = code;
        this.contact = contact;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
        this.tryCount = tryCount;
    }

    public CodesFromUsersEntity() {
    }

    public Boolean isExpired()
    {
        return LocalDateTime.now().isAfter(expireTime);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Integer getTryCount() {
        return tryCount;
    }

    public void setTryCount(Integer tryCount) {
        this.tryCount = tryCount;
    }
}
