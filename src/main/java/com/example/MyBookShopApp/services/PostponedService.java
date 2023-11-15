package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.book.Book;
import com.example.MyBookShopApp.data.book.Book2User;
import com.example.MyBookShopApp.reposotories.book.Book2UserRepository;
import com.example.MyBookShopApp.reposotories.book.BookRepository;
import com.example.MyBookShopApp.data.BookstoreUser;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
@AllArgsConstructor
@Service
public class PostponedService {
    private final BookRepository bookRepository;
    private final BookstoreUserRegister userRegister;
    private final Book2UserRepository book2UserRepository;

    public void postponedPage(String postponedContents, Model model, @AuthenticationPrincipal OAuth2User principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            if (postponedContents == null || postponedContents.equals("")) {
                model.addAttribute("isPostponedEmpty", true);
            } else {
                model.addAttribute("isPostponedEmpty", false);
                postponedContents = postponedContents.startsWith("/") ? postponedContents.substring(1) : postponedContents;
                postponedContents = postponedContents.endsWith("/") ? postponedContents.substring(0, postponedContents.length() - 1) : postponedContents;
                String[] cookieSlugs = postponedContents.split("/");
                List<Book> booksFromCookieSlugs = bookRepository.findBooksBySlugIn(cookieSlugs);
                model.addAttribute("bookPostponed", booksFromCookieSlugs);
            }
        } else {
            BookstoreUser currentUser = userRegister.getCurrentUser(principal);
            List<Book> postponedBooks = new ArrayList<>();
            List<Book2User> book2UserList = book2UserRepository.findBook2UserByUserIdAndTypeId(currentUser.getId(), 1);
            for (Book2User book2User : book2UserList) {
                postponedBooks.add(bookRepository.findBookById(book2User.getBook().getId()));
            }
            model.addAttribute("bookPostponed", postponedBooks);
        }
    }

    public void removeBookFromPostponedRequest(String slug, String postponedContents, HttpServletResponse response,
                                               Model model, @AuthenticationPrincipal OAuth2User principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            if (postponedContents != null && !postponedContents.equals("")) {
                ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(postponedContents.split("/")));
                cookieBooks.remove(slug);
                Cookie cookie = new Cookie("postponedContents", String.join("/", cookieBooks));
                cookie.setPath("/");
                response.addCookie(cookie);
                model.addAttribute("isPostponedEmpty", false);
            } else {
                model.addAttribute("isPostponedEmpty", true);
            }
        } else {
            BookstoreUser currentUser = userRegister.getCurrentUser(principal);
            Book book = bookRepository.findBookBySlug(slug);
            Book2User book2User = book2UserRepository.findBook2UserByBookIdAndUserId(book.getId(), currentUser.getId());
            book2User.setTypeId(5);
            book2UserRepository.save(book2User);
        }
    }

    public void removeAllBookFromPostponedRequest(String postponedContents, HttpServletResponse response, Model model,
                                                  @AuthenticationPrincipal OAuth2User principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            if (postponedContents != null && !postponedContents.equals("")) {
                postponedContents = "";
                Cookie cookie = new Cookie("postponedContents", String.join("/", postponedContents));
                cookie.setPath("/");
                response.addCookie(cookie);
            }
            model.addAttribute("isPostponedEmpty", true);
        }
    }

    public void changeBookStatusPostponed(String slug, String postponedContents, HttpServletResponse response,
                                          Model model, @AuthenticationPrincipal OAuth2User principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            if (postponedContents == null || postponedContents.equals("")) {
                Cookie cookie = new Cookie("postponedContents", slug);
                cookie.setPath("/");
                response.addCookie(cookie);
                model.addAttribute("isPostponedEmpty", false);
            } else if (!postponedContents.contains(slug)) {
                StringJoiner stringJoiner = new StringJoiner("/");
                stringJoiner.add(postponedContents).add(slug);
                Cookie cookie = new Cookie("postponedContents", stringJoiner.toString());
                cookie.setPath("/");
                response.addCookie(cookie);
                model.addAttribute("isPostponedEmpty", false);
            }
        } else {
            BookstoreUser currentUser = userRegister.getCurrentUser(principal);
            Book book = bookRepository.findBookBySlug(slug);
            Book2User book2User = book2UserRepository.findBook2UserByBookIdAndUserId(book.getId(), currentUser.getId());
            book2User.setTypeId(1);
            book2UserRepository.save(book2User);
        }
    }

    public void postponedCount(String postponedContents,
                               Model model, @AuthenticationPrincipal OAuth2User principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int postponedCount;
        if (authentication instanceof AnonymousAuthenticationToken) {
            if (postponedContents == null) {
                postponedCount = 0;
            } else {
                postponedContents = postponedContents.startsWith("/") ? postponedContents.substring(1) : postponedContents;
                postponedContents = postponedContents.endsWith("/") ? postponedContents.substring(0, postponedContents.length() - 1) : postponedContents;
                String[] cookieSlugs = postponedContents.split("/");
                postponedCount = cookieSlugs.length;
            }
        } else {
            BookstoreUser currentUser = userRegister.getCurrentUser(principal);
            postponedCount = book2UserRepository.countBook2UserByUserIdAndTypeId(currentUser.getId(), 1);
        }
        model.addAttribute("countPostponedBook", postponedCount);
    }
}
