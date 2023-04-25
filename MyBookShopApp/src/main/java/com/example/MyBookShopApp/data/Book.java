package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.data.struct.book.file.BookFile;
import com.example.MyBookShopApp.data.struct.book.file.FileDownloadEntity;
import com.example.MyBookShopApp.data.struct.book.links.*;
import com.example.MyBookShopApp.data.struct.book.review.BookReviewEntity;
import com.example.MyBookShopApp.data.struct.tag.TagEntity;
import com.example.MyBookShopApp.security.BookstoreUser;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "DATE NOT NULL")
    private Date pubDate;
    @Column(columnDefinition = "SMALLINT NOT NULL")
    private int isBestseller;
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String slug;
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String title;
    private String image;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(columnDefinition = "INT NOT NULL")
    private Integer price;
    @Column(columnDefinition = "NUMERIC NOT NULL DEFAULT 0")
    private double discount;
    @Transient
    private String authors;

    @Transient
    private BookstoreUser user;
    @Transient
    private String status;





    @JsonGetter("status")
    public String getStatus() {
        if (user != null && book2UserEntityList != null) {
            for (Book2UserEntity book2User : book2UserEntityList) {
                if (book2User.getUser().getId() == user.getId()) {
                    switch (book2User.getTypeId()) {
                        case 1:
                            return "KEPT";
                        case 2:
                            return "CART";
                        case 3:
                            return "PAID";
                        default:
                            return "";
                    }
                }
            }
        } else if (status != null){
            return status;
        }
        return "";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "book")
    private List<Book2AuthorEntity> book2AuthorEntityList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "book")
    private List<Book2TagEntity> book2TagEntityList;
    @JsonIgnore
    @OneToMany(mappedBy = "book")
    private List<Book2GenreEntity> book2GenreEntityList;
    @JsonIgnore
    @OneToMany(mappedBy = "book")
    private List<Book2UserEntity> book2UserEntityList;
    @JsonIgnore
    @OneToMany(mappedBy = "book")
    private List<FileDownloadEntity> fileDownloadEntityList;


    @JsonGetter("rating")
    public Integer getBookRating(){
        if (bookLike2UserEntityList != null && bookLike2UserEntityList.size() > 0) {
            return (int) bookLike2UserEntityList.stream().mapToDouble(e -> e.getLikeValue()).average().getAsDouble();
        }
        else {
           return  0;
        }
    }

    @JsonIgnore
    public Double getBookRatingAllReviews(){


        if (bookReviewEntityList.size() > 0) {
            return bookReviewEntityList.stream().mapToDouble(e -> e.getRatingForReview()).filter(e-> e > 0).average().getAsDouble();
        }
        else {
            return  0.0;
        }
    }

    @JsonIgnore
    public List<BookReviewEntity> getBooksSortedReviews(){
        bookReviewEntityList.sort(Comparator.comparing(BookReviewEntity::getRang).reversed());
        return bookReviewEntityList;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "book")
    private List<BookLike2UserEntity> bookLike2UserEntityList;


    @Transient
    @JsonIgnore
    @OneToMany(mappedBy = "book")
    private List<TransactionEntity> balanceTransactionEntityList;
    @JsonIgnore
    @OneToMany(mappedBy = "book")
    private List<BookReviewEntity> bookReviewEntityList;


    @JsonIgnore
    @OneToMany(mappedBy = "book")
    private List<Book2UserHistory> book2UserHistoryList;

    @JsonIgnore
    @OneToMany(mappedBy = "book")
    private List<BookFile> bookFileList = new ArrayList<>();

    public List<Book2TagEntity> getBook2TagEntityList() {
        return book2TagEntityList;
    }

    public void setBook2TagEntityList(List<Book2TagEntity> book2TagEntityList) {
        this.book2TagEntityList = book2TagEntityList;
    }

    @Transient
    private List<TagEntity> tags;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Book2AuthorEntity> getBook2AuthorEntityList() {
        return book2AuthorEntityList;
    }

    public void setBook2AuthorEntityList(List<Book2AuthorEntity> book2AuthorEntityList) {
        this.book2AuthorEntityList = book2AuthorEntityList;
    }

    public List<Book2GenreEntity> getBook2GenreEntityList() {
        return book2GenreEntityList;
    }


    public void setBook2GenreEntityList(List<Book2GenreEntity> book2GenreEntityList) {
        this.book2GenreEntityList = book2GenreEntityList;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public int getIsBestseller() {
        return isBestseller;
    }

    public void setIsBestseller(int isBestseller) {
        this.isBestseller = isBestseller;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public List<Book2UserEntity> getBook2UserEntityList() {
        return book2UserEntityList;
    }

    public void setBook2UserEntityList(List<Book2UserEntity> book2UserEntityList) {
        this.book2UserEntityList = book2UserEntityList;
    }

    public List<FileDownloadEntity> getFileDownloadEntityList() {
        return fileDownloadEntityList;
    }

    public void setFileDownloadEntityList(List<FileDownloadEntity> fileDownloadEntityList) {
        this.fileDownloadEntityList = fileDownloadEntityList;
    }

    public List<TransactionEntity> getBalanceTransactionEntityList() {
        return balanceTransactionEntityList;
    }

    public void setBalanceTransactionEntityList(List<TransactionEntity> balanceTransactionEntityList) {
        this.balanceTransactionEntityList = balanceTransactionEntityList;
    }

    public List<BookReviewEntity> getBookReviewEntityList() {
        return bookReviewEntityList;
    }

    public void setBookReviewEntityList(List<BookReviewEntity> bookReviewEntityList) {
        this.bookReviewEntityList = bookReviewEntityList;
    }

    public List<TagEntity> getTags() {
        return tags;
    }

    public void setTags(List<TagEntity> tags) {
        this.tags = tags;
    }

    public List<BookFile> getBookFileList() {
        return bookFileList;
    }

    public void setBookFileList(List<BookFile> bookFileList) {
        this.bookFileList = bookFileList;
    }

    public List<BookLike2UserEntity> getBookLike2UserEntityList() {
        return bookLike2UserEntityList;
    }

    public void setBookLike2UserEntityList(List<BookLike2UserEntity> bookLike2UserEntityList) {
        this.bookLike2UserEntityList = bookLike2UserEntityList;
    }

    @JsonIgnore
    public HashMap<Integer, Integer> getBookLikeMap() {
        HashMap<Integer, Integer> map = new HashMap<>();
        bookLike2UserEntityList.forEach(e->{map.put(e.getLikeValue(), map.get(e.getLikeValue()) == null ? 1 : map.get(e.getLikeValue()) + 1);
    });
        return map;
    }

    public BookstoreUser getUser() {
        return user;
    }

    public void setUser(BookstoreUser user) {
        this.user = user;
    }

    public List<Book2UserHistory> getBook2UserHistoryList() {
        return book2UserHistoryList;
    }

    public void setBook2UserHistoryList(List<Book2UserHistory> book2UserHistoryList) {
        this.book2UserHistoryList = book2UserHistoryList;
    }

    public String getAuthors() {
        List<String> authorsList = book2AuthorEntityList.stream().map(e -> e.authorsFullName()).collect(Collectors.toList());
        if (authorsList.size() > 0) {
            return authorsList.get(0) + (authorsList.size() > 1 ? " и другие" : "");
        } else if (authors != null) {
            return authors;
        } else {
            return "Автор неизвестен";
        }
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    @Override
    public String toString() {
        return "Book{" +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", price=" + price +
                ", discount=" + discount +
                '}';
    }
}