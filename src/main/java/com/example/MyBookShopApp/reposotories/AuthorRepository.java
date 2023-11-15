package com.example.MyBookShopApp.reposotories;

import com.example.MyBookShopApp.data.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Integer> {

    Author findAuthorByName(String name);

    Author findAuthorBySlug(String slug);
}

