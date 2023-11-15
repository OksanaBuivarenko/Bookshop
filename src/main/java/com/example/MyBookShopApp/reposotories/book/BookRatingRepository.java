package com.example.MyBookShopApp.reposotories.book;

import com.example.MyBookShopApp.data.book.BookRating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRatingRepository extends JpaRepository<BookRating, Integer> {
}
