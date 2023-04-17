package com.example.MyBookShopApp.data.struct.other;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.persistence.*;
import java.util.StringJoiner;

@Entity
@Table(name = "document")
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "INT NOT NULL  DEFAULT 0")
    private int sortIndex;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String slug;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String title;

    @Column(columnDefinition = "TEXT NOT NULL")
    private String text;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPartText(){
        Document doc = Jsoup.parse(text);
        Elements elements = doc.body().select("p");
        StringBuilder sb = new StringBuilder();
        for (Element element : elements) {
            if (sb.length() + element.toString().length() < 800){
                sb.append(element);
            } else {
                StringJoiner sj = new StringJoiner(" ");
                for (String str : element.text().split(" ")){
                    if (sb.length() + sj.length() + str.length() > 800){
                        element.text(sj.toString());
                        sb.append(element);
                        return sb.toString();
                    } else {
                        sj.add(str);
                    }
                }
            }

        }
        return sb.toString();
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
