package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.book.Book;
import com.example.MyBookShopApp.reposotories.book.Book2UserRepository;
import com.example.MyBookShopApp.reposotories.book.BookRepository;
import com.example.MyBookShopApp.data.BookstoreUser;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
@AllArgsConstructor
@Service
public class RecentlyViewedService {
    private final BookstoreUserRegister userRegister;
    private final Book2UserRepository book2UserRepository;
    private final BookRepository bookRepository;

    public Page<Book> getPageOfRecentlyViewedBooks(Integer offset, Integer limit,
                                                   @AuthenticationPrincipal OAuth2User principal) {
        Pageable nextPage = PageRequest.of(offset, limit);
        BookstoreUser currentUser = userRegister.getCurrentUser(principal);
        return bookRepository.findBookByUsersIdAndBook2UsersTypeIdOrderByBook2UsersTimeAsc(currentUser.getId(), 5, nextPage);
    }
}
