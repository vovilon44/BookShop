package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.data.struct.genre.GenreEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GenresService {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private GenreRepository genreRepository;

    @Autowired
    public GenresService(NamedParameterJdbcTemplate jdbcTemplate, GenreRepository genreRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreRepository = genreRepository;
    }

    public List<GenresDtoFirst> getAllGenres() {
        Map<Integer, GenreEntity> listDto = new HashMap<>();
        jdbcTemplate.query("select *, (select count(*) as book_count from book2genre where book2genre.genre_id = genre.id group by genre.id) from genre", (ResultSet rs, int rowNum) -> {
            GenreEntity genre = new GenreEntity();
            genre.setId(rs.getInt("id"));
            genre.setParentId(rs.getInt("parent_id"));
            genre.setSlug(rs.getString("slug"));
            genre.setName(rs.getString("name"));
            genre.setBookCount(rs.getInt("book_count"));
            listDto.put(genre.getId(),genre);
            return genre;
        });
        List<GenresDtoFirst> listFirst = new ArrayList<>();
        listDto.forEach((e, y)->{
            if (y.getParentId() == 0){
                List<GenresDtoSecond> secondList = new ArrayList<>();
                getListForFatherGenres(listDto, e).forEach(genre ->{
                    GenresDtoSecond genresDtoSecond = new GenresDtoSecond();
                    genresDtoSecond.setGenre(genre);
                    genresDtoSecond.setChildList(getListForFatherGenres(listDto, genre.getId()));
                    secondList.add(genresDtoSecond);
                });
                listFirst.add(new GenresDtoFirst(y, secondList));
            }
        });

        return listFirst;
    }

    private List<GenreEntity> getListForFatherGenres(Map<Integer, GenreEntity> map, int id){
        List<GenreEntity> list = new ArrayList<>();
        map.forEach((key, value)->{
           if (value.getParentId() == id){list.add(value);}
        });
        return list;
    }

    public GenreEntity getGenreEntityFromSlug(String slug){
        return genreRepository.findGenreEntityBySlug(slug);
    }

    public GenreEntity getGenreEntityFromId(Integer id){
        return genreRepository.findGenreEntityById(id);
    }

}
