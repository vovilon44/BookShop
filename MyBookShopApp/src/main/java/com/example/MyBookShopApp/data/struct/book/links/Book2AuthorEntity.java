package com.example.MyBookShopApp.data.struct.book.links;

import com.example.MyBookShopApp.data.Author;
import com.example.MyBookShopApp.data.Book;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "book2author")
public class Book2AuthorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private Author author;

    @JsonGetter("authors")
    public String authorsFullName(){
        return author.toString();
    }

    @JsonGetter("bookSlug")
    public String getSlugBook(){
        return book.getSlug();
    }

    @JsonGetter("authorSlug")
    public String getSlugAuthor(){
        return author.getSlug();
    }

    @JsonGetter("books")
    public String booksName(){
        return book.toString();
    }

    @Column(columnDefinition = "INT NOT NULL  DEFAULT 0")
    private int sortIndex;

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

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    @Override
    public String toString() {
        return "Book2AuthorEntity{" +
                "id=" + id +
                ", book=" + book.getTitle() +
                ", author=" + author.getName() +
                ", sortIndex=" + sortIndex +
                '}';
    }
}
