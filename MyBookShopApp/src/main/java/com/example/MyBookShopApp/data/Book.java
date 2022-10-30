package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.data.struct.book.file.FileDownloadEntity;
import com.example.MyBookShopApp.data.struct.book.links.Book2AuthorEntity;
import com.example.MyBookShopApp.data.struct.book.links.Book2GenreEntity;
import com.example.MyBookShopApp.data.struct.book.links.Book2UserEntity;
import com.example.MyBookShopApp.data.struct.book.review.BookReviewEntity;
import com.example.MyBookShopApp.data.struct.payments.BalanceTransactionEntity;
import com.example.MyBookShopApp.data.struct.tag.TagEntity;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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
    @OneToMany(mappedBy = "book")
    private List<Book2AuthorEntity> book2AuthorEntityList = new ArrayList<>();
    @Transient
    @OneToMany(mappedBy = "book")
    private List<Book2GenreEntity> book2GenreEntityList = new ArrayList<>();
    @Transient
    @OneToMany(mappedBy = "book")
    private List<Book2UserEntity> book2UserEntityList = new ArrayList<>();
    @Transient
    @OneToMany(mappedBy = "book")
    private List<FileDownloadEntity> fileDownloadEntityList = new ArrayList<>();
    @Transient
    @OneToMany(mappedBy = "book")
    private List<BalanceTransactionEntity> balanceTransactionEntityList = new ArrayList<>();
    @Transient
    @OneToMany(mappedBy = "book")
    private List<BookReviewEntity> bookReviewEntityList = new ArrayList<>();

    @Transient
    private String author;

    @Transient
    private double rating;

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

    public List<BalanceTransactionEntity> getBalanceTransactionEntityList() {
        return balanceTransactionEntityList;
    }

    public void setBalanceTransactionEntityList(List<BalanceTransactionEntity> balanceTransactionEntityList) {
        this.balanceTransactionEntityList = balanceTransactionEntityList;
    }

    public List<BookReviewEntity> getBookReviewEntityList() {
        return bookReviewEntityList;
    }

    public void setBookReviewEntityList(List<BookReviewEntity> bookReviewEntityList) {
        this.bookReviewEntityList = bookReviewEntityList;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<TagEntity> getTags() {
        return tags;
    }

    public void setTags(List<TagEntity> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", pubDate=" + pubDate +
                ", isBestseller=" + isBestseller +
                ", slug='" + slug + '\'' +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", discount=" + discount +
                ", book2AuthorEntityList=" + book2AuthorEntityList +
                ", book2GenreEntityList=" + book2GenreEntityList +
                ", book2UserEntityList=" + book2UserEntityList +
                ", fileDownloadEntityList=" + fileDownloadEntityList +
                ", balanceTransactionEntityList=" + balanceTransactionEntityList +
                ", bookReviewEntityList=" + bookReviewEntityList +
                ", author='" + author + '\'' +
                ", rating=" + rating +
                ", tags=" + tags +
                '}';
    }
}