package com.example.MyBookShopApp.reposotories;

import com.example.MyBookShopApp.data.EmailLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailLinksRepository extends JpaRepository<EmailLink, Long> {
    public EmailLink findByLink(String link);
}
