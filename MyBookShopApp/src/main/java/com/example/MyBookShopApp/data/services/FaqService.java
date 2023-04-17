package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.repositories.FaqRepository;

import com.example.MyBookShopApp.data.struct.other.FaqEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FaqService {

    private final FaqRepository faqRepository;

    @Autowired
    public FaqService(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    public List<FaqEntity> getAllFaqEntities(){
        return faqRepository.findByOrderBySortIndex();
    }
}
