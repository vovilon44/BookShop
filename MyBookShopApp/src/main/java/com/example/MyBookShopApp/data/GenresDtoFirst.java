package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.data.struct.genre.GenreEntity;

import java.util.List;

public class GenresDtoFirst
{
    private GenreEntity genre;
    private List<GenresDtoSecond> childList;

    public GenresDtoFirst(GenreEntity genre, List<GenresDtoSecond> childList) {
        this.genre = genre;
        this.childList = childList;
    }

    public GenreEntity getGenre() {
        return genre;
    }

    public void setGenre(GenreEntity genre) {
        this.genre = genre;
    }

    public List<GenresDtoSecond> getChildList() {
        return childList;
    }

    public void setChildList(List<GenresDtoSecond> childList) {
        this.childList = childList;
    }

    @Override
    public String toString() {
        return "GenresDtoFirst{" +
                "genre=" + genre +
                ", childList=" + childList +
                '}';
    }
}
