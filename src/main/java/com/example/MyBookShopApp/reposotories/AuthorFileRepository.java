package com.example.MyBookShopApp.reposotories;

import com.example.MyBookShopApp.data.AuthorFile;
import com.example.MyBookShopApp.data.book.BookFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorFileRepository extends JpaRepository<AuthorFile, Integer> {
    public AuthorFile findAuthorFileByHash(String hash);
}
