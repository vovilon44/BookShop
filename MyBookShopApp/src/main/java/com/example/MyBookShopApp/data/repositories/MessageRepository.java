package com.example.MyBookShopApp.data.repositories;

import com.example.MyBookShopApp.data.struct.other.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {

}
