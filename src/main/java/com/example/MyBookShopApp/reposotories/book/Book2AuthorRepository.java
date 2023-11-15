package com.example.MyBookShopApp.reposotories.book;

import com.example.MyBookShopApp.data.BookstoreUser;
import com.example.MyBookShopApp.data.book.Book2Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Book2AuthorRepository extends JpaRepository<Book2Author, Integer> {
    List<Book2Author> findAllByBookId(Integer id);
}
