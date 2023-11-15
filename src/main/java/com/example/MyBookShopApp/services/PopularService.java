package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.book.Book;
import com.example.MyBookShopApp.data.book.Book2User;
import com.example.MyBookShopApp.reposotories.book.Book2UserRepository;
import com.example.MyBookShopApp.reposotories.book.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@AllArgsConstructor
@Service
public class PopularService {
    private final BookRepository bookRepository;
    private final Book2UserRepository book2UserRepository;

    public Page<Book> getPageOfPopularBooks(Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        List<Book2User> books = book2UserRepository.findAll();
        Map<Book, List<Book2User>> map = books.stream().collect(Collectors.groupingBy(Book2User::getBook));
        for (Map.Entry<Book, List<Book2User>> item : map.entrySet()) {
            int paid = 0;
            int cart = 0;
            int kept = 0;
            int viewed = 0;
            int popularity = 0;
            for (Book2User book2User : item.getValue()) {
                if (book2User.getTypeId() == 3) {
                    paid += 10;
                }
                if (book2User.getTypeId() == 2) {
                    cart += 10;
                }
                if (book2User.getTypeId() == 1) {
                    kept += 10;
                }
                if (book2User.getTypeId() == 5 && isRecentlyViewed(book2User.getTime())) {
                    viewed += 10;
                }
            }
            popularity = calculationPopularity(paid, cart, kept, viewed);
            Book bookUpdate = item.getKey();
            bookUpdate.setRatingPopularity(popularity);
            bookRepository.save(bookUpdate);
        }
        return bookRepository.findAllByOrderByRatingPopularityDesc(nextPage);
    }

    public int calculationPopularity(int paid, int cart, int kept, int viewed) {
        int popularity = (int) (paid + 0.7 * cart + 0.4 * kept + 0.2 * viewed);
        return popularity;
    }

    public boolean isRecentlyViewed(LocalDateTime time) {
        LocalDateTime recentTime = LocalDateTime.now().minusMonths(1);
        return time.isAfter(recentTime);
    }
}


