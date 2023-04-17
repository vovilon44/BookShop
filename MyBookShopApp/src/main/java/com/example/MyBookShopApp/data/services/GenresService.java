package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.repositories.GenreRepository;
import com.example.MyBookShopApp.data.struct.genre.GenreEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

@Service
public class GenresService {


    private GenreRepository genreRepository;

    @Autowired
    public GenresService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }


    //--------------------------------------------yea
    public GenreEntity getGenreEntityFromSlug(String slug){
        return genreRepository.findGenreEntityBySlug(slug);
    }
    //--------------------------------------------yea
    public ArrayList<GenreEntity> getGenreTree(String genreSlug){
        GenreEntity genre = getGenreEntityFromSlug(genreSlug);
        ArrayList<GenreEntity> genreTree;
        if (genre.getParentId() != null){
            genreTree = getGenreTree(new ArrayList<>(), genre.getParentId());
            Collections.reverse(genreTree);
        } else {
            return new ArrayList<>();
        }
        return genreTree;
    }

    private ArrayList<GenreEntity> getGenreTree(ArrayList<GenreEntity> list, Integer id){
        GenreEntity genre = getGenreEntityFromId(id);
        list.add(genre);
        if (genre.getParentId() != null) {
            getGenreTree(list, genre.getParentId());
        } else {
            return list;
        }
        return list;
    }

    public GenreEntity getGenreEntityFromId(Integer id){
        return genreRepository.findGenreEntityById(id);
    }


    public List<GenreEntity> getAllGenres() {
        List<GenreEntity> genres = genreRepository.findAll();
        genres.forEach(e -> {
            for (GenreEntity genre : genres) {
                if (genre.getParentId() != null && genre.getParentId() == e.getId()) {
                    e.setCountChildren(e.getCountChildren() + 1);
                }
            }
        });
        return genres;
    }


//        return genreRepository.findAllByOrderByParentIdDesc();

}
