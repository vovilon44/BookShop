package com.example.MyBookShopApp.data.struct.genre;

import com.example.MyBookShopApp.data.struct.book.links.Book2GenreEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "genre")
public class GenreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "INT")
    private Integer parentId;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String slug;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String ruName;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String enName;


    @OneToMany(mappedBy = "genre")
    private List<Book2GenreEntity> book2GenreEntityList = new ArrayList<>();

    @Transient
    private Integer countChildren = 0;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
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

    public List<Book2GenreEntity> getBook2GenreEntityList() {
        return book2GenreEntityList;
    }

    public void setBook2GenreEntityList(List<Book2GenreEntity> book2GenreEntityList) {
        this.book2GenreEntityList = book2GenreEntityList;
    }

    public Integer getBookCount() {
        return book2GenreEntityList.size();
    }

    public Integer getCountChildren() {
        return countChildren;
    }

    public void setCountChildren(Integer countChildren) {
        this.countChildren = countChildren;
    }

    @Override
    public String toString() {
        return "GenreEntity{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", slug='" + slug + '\'' +
                ", ruName='" + ruName + '\'' +
                ", enName='" + enName + '\'' +
                ", book2GenreEntityList=" + book2GenreEntityList +
                ", countChildren=" + countChildren +
                '}';
    }
}
