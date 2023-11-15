package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.BookstoreUser;
import com.example.MyBookShopApp.data.book.Book;
import com.example.MyBookShopApp.data.book.Book2User;
import com.example.MyBookShopApp.reposotories.BookstoreUserRepository;
import com.example.MyBookShopApp.reposotories.book.Book2UserRepository;
import com.example.MyBookShopApp.reposotories.book.BookRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@Slf4j
@Service
public class MyService {
    private final BookRepository bookRepository;
    private final BookstoreUserRegister userRegister;
    private final Book2UserRepository book2UserRepository;
    private final BookstoreUserRepository bookstoreUserRepository;

    public List<Book> getMyBookList(@AuthenticationPrincipal OAuth2User principal, Model model) {
        BookstoreUser currentUser = userRegister.getCurrentUser(principal);
        List<Book> myBooks = new ArrayList<>();
        List<Book2User> book2UserListPaid = book2UserRepository.findBook2UserByUserIdAndTypeId(currentUser.getId(), 3); //3 = paid
        for (Book2User book2User : book2UserListPaid) {
            myBooks.add(bookRepository.findBookById(book2User.getBook().getId()));
        }
        List<Book2User> book2UserListPromo = book2UserRepository.findBook2UserByUserIdAndTypeId(currentUser.getId(), 6); //6 = promo
        for (Book2User book2User : book2UserListPromo) {
            myBooks.add(bookRepository.findBookById(book2User.getBook().getId()));
        }
        model.addAttribute("myBooks", myBooks);
        return myBooks;
    }

    public List<Book> getPaidBookList(BookstoreUser user) {
        List<Book> paidBooks = new ArrayList<>();
        List<Book2User> book2UserListPaid = book2UserRepository.findBook2UserByUserIdAndTypeId(user.getId(), 3); //3 = paid
        for (Book2User book2User : book2UserListPaid) {
            paidBooks.add(bookRepository.findBookById(book2User.getBook().getId()));
        }
        return paidBooks;
    }

    public List<Book> getMyArchiveBookList(@AuthenticationPrincipal OAuth2User principal, Model model) {
        BookstoreUser currentUser = userRegister.getCurrentUser(principal);
        List<Book> myArchiveBooks = getArchiveBookList(currentUser);
        model.addAttribute("myArchiveBooks", myArchiveBooks);
        return myArchiveBooks;
    }

    public List<Book> getArchiveBookList(BookstoreUser user) {
        List<Book> myArchiveBooks = new ArrayList<>();
        List<Book2User> book2UserList = book2UserRepository.findBook2UserByUserIdAndTypeId(user.getId(), 4); //4 = ARC
        for (Book2User book2User : book2UserList) {
            myArchiveBooks.add(bookRepository.findBookById(book2User.getBook().getId()));
        }
        return myArchiveBooks;
    }

    public int getMyBookCount(int myCount, int myArchCount, Model model) {
        int myBookCount = myArchCount + myCount;
        model.addAttribute("myBookCount", myBookCount);
        return myBookCount;
    }

    public boolean isPaidBook(String slug, @AuthenticationPrincipal OAuth2User principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        BookstoreUser currentUser = userRegister.getCurrentUser(principal);
        Book book = bookRepository.findBookBySlug(slug);
        Book2User book2User = book2UserRepository.findBook2UserByBookIdAndUserId(book.getId(), currentUser.getId());
        if (book2User == null) {
            return false;
        }
        return book2User.getTypeId() == 3 || book2User.getTypeId() == 4;
    }

    public void changeBookStatusByStatusId(String slug, @AuthenticationPrincipal OAuth2User principal) {
        BookstoreUser currentUser = userRegister.getCurrentUser(principal);
        Book book = bookRepository.findBookBySlug(slug);
        Book2User book2User = book2UserRepository.findBook2UserByBookIdAndUserId(book.getId(), currentUser.getId());
        if (book2User.getTypeId() == 3) {
            book2User.setTypeId(4);
            book2UserRepository.save(book2User);
            log.info("CHANGE BOOK STATUS - ARCH!");
        } else if (book2User.getTypeId() == 4) {
            book2User.setTypeId(3);
            book2UserRepository.save(book2User);
            log.info("CHANGE BOOK STATUS - PAID!");
        } else {
            log.info("BOOK ISN'T PAID!");
        }
    }
}
