package com.example.MyBookShopApp.data.struct.user;

import com.example.MyBookShopApp.data.struct.enums.ContactType;
import com.example.MyBookShopApp.security.BookstoreUser;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_contact")
public class UserContactEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "iser_id", referencedColumnName = "id")
    private BookstoreUser user;

    private ContactType type;

    @Column(columnDefinition = "SMALLINT NOT NULL")
    private short approved;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String code;

    @Column(columnDefinition = "INT")
    private int codeTrails;

    @Column(columnDefinition = "DATE NOT NULL")
    private LocalDate codeTime;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String contact;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BookstoreUser getUser() {
        return user;
    }

    public void setUser(BookstoreUser user) {
        this.user = user;
    }

    public ContactType getType() {
        return type;
    }

    public void setType(ContactType type) {
        this.type = type;
    }

    public short getApproved() {
        return approved;
    }

    public void setApproved(short approved) {
        this.approved = approved;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCodeTrails() {
        return codeTrails;
    }

    public void setCodeTrails(int codeTrails) {
        this.codeTrails = codeTrails;
    }

    public LocalDate getCodeTime() {
        return codeTime;
    }

    public void setCodeTime(LocalDate codeTime) {
        this.codeTime = codeTime;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
