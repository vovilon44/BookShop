package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.data.struct.book.links.Book2AuthorEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String photo;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String slug;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Transient
    private String showText;
    @Transient
    private String hideText;

    public void setShowText(String showText) {
        this.showText = showText;
    }

    public void setHideText(String hideText) {
        this.hideText = hideText;
    }

    public String getShowText(){
        String showText = "";
        int textLimit = 500;
        for(String text : getDescription().split("\\.")){
            if (showText.length() + text.length() < textLimit){
                showText+= text + ".";
            }
        }
        return showText;
    }

    public String getHideText(){
        String hideText = "";
        String showText = "";
        int textLimit = 500;
        for(String text : getDescription().split("\\.")){
            if (showText.length() + text.length() < textLimit){
                showText+= text + ".";
            } else {
                hideText += text + ".";
            }
        }
        return hideText;
    }

    @Transient
    @OneToMany(mappedBy = "author")
    private List<Book2AuthorEntity> book2AuthorEntityList = new ArrayList<>();

    public List<Book2AuthorEntity> getBook2AuthorEntityList() {
        return book2AuthorEntityList;
    }

    public void setBook2AuthorEntityList(List<Book2AuthorEntity> book2AuthorEntityList) {
        this.book2AuthorEntityList = book2AuthorEntityList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }
}