package com.example.MyBookShopApp.data.struct.tag;

import com.example.MyBookShopApp.data.struct.book.links.Book2TagEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tag")
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String ruName;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String enName;

    String slug;
    @JsonIgnore
    @OneToMany(mappedBy = "tag")
    private List<Book2TagEntity> book2TagEntityList = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRuName() {
        return ruName;
    }

    public void setRuName(String ruName) {
        this.ruName = ruName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public List<Book2TagEntity> getBook2TagEntityList() {
        return book2TagEntityList;
    }

    public void setBook2TagEntityList(List<Book2TagEntity> book2TagEntityList) {
        this.book2TagEntityList = book2TagEntityList;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    @Override
    public String toString() {
        return "TagEntity{" +
                "id=" + id +
                ", ruName='" + ruName + '\'' +
                ", enName='" + enName + '\'' +
                ", slug='" + slug + '\'' +
                ", book2TagEntityList=" + book2TagEntityList +
                '}';
    }
}
