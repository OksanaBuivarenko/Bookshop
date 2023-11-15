package com.example.MyBookShopApp.reposotories.book;

import com.example.MyBookShopApp.data.book.BookReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookReviewLikeRepository extends JpaRepository<BookReviewLike, Integer> {
}
