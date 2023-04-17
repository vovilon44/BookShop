package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.data.struct.tag.TagEntity;

public class TagWithRangDto
{


    private TagEntity tag;

    private Integer rang;

    public TagWithRangDto(TagEntity tagEntity, Integer rang) {
        this.tag = tagEntity;
        this.rang = rang;

    }

    public TagEntity getTag() {
        return tag;
    }

    public void setTag(TagEntity tag) {
        this.tag = tag;
    }

    public double getRang() {
        return rang;
    }

    public void setRang(Integer rang) {
        this.rang = rang;
    }

    @Override
    public String toString() {
        return "TagWithRangDto{" +
                "tag=" + tag +
                ", rang=" + rang +
                '}';
    }
}
