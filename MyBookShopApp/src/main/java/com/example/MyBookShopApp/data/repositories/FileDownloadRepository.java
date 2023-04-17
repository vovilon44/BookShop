package com.example.MyBookShopApp.data.repositories;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.data.struct.book.file.FileDownloadEntity;
import com.example.MyBookShopApp.security.BookstoreUser;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FileDownloadRepository extends JpaRepository<FileDownloadEntity, Integer> {

    FileDownloadEntity findByBookAndUser(Book book, BookstoreUser user);

    FileDownloadEntity findByBook_SlugAndUser_Id(String slug, Integer userId);
    FileDownloadEntity findByBook_IdAndUser_Id(Integer bookId, Integer userId);
    FileDownloadEntity findFileDownloadEntityByBook_IdAndUser_Id(Integer bookId, Integer userId);
}
