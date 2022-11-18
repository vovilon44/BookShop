package com.example.MyBookShopApp.data;

public class ReviewMessageDto
{
    private String bookId;
    private String text;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "ReviewMessageDto{" +
                "bookId='" + bookId + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
