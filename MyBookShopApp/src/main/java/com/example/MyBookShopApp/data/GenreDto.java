package com.example.MyBookShopApp.data;

import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class GenreDto {
    private final List<Integer> ids = new ArrayList<>();
    private Element element;

    public GenreDto(Integer id, Element element) {
        this.ids.add(id);
        this.element = element;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void addId(Integer id){
        this.ids.add(id);
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }
}
