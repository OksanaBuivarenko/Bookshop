package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.data.book.Book;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BooksPageDto {
    private Integer count;
    private List<Book> books;

    public BooksPageDto(List<Book> books) {
        this.books = books;
        this.count = books.size();
    }
}
