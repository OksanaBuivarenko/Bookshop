package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.BookstoreUser;
import com.example.MyBookShopApp.data.book.Book;
import com.example.MyBookShopApp.data.book.BookReview;
import com.example.MyBookShopApp.data.book.BookReviewLike;
import com.example.MyBookShopApp.reposotories.BookstoreUserRepository;
import com.example.MyBookShopApp.reposotories.book.BookRepository;
import com.example.MyBookShopApp.reposotories.book.BookReviewLikeRepository;
import com.example.MyBookShopApp.reposotories.book.BookReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@Service
public class BookReviewService {
    private final BookRepository bookRepository;
    private final BookReviewRepository bookReviewRepository;
    private final BookReviewLikeRepository bookReviewLikeRepository;
    private final BookstoreUserRegister userRegister;
    private final BookstoreUserRepository bookstoreUserRepository;

    public void addReview(Book book, String text, @AuthenticationPrincipal OAuth2User principal, Model model) {
        BookstoreUser currentUser = userRegister.getCurrentUser(principal);
        BookstoreUser user = bookstoreUserRepository.findBookstoreUserById(currentUser.getId());
        if (user.getIsBlocked() == 1) {
            model.addAttribute("isBlocked", true);
        } else {
            BookReview bookReview = new BookReview();
            bookReview.setUser(user);
            bookReview.setBook(book);
            bookReview.setTime(LocalDateTime.now());
            bookReview.setText(text);
            bookReviewRepository.save(bookReview);
        }
    }

    public void addReviewLike(BookReview bookReview, Short value, @AuthenticationPrincipal OAuth2User principal) {
        BookstoreUser currentUser = userRegister.getCurrentUser(principal);
        BookReviewLike bookReviewLike = new BookReviewLike();
        bookReviewLike.setUser(currentUser);
        bookReviewLike.setBookReview(bookReview);
        bookReviewLike.setTime(LocalDateTime.now());
        bookReviewLike.setValue(value);
        bookReviewLikeRepository.save(bookReviewLike);
    }

    public Book getBook(String slug) {
        return bookRepository.findBookBySlug(slug);
    }

    public void getReviewLikeAndDislikeCount(BookReview bookReview) {
        for (BookReviewLike bookReviewLike : bookReview.getBookReviewLikes()) {
            if (bookReviewLike.getValue() == 1) {
                bookReview.setLikeCount(bookReview.getLikeCount() + 1);
            } else if (bookReviewLike.getValue() == -1) {
                bookReview.setDislikeCount(bookReview.getDislikeCount() + 1);
            }
        }
    }

    public void getReviewLikeRating(BookReview bookReview) {
        getReviewLikeAndDislikeCount(bookReview);
        if (bookReview.getLikeCount() == 0 && bookReview.getDislikeCount() == 0) {
            bookReview.setBookReviewLikeRating(0);
        } else {
            bookReview.setBookReviewLikeRating((int) Math.ceil(calculationReviewLikeRating(bookReview.getLikeCount(),
                    bookReview.getDislikeCount())));
        }
    }

    public void getReviewListLikeRating(String slug) {
        Book book = bookRepository.findBookBySlug(slug);
        for (BookReview bookReview : book.getBookReviewList()) {
            getReviewLikeRating(bookReview);
        }
    }

    public double calculationReviewLikeRating(int like, int dislike) {
        double rating = like / (double) (like + dislike) * 5;
        return rating;
    }

    public void getStarsColorsReview(BookReview bookReview) {
        List<String> startColorsReview = new ArrayList<>();
        String starsClass = "Rating-star";
        for (int i = 0; i < 5; i++) {
            startColorsReview.add(starsClass);
        }
        List<String> starsColorsReview = new ArrayList<>(startColorsReview);
        if (bookReview.getBookReviewLikeRating() != 0) {
            for (int i = 0; i < bookReview.getBookReviewLikeRating(); i++) {
                starsColorsReview.set(i, "Rating-star Rating-star_view");
                bookReview.setStarsColorsReview(starsColorsReview);
            }
        }
    }

    public void getStarsColorsReviewList(String slug) {
        Book book = bookRepository.findBookBySlug(slug);
        for (BookReview bookReview : book.getBookReviewList()) {
            getStarsColorsReview(bookReview);
        }
    }

    public boolean userIsBlocked(OAuth2User principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        BookstoreUser currentUser = userRegister.getCurrentUser(principal);
        BookstoreUser user = bookstoreUserRepository.findBookstoreUserById(currentUser.getId());
        return user.getIsBlocked() == 1;
    }
}
