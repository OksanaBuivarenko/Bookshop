package com.example.MyBookShopApp.reposotories.book;

import com.example.MyBookShopApp.data.book.Book2Genre;
import com.example.MyBookShopApp.data.book.Book2User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Book2GenreRepository extends JpaRepository<Book2Genre, Integer> {
    List<Book2Genre> findAllByBookId(Integer id);
}
