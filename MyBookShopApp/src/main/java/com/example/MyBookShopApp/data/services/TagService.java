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
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public TagService(TagRepository tagRepository, NamedParameterJdbcTemplate jdbcTemplate) {
        this.tagRepository = tagRepository;
        this.jdbcTemplate = jdbcTemplate;
    }



    public List<TagWithRangDto> getAllTags() {
        HashMap<Integer, TagEntity> map = new HashMap<Integer, TagEntity>();
        tagRepository.findAll().
                forEach(tag -> map.put(tag.getId(), tag));
        List<TagWithRangDto> listDto = jdbcTemplate.query("select tag.id , (select count(*) from tag tg inner join book2tag bk on tg.id = bk.tag_id group by tg.id order by count desc limit 1) as maximum, (select count(*) from tag tg inner join book2tag bk on tg.id = bk.tag_id group by tg.id order by count limit 1) as minimum, count(*) from tag inner join book2tag on tag.id = book2tag.tag_id group by tag.id ", (ResultSet rs, int rowNum) -> {
            int maximum = rs.getInt("maximum");
            int minimum = rs.getInt("minimum");
            int count = rs.getInt("count");
            int rang = (maximum - count) / ((maximum - minimum) / 4);
            return new TagWithRangDto(map.get(rs.getInt("id")), rang);
        });
        return new ArrayList<>(listDto);
    }


    public List<TagEntity> getTagsForBookBySlug(String slug) {
        List tt = jdbcTemplate.query("select * from tag inner join book2tag on tag.id = book2tag.tag_id where book2tag.book_id=(select books.id from books where books.slug = :slug) ", Map.of( "slug", slug), (ResultSet rs, int rowNum) ->{
            TagEntity tag = new TagEntity();
            tag.setId(rs.getInt("id"));
            tag.setName(rs.getString("name"));
            tag.setSlug(rs.getString("slug"));
            return tag;
        });
        return tt;
    }

    public TagEntity getTagEntityBySlug(String slug){
        return tagRepository.findBySlug(slug);
    }
}
