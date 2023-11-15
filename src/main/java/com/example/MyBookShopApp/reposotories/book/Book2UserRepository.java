package com.example.MyBookShopApp.reposotories.book;

import com.example.MyBookShopApp.data.book.Book2User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Book2UserRepository extends JpaRepository<Book2User, Integer> {
    List<Book2User> findAll();

    List<Book2User> findBook2UserByUserIdAndTypeId(int userId, int typeId);

    Book2User findBook2UserByBookIdAndUserId(int bookId, int userId);

    int countBook2UserByUserIdAndTypeId(int userId, int typeId);
}