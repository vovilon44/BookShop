package com.example.MyBookShopApp.data.struct.book.review;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.security.BookstoreUser;

import javax.persistence.*;
import java.util.Date;
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
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private BookstoreUser user;

    @Column(columnDefinition = "DATE NOT NULL")
    private Date time;

    @Column(columnDefinition = "TEXT NOT NULL")
    private String text;

    @OneToMany(mappedBy = "review")
    private List<BookReviewLikeEntity> bookReviewLikeEntityList;

    public String getShowText(){
        String showText = "";
        for (String str : text.split("\\.")){
            if (showText.length() + str.length() < 750 ){
                showText += str + ". ";
            } else {
                return showText;
            }
        }
        return showText;
    }

    public String getHideText(){
        String hideText = "";
        String showText = "";
        boolean hide = true;
        for (String str : text.split("\\.")){
            if (showText.length() + str.length() < 750 && hide){
                showText += str + ". ";
            } else {
                hide = false;
                hideText += str + ". ";
            }
        }
        return hideText;
    }

    public Long getPosLikeCount(){
        return (bookReviewLikeEntityList.stream().filter(e -> e.getValue() > 0).count());
    }


    public Long getNegLikeCount(){
        return (bookReviewLikeEntityList.stream().filter(e -> e.getValue() < 0).count());
    }


    public Double getRatingForReview() {
        if (bookReviewLikeEntityList.size() > 0) {
            return ((double) bookReviewLikeEntityList.stream().filter(e -> e.getValue() > 0).count()) / bookReviewLikeEntityList.size();
        } else {
            return 0.0;
        }


    }

    public Integer getRang() {
        if (bookReviewLikeEntityList.size() > 0) {
            return bookReviewLikeEntityList.stream().reduce(0, (x, e) -> x + e.getValue(), (x, e) -> x + e);
        } else {
            return 0;
        }
    }

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

    public BookstoreUser getUser() {
        return user;
    }


    public void setUser(BookstoreUser user) {
        this.user = user;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
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

    @Override
    public String toString() {
        return "BookReviewEntity{" +
                "id=" + id +
                ", book=" + book.getTitle() +
                ", user=" + user.getName() +
                ", time=" + time + + '\'' +
                ", textShow='" + getShowText() + "\' \n" +
//                ", rang =" + getRang() +
                "rating=" + getRatingForReview() +
                ", bookReviewLikeEntityList=" + bookReviewLikeEntityList +
                '}';
    }
}
