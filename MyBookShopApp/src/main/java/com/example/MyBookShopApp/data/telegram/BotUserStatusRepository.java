package com.example.MyBookShopApp.data.telegram;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BotUserStatusRepository extends JpaRepository<BotUser, Long> {

    BotUser findBotUserById(Long userId);
}
