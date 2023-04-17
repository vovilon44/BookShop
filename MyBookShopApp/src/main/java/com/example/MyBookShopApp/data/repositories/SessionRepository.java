package com.example.MyBookShopApp.data.repositories;

import com.example.MyBookShopApp.data.SessionEntity;
import com.example.MyBookShopApp.security.BookstoreUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<SessionEntity, Integer> {

    List<SessionEntity> findAllByUserOrderByCountDay(BookstoreUser user);

    SessionEntity findByToken(String token);
    SessionEntity findSessionEntityById(Integer sessionId);

    List<SessionEntity> findAllByIdIn(Integer[] ids);

}
