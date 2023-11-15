package com.example.MyBookShopApp.reposotories.book;

import com.example.MyBookShopApp.data.book.BookFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookFileRepository extends JpaRepository<BookFile, Integer> {
    public BookFile findBookFileByHash(String hash);
}
