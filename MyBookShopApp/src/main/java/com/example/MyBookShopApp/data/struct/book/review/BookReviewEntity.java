package com.example.MyBookShopApp.data.struct.book.review;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.data.struct.user.UserEntity;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "book_review")
public class BookReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "iser_id", referencedColumnName = "id")
    private UserEntity user;

    @Column(columnDefinition = "DATE NOT NULL")
    private LocalDate time;

    @Column(columnDefinition = "TEXT NOT NULL")
    private String text;

    @OneToMany(mappedBy = "review")
    private List<BookReviewLikeEntity> bookReviewLikeEntityList = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public LocalDate getTime() {
        return time;
    }

    public void setTime(LocalDate time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<BookReviewLikeEntity> getBookReviewLikeEntityList() {
        return bookReviewLikeEntityList;
    }

    public void setBookReviewLikeEntityList(List<BookReviewLikeEntity> bookReviewLikeEntityList) {
        this.bookReviewLikeEntityList = bookReviewLikeEntityList;
    }
}
