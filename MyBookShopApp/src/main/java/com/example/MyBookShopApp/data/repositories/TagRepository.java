package com.example.MyBookShopApp.data.repositories;

import com.example.MyBookShopApp.data.TagWithRangDto;
import com.example.MyBookShopApp.data.struct.tag.TagEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class TagRepository
{

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public TagRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }



    public TagEntity findBySlug(String slug){
        return jdbcTemplate.queryForObject("select * from tag where slug = :slug", Map.of("slug", slug), this::getTagEntityAfterSQL);
    }

    public List<TagWithRangDto> findAllTagsWithRang(){
        return jdbcTemplate.query("select tag.* , " +
                "(select count(*) from tag tg inner join book2tag bk on tg.id = bk.tag_id group by tg.id order by count desc limit 1) as maximum, " +
                "(select count(*) from tag tg inner join book2tag bk on tg.id = bk.tag_id group by tg.id order by count limit 1) as minimum, " +
                "count(*) from tag inner join book2tag on tag.id = book2tag.tag_id group by tag.id", (ResultSet rs, int rowNum) -> {
            int maximum = rs.getInt("maximum");
            int minimum = rs.getInt("minimum");
            int count = rs.getInt("count");
            int rang = (count - minimum) / ((maximum - minimum) / 5);
            return new TagWithRangDto(getTagEntityAfterSQL(rs, rowNum), rang);
        });
    }


    public List<TagEntity> getTagsForBookBySlug(String slug) {
        return jdbcTemplate.query("select * from tag inner join book2tag on tag.id = book2tag.tag_id where book2tag.book_id=(select books.id from books where books.slug = :slug) ", Map.of( "slug", slug), this::getTagEntityAfterSQL);
    }

    public TagEntity getTagEntityAfterSQL(ResultSet rs, int rowNum) throws SQLException {
        TagEntity tag = new TagEntity();
        tag.setId(rs.getInt("id"));
        tag.setEnName(rs.getString("en_name"));
        tag.setRuName(rs.getString("ru_name"));
        tag.setSlug(rs.getString("slug"));
        return tag;
    }
}
