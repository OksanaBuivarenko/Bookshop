package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.book.Book;
import com.example.MyBookShopApp.reposotories.book.BookRepository;
import com.example.MyBookShopApp.data.Genre;
import com.example.MyBookShopApp.reposotories.GenreRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@Service
public class GenreService {
    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;

    public List<Genre> getGenreWithChildList() {
        List<Genre> genres = genreRepository.findGenreByParentIdIsNull();
        return genres;
    }

    public List<Genre> getGenre() {
        List<Genre> genres = genreRepository.findAll();
        return genres;
    }

    public Page<Book> getPageBooksOfGenre(String genreName, Integer offset, Integer limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        Genre genre = genreRepository.findGenreByGenreName(genreName);
        if (genre.getChildren().isEmpty()) {
            return bookRepository.findBooksByGenresGenreName(genreName, pageable);
        }
        List<Book> bookList = new ArrayList<>();
        addBookToListBooksOfGenre(genre, bookList);
        PagedListHolder page = new PagedListHolder(bookList);
        page.setPage(pageable.getPageNumber());
        page.setPageSize(pageable.getPageSize());
        Page<Book> bookPage = new PageImpl<>(page.getPageList());
        return bookPage;
    }

    public void addBookToListBooksOfGenre(Genre genre, List<Book> bookList) {
        if (genre.getChildren().isEmpty()) {
            for (Book book : genre.getBooks()) {
                bookList.add(book);
            }
        } else {
            for (Genre child : genre.getChildren()) {
                addBookToListBooksOfGenre(child, bookList);
            }
        }
    }
}
