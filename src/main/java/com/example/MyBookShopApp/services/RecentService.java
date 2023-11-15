package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.book.Book;
import com.example.MyBookShopApp.reposotories.book.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
@AllArgsConstructor
@Service
public class RecentService {
    private BookRepository bookRepository;

    public Page<Book> getPageOfRecentBooks(Date dateFrom, Date dateTo, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findBooksByPubDateBetweenOrderByPubDateDesc(dateFrom, dateTo, nextPage);
    }

    public Date convertDate(String dates) {
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd.MM.yyyy");
        Date date = null;
        try {
            date = format.parse(dates);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return date;
    }

    public Page<Book> getPageOfRecentBooksWithoutDate(Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findAllByOrderByPubDateAsc(nextPage);
    }
}
