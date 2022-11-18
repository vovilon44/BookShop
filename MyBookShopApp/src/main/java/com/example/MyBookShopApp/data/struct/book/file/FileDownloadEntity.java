package com.example.MyBookShopApp.data.struct.book.file;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.security.BookstoreUser;

import javax.persistence.*;

@Entity
@Table(name = "file_download")
public class FileDownloadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "iser_id", referencedColumnName = "id")
    private BookstoreUser user;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book;

    @Column(columnDefinition = "INT NOT NULL DEFAULT 1")
    private int count;

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

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


}
