package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.repositories.MessageRepository;
import com.example.MyBookShopApp.data.struct.other.MessageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void saveContactAuthorized(MessageEntity message){
        messageRepository.save(message);
    }
}
