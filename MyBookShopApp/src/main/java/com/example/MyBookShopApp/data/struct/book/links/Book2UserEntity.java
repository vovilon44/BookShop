package com.example.MyBookShopApp.data.struct.book.links;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.security.BookstoreUser;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "book2user")
public class Book2UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "DATE NOT NULL")
    private LocalDate time;

    @Column(columnDefinition = "INT NOT NULL")
    private int typeId;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private BookstoreUser user;

    public Book2UserEntity() {

    }

    public Book2UserEntity(int typeId, Book book, BookstoreUser user) {
        this();
        this.time = LocalDate.now();
        this.typeId = typeId;
        this.book = book;
        this.user = user;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getTime() {
        return time;
    }

    public void setTime(LocalDate time) {
        this.time = time;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public BookstoreUser getUser() {
        return user;
    }

    public void setUser(BookstoreUser user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Book2UserEntity{" +
                "id=" + id +
                ", time=" + time +
                ", typeId=" + typeId +
                ", book=" + book +
                ", user=" + user +
                '}';
    }
}
