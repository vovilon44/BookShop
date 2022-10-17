package com.example.MyBookShopApp.data.struct.book.links;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.data.struct.genre.GenreEntity;

import javax.persistence.*;

@Entity
@Table(name = "book2genre")
public class Book2GenreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "genre_id", referencedColumnName = "id")
    private GenreEntity genre;
}
