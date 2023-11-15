package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.book.Book;
import com.example.MyBookShopApp.data.book.BookReview;
import com.example.MyBookShopApp.reposotories.book.BookRepository;
import com.example.MyBookShopApp.reposotories.book.BookReviewRepository;
import com.example.MyBookShopApp.services.BookReviewService;
import com.example.MyBookShopApp.services.BookService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@Controller
public class BookReviewController {
    private final BookRepository bookRepository;
    private final BookReviewRepository bookReviewRepository;
    private final BookReviewService bookReviewService;
    private final BookService bookService;

    @ModelAttribute("slugBook")
    public Book slugBook() {
        return new Book();
    }

    @ModelAttribute("starsColor")
    public List<String> starsColors() {
        return new ArrayList<>();
    }

    @ModelAttribute("bookReviews")
    public List<BookReview> bookReviews() {
        return new ArrayList<>();
    }

    @PostMapping("/bookReview")
    public String addBookReview(@RequestParam("bookid") String slug, @RequestParam("text") String text,
                                @AuthenticationPrincipal OAuth2User principal, Model model) {
        Book book = bookRepository.findBookBySlug(slug);
        bookReviewService.addReview(book, text, principal, model);
        model.addAttribute("bookReviews", bookReviewService.getBook(slug).getBookReviewList());
        model.addAttribute("slugBook", bookReviewService.getBook(slug));
        model.addAttribute("starsColor", bookService.starsColors(bookReviewService.getBook(slug).getBookRating().getRating()));
        return ("redirect:/books/" + slug);
    }

    @PostMapping("/rateBookReview")
    public String addReviewLike(@RequestParam("reviewid") Integer reviewId, @RequestParam("value") Short value,
                                @AuthenticationPrincipal OAuth2User principal, Model model) {
        BookReview bookReview = bookReviewRepository.findBookReviewById(reviewId);
        Book book = bookReview.getBook();
        bookReviewService.addReviewLike(bookReview, value, principal);
        bookReviewService.getReviewLikeRating(bookReview);
        bookReviewService.getStarsColorsReview(bookReview);
        model.addAttribute("bookReviews", book.getBookReviewList());
        model.addAttribute("slugBook", book);
        model.addAttribute("starsColor", bookService.starsColors(book.getBookRating().getRating()));
        return ("redirect:/books/" + book.getSlug());
    }
}
