package com.example.MyBookShopApp.data.repositories;

import com.example.MyBookShopApp.data.struct.other.FaqEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqRepository extends JpaRepository <FaqEntity, Integer> {

    List<FaqEntity> findByOrderBySortIndex();

}
