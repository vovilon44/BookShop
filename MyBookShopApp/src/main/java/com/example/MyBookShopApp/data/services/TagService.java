package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.TagWithRangDto;
import com.example.MyBookShopApp.data.repositories.TagRepository;
import com.example.MyBookShopApp.data.struct.tag.TagEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TagService
{
    private TagRepository tagRepository;


    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }



    public List<TagWithRangDto> getAllTags() {
        return tagRepository.findAllTagsWithRang();
    }


    public List<TagEntity> getTagsForBookBySlug(String slug) {
        return tagRepository.getTagsForBookBySlug(slug);
    }

    public TagEntity getTagEntityBySlug(String slug){
        return tagRepository.findBySlug(slug);
    }
}
