package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface BookstoreUserRepository extends JpaRepository<BookstoreUser, Integer>
{
    BookstoreUser findBookstoreUserByEmail(String email);
    BookstoreUser findBookstoreUserByPhone(String phone);
    BookstoreUser findBookstoreUserById(Integer id);

    BookstoreUser findBookstoreUserByEmailOrPhone(String email, String phone);


}