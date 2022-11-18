package com.example.MyBookShopApp.data;

public class BookRatingDto {
    private Integer value;
    private String bookId;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    @Override
    public String toString() {
        return "BooksRatingDto{" +
                "value=" + value +
                ", bookId='" + bookId + '\'' +
                '}';
    }
}
