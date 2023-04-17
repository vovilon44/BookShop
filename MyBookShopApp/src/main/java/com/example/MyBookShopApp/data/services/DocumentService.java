package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.repositories.DocumentRepository;
import com.example.MyBookShopApp.data.struct.other.DocumentEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Autowired
    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }


    public List<DocumentEntity> getAllDocuments(){
       return documentRepository.findDocumentEntitiesByOrderBySortIndex();
    }

    public DocumentEntity getDocumentBySlug(String slug){
        return documentRepository.findDocumentEntityBySlug(slug);
    }
}
