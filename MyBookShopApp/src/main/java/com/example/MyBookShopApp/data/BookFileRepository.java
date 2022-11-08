package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.data.struct.book.file.BookFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookFileRepository extends JpaRepository<BookFile, Integer>
{
    public BookFile findBookFileByHash(String hash);
}
