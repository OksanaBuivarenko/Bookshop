package com.example.MyBookShopApp.reposotories.book;

import com.example.MyBookShopApp.data.book.BookRatingGrade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRatingGradeRepository extends JpaRepository<BookRatingGrade, Integer> {
}
