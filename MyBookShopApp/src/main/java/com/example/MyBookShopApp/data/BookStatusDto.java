package com.example.MyBookShopApp.data;

import java.util.ArrayList;
import java.util.List;

public class BookStatusDto
{
    private String status;
    private List<String> booksIds = new ArrayList<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getBooksIds() {
        return booksIds;
    }

    public void setBooksIds(List<String> booksIds) {
        this.booksIds = booksIds;
    }

    @Override
    public String toString() {
        return "BookStatusDto{" +
                "status='" + status + '\'' +
                ", bookIds=" + booksIds +
                '}';
    }
}
