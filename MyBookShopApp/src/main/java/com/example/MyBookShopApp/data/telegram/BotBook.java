package com.example.MyBookShopApp.data.telegram;

import com.example.MyBookShopApp.data.Book;

public class BotBook {
    private Book book;
    private String authors;
    private String genres;
    private String tags;
    private Double rating;


    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Double getRating() {

        return (Double.valueOf(Math.round(rating * 100))) / 100;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Double getCurrentPrice() {

        return (double) Math.round((book.getPrice() - book.getPrice() * book.getDiscount()) * 100) / 100;
    }

    @Override
    public String toString() {
        return "BotBookData{" +
                "book=" + book +
                ", authors='" + authors + '\'' +
                ", genres='" + genres + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }
}
