package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.data.struct.tag.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<TagEntity, Integer>
{

    TagEntity findBySlug(String slug);
}
