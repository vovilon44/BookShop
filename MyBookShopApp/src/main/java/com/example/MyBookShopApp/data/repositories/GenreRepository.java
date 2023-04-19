package com.example.MyBookShopApp.data.repositories;

import com.example.MyBookShopApp.data.struct.genre.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface GenreRepository extends JpaRepository<GenreEntity, Integer> {


    GenreEntity findGenreEntityBySlug(String slug);

    GenreEntity findGenreEntityById(Integer id);

    List<GenreEntity> findAllByOrderByParentIdAscIdDesc();



}




