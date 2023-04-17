package com.example.MyBookShopApp.data;

import java.util.ArrayList;
import java.util.List;

public class BooksPageDto {

    private Integer count;
    private List<Book> books = new ArrayList<>();
    private boolean endList;
    private Integer totalCount;

    public BooksPageDto() {
    }

    public BooksPageDto(List<Book> books, boolean endList, Integer totalCount) {
        this();
        this.count = books.size();
        this.books = books;
        this.endList = endList;
        this.totalCount = totalCount;
    }


    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public boolean isEndList() {
        return endList;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public void setEndList(boolean endList) {
        this.endList = endList;
    }

    @Override
    public String toString() {
        return "BooksPageDto{" +
                "count=" + count +
                ", books=" + books +
                ", endList=" + endList +
                '}';
    }
}
