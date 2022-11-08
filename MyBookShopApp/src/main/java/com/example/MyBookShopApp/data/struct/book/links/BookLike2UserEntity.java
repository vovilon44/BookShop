package com.example.MyBookShopApp.data.struct.book.links;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.data.struct.user.UserEntity;

import javax.persistence.*;
import java.sql.Date;

@Entity
public class BookLike2UserEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "pub_date")
    private Date pubDate;

    @Column(name = "like_value")
    private Integer likeValue;


    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public Integer getLikeValue() {
        return likeValue;
    }

    public void setLikeValue(Integer likeValue) {
        this.likeValue = likeValue;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
