package com.example.MyBookShopApp.reposotories;

import com.example.MyBookShopApp.data.Genre;
import com.example.MyBookShopApp.data.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Integer> {

    List<Genre> findGenreByParentIdIsNull();

    List<Genre> findAll();

    Genre findGenreByGenreName(String genreName);
}

