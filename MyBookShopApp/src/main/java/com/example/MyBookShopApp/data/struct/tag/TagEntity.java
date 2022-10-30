package com.example.MyBookShopApp.data.struct.tag;

import com.example.MyBookShopApp.data.struct.book.links.Book2TagEntity;

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
    private String name;

    String slug;
    @Transient
    @OneToMany(mappedBy = "tag")
    private List<Book2TagEntity> book2TagEntityList = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", book2TagEntityList=" + book2TagEntityList +
                '}';
    }
}
