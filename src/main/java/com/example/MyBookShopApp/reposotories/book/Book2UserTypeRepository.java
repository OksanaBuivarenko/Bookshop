package com.example.MyBookShopApp.reposotories.book;

import com.example.MyBookShopApp.data.book.Book2UserType;
import com.example.MyBookShopApp.data.book.BookType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Book2UserTypeRepository extends JpaRepository<Book2UserType, Integer> {
    Book2UserType findBook2UserTypeByTypeName(String typeName);
}
