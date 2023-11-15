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
public class CartService {
    private final BookRepository bookRepository;
    private final BookstoreUserRegister userRegister;
    private final Book2UserRepository book2UserRepository;
    private final PaymentService paymentService;

    public void cartPage(String cartContents, Model model, @AuthenticationPrincipal OAuth2User principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            if (cartContents == null || cartContents.equals("")) {
                model.addAttribute("isCartEmpty", true);
            } else {
                model.addAttribute("isCartEmpty", false);
                cartContents = cartContents.startsWith("/") ? cartContents.substring(1) : cartContents;
                cartContents = cartContents.endsWith("/") ? cartContents.substring(0, cartContents.length() - 1) : cartContents;
                String[] cookieSlugs = cartContents.split("/");
                List<Book> booksFromCookieSlugs = bookRepository.findBooksBySlugIn(cookieSlugs);
                model.addAttribute("bookCart", booksFromCookieSlugs);
                model.addAttribute("paymentSum", paymentService.getPaymentSumTotal(booksFromCookieSlugs));
            }
        } else {
            BookstoreUser currentUser = userRegister.getCurrentUser(principal);
            List<Book> cartBooks = new ArrayList<>();
            List<Book2User> book2UserList = book2UserRepository.findBook2UserByUserIdAndTypeId(currentUser.getId(), 2);
            for (Book2User book2User : book2UserList) {
                cartBooks.add(bookRepository.findBookById(book2User.getBook().getId()));
            }
            model.addAttribute("bookCart", cartBooks);
            model.addAttribute("paymentSum", paymentService.getPaymentSumTotal(cartBooks));
        }
    }

    public void removeBookFromCart(String slug, String cartContents, HttpServletResponse response,
                                   Model model, @AuthenticationPrincipal OAuth2User principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            if (cartContents != null && !cartContents.equals("")) {
                ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(cartContents.split("/")));
                cookieBooks.remove(slug);
                Cookie cookie = new Cookie("cartContents", String.join("/", cookieBooks));
                cookie.setPath("/");
                response.addCookie(cookie);
                model.addAttribute("isCartEmpty", false);
            } else {
                model.addAttribute("isCartEmpty", true);
            }
        } else {
            BookstoreUser currentUser = userRegister.getCurrentUser(principal);
            Book book = bookRepository.findBookBySlug(slug);
            Book2User book2User = book2UserRepository.findBook2UserByBookIdAndUserId(book.getId(), currentUser.getId());
            book2User.setTypeId(5);
            book2UserRepository.save(book2User);
        }
    }

    public void changeBookStatus(String slug, String cartContents, HttpServletResponse response, Model model,
                                 @AuthenticationPrincipal OAuth2User principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            if (cartContents == null || cartContents.equals("")) {
                Cookie cookie = new Cookie("cartContents", slug);
                cookie.setPath("/");
                response.addCookie(cookie);
                model.addAttribute("isCartEmpty", false);
            } else if (!cartContents.contains(slug)) {
                StringJoiner stringJoiner = new StringJoiner("/");
                stringJoiner.add(cartContents).add(slug);
                Cookie cookie = new Cookie("cartContents", stringJoiner.toString());
                cookie.setPath("/");
                response.addCookie(cookie);
                model.addAttribute("isCartEmpty", false);
            }
        } else {
            BookstoreUser currentUser = userRegister.getCurrentUser(principal);
            Book book = bookRepository.findBookBySlug(slug);
            Book2User book2User = book2UserRepository.findBook2UserByBookIdAndUserId(book.getId(), currentUser.getId());
            book2User.setTypeId(2);
            book2UserRepository.save(book2User);
        }
    }

    public void changeAllBookStatus(String cartContents, String contents, HttpServletResponse response, Model model,
                                    @AuthenticationPrincipal OAuth2User principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            if (cartContents == null || cartContents.equals("")) {
                ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(contents.split("/")));
                for (String slug : cookieBooks) {
                    Cookie cookie = new Cookie("cartContents", String.join("/", cookieBooks));
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            } else {
                StringJoiner stringJoiner = new StringJoiner("/");
                ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(contents.split("/")));
                for (String slug : cookieBooks) {
                    stringJoiner.add(cartContents).add(slug);
                    Cookie cookie = new Cookie("cartContents", stringJoiner.toString());
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
            model.addAttribute("isCartEmpty", false);
        } else {
            BookstoreUser currentUser = userRegister.getCurrentUser(principal);
            List<Book2User> book2UserList = book2UserRepository.findBook2UserByUserIdAndTypeId(currentUser.getId(), 1);
            for (Book2User book2User : book2UserList) {
                book2User.setTypeId(2);
                book2UserRepository.save(book2User);
            }
        }
    }

    public void cartCount(String cartContents, Model model, @AuthenticationPrincipal OAuth2User principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int cartCount;
        if (authentication instanceof AnonymousAuthenticationToken) {
            if (cartContents == null ) {
                cartCount = 0;
            } else {
                cartContents = cartContents.startsWith("/") ? cartContents.substring(1) : cartContents;
                cartContents = cartContents.endsWith("/") ? cartContents.substring(0, cartContents.length() - 1) : cartContents;
                String[] cookieSlugs = cartContents.split("/");
                cartCount = cookieSlugs.length;
            }
        } else {
            BookstoreUser currentUser = userRegister.getCurrentUser(principal);
            cartCount = book2UserRepository.countBook2UserByUserIdAndTypeId(currentUser.getId(), 2);
        }
        model.addAttribute("countCartBook", cartCount);
    }

    public boolean payBooks(@AuthenticationPrincipal OAuth2User principal) {
        BookstoreUser currentUser = userRegister.getCurrentUser(principal);
        List<Book> cartBooks = new ArrayList<>();
        List<Book2User> book2UserList = book2UserRepository.findBook2UserByUserIdAndTypeId(currentUser.getId(), 2);
        for (Book2User book2User : book2UserList) {
            cartBooks.add(bookRepository.findBookById(book2User.getBook().getId()));
        }

        int paymentSumTotal = paymentService.getPaymentSumTotal(cartBooks);
        if (currentUser.getBalance() >= paymentSumTotal) {
            paymentService.BalanceDebit(currentUser, paymentSumTotal);
            paymentService.debitTransactions(currentUser, cartBooks);
            for (Book2User book2User : book2UserList) {
                book2User.setTypeId(3);
                book2UserRepository.save(book2User);
            }
            return true;
        } else {
            System.out.println("Недостаточно средств");
            return false;
        }
    }
}
