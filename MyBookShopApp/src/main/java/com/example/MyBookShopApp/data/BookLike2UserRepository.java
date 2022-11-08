package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.data.struct.book.links.BookLike2UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookLike2UserRepository extends JpaRepository<BookLike2UserEntity, Integer>
{
    List<BookLike2UserEntity> findBookLike2UserEntitiesByBookIdIs(Integer id);
}
