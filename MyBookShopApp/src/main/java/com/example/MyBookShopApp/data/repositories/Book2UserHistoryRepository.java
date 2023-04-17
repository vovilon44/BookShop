package com.example.MyBookShopApp.data.repositories;

import com.example.MyBookShopApp.data.struct.book.links.Book2UserHistory;
import com.example.MyBookShopApp.security.BookstoreUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Book2UserHistoryRepository extends JpaRepository<Book2UserHistory, Integer> {
    Book2UserHistory findByBook_SlugAndUser(String slug, BookstoreUser user);
}
