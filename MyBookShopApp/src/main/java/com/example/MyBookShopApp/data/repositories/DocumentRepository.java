package com.example.MyBookShopApp.data.repositories;

import com.example.MyBookShopApp.data.struct.other.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DocumentRepository extends JpaRepository<DocumentEntity, Integer>  {

    List<DocumentEntity> findDocumentEntitiesByOrderBySortIndex();

    DocumentEntity findDocumentEntityBySlug(String slug);

}
