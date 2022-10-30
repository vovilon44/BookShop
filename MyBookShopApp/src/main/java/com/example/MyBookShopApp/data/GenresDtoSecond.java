package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.data.struct.genre.GenreEntity;

import java.util.List;

public class GenresDtoSecond
{
    private GenreEntity genre;
    private List<GenreEntity> childList;

    public GenresDtoSecond() {
    }

    public GenreEntity getGenre() {
        return genre;
    }

    public void setGenre(GenreEntity genre) {
        this.genre = genre;
    }

    public List<GenreEntity> getChildList() {
        return childList;
    }

    public void setChildList(List<GenreEntity> childList) {
        this.childList = childList;
    }

    @Override
    public String toString() {
        return "GenresDtoSecond{" +
                "genre=" + genre +
                ", childList=" + childList +
                '}';
    }
}
