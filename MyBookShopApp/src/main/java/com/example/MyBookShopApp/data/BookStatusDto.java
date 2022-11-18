package com.example.MyBookShopApp.data;

public class BookStatusDto
{
    private String status;
    private String bookId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    @Override
    public String toString() {
        return "BookStatusDto{" +
                "status='" + status + '\'' +
                ", bookId='" + bookId + '\'' +
                '}';
    }
}
