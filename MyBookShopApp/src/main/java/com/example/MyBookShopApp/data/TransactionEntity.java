package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.security.BookstoreUser;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;


@Entity
@Table(name = "balance_transaction")
public class TransactionEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Double value;

    private LocalDateTime time;

    private String description;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private BookstoreUser user;

    @Transient
    private String dateTimeToString;

    public String getDateTimeToString() {
        return dateTimeToString;
    }

    public void setDateTimeToString(String dateTimeToString) {
        this.dateTimeToString = dateTimeToString;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return "TransactionEntity{" +
                "id=" + id +
                ", value=" + value +
                ", time=" + time +
                ", description='" + description + '\'' +
                ", book=" + book.getTitle() +
                ", user=" + user.getEmail() +
                '}';
    }
}
