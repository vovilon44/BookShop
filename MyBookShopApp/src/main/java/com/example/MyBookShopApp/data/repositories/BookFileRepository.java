package com.example.MyBookShopApp.data.repositories;

import com.example.MyBookShopApp.data.struct.book.file.BookFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookFileRepository extends JpaRepository<BookFile, Integer>
{
    public BookFile findBookFileByHash(String hash);
    public BookFile findBookFileByBook_IdAndTypeIdIs(Integer bookId, Integer typeId);
}
