package com.example.MyBookShopApp.data.repositories;

import com.example.MyBookShopApp.data.struct.genre.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


public interface GenreRepository extends JpaRepository<GenreEntity, Integer> {


    GenreEntity findGenreEntityBySlug(String slug);

    GenreEntity findGenreEntityById(Integer id);

    List<GenreEntity> findAllByOrderByParentIdAscId();


    }




