package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.Author;
import com.example.MyBookShopApp.reposotories.AuthorRepository;
import com.example.MyBookShopApp.data.book.Book;
import com.example.MyBookShopApp.reposotories.book.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@AllArgsConstructor
@Service
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public Map<String, List<Author>> getAuthorsMap() {
        return authorRepository.findAll().
                stream().collect(Collectors.groupingBy((Author a) -> {
                    return a.getName().substring(0, 1);
                }));
    }

    public Page<Book> getPageBooksOfAuthor(String slug, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findBookByAuthorsSlug(slug, nextPage);
    }

    public Author getAuthorBySlug(String slug) {
        return authorRepository.findAuthorBySlug(slug);
    }

    public void saveAuthor(Author author) {
        authorRepository.save(author);
    }

    public List<String> getParagraphs(String slug) {
        List<String> paragraphs = new ArrayList<>();
        String paragraph1 = "";
        String paragraph2 = "";
        String startString = authorRepository.findAuthorBySlug(slug).getDescription();
        if (startString.length() < 400) {
            paragraphs.add(startString);
            return paragraphs;
        }
        String[] sentences = startString.split("\\.");//
        for (int i = 0; i < sentences.length; i++) {
            if (i <= sentences.length / 2) {
                paragraph1 += sentences[i] + ". ";
            } else {
                paragraph2 += sentences[i] + ". ";
            }
        }
        paragraphs.add(paragraph1);
        paragraphs.add(paragraph2);
        return paragraphs;
    }
}
