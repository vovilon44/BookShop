package com.example.MyBookShopApp.data.struct.book.review;

import com.example.MyBookShopApp.security.BookstoreUser;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "book_review_like")
public class BookReviewLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private BookstoreUser user;

    @ManyToOne
    @JoinColumn(name = "review_id", referencedColumnName = "id")
    private BookReviewEntity review;

    @Column(columnDefinition = "DATE NOT NULL")
    private LocalDate time;

    @Column(columnDefinition = "SMALLINT NOT NULL")
    private Integer value;



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

    public BookReviewEntity getReview() {
        return review;
    }

    public void setReview(BookReviewEntity review) {
        this.review = review;
    }

    public LocalDate getTime() {
        return time;
    }

    public void setTime(LocalDate time) {
        this.time = time;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "BookReviewLikeEntity{" +
                "id=" + id +
                ", user=" + user.getId() +
                ", review=" + review.getId() +
                ", time=" + time +
                ", value=" + value +
                '}';
    }
}
