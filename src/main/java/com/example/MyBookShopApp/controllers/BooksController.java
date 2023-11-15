package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.book.Book;
import com.example.MyBookShopApp.data.book.BookReview;
import com.example.MyBookShopApp.data.BookstoreUser;
import com.example.MyBookShopApp.dto.WordDto;
import com.example.MyBookShopApp.services.*;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
@AllArgsConstructor
@Controller
@RequestMapping("/books")
public class BooksController {
    private final ResourceStorage storage;
    private final BookService bookService;
    private final BookReviewService bookReviewService;
    private final BookstoreUserRegister userRegister;
    private final PostponedService postponedService;
    private final CartService cartService;
    private final MyService myService;

    @ModelAttribute("slugBook")
    public Book slugBook() {
        return new Book();
    }

    @ModelAttribute("starsColor")
    public List<String> starsColors() { return new ArrayList<>(); }

    @ModelAttribute("bookReviews")
    public List<BookReview> bookReviews() { return new ArrayList<>(); }

    @GetMapping("/{slug}")
    public String bookPage(@PathVariable("slug") String slug, Model model, @AuthenticationPrincipal OAuth2User principal,
                           @CookieValue(name = "postponedContents", required = false) String postponedContents,
                           @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model,principal);
        cartService.cartCount(cartContents, model, principal);
        bookReviewService.getReviewListLikeRating(slug);
        bookReviewService.getStarsColorsReviewList(slug);
        if (bookReviewService.userIsBlocked(principal)){
            model.addAttribute("isBlocked", true);
        }
        else {
        model.addAttribute("isBlocked", false);
        }
        if (userRegister.isAdmin(principal)){
            model.addAttribute("isAdmin", true);
        }
        else {
            model.addAttribute("isAdmin", false);
        }
        model.addAttribute("bookCount", new WordDto());
        model.addAttribute("userCheckAmount", new WordDto());
        model.addAttribute("bookReviews", bookReviewService.getBook(slug).getBookReviewList());
        model.addAttribute("slugBook", bookReviewService.getBook(slug));
        model.addAttribute("starsColor", bookService.starsColors(bookReviewService.getBook(slug).getBookRating().getRating()));
        model.addAttribute("changeBookForm", bookReviewService.getBook(slug));
        if (myService.isPaidBook(slug, principal)) {
            model.addAttribute("isPaid", true);
        }
        else {
            model.addAttribute("isPaid", false);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken){
            return "books/slugnoauth";
        }
        else {
            BookstoreUser currentUser = userRegister.getCurrentUser(principal);
            bookService.addViewedBook(slug, currentUser);
            return "/books/slug";
        }
    }

    @PostMapping("/rateBook")
    public String changeBookRating(@RequestParam("bookId") String slug, @RequestParam("value") String grade, Model model,
                                   @AuthenticationPrincipal OAuth2User principal){
        bookService.changeRating(bookReviewService.getBook(slug),Integer.parseInt(grade));
        if (userRegister.isAdmin(principal)){
            model.addAttribute("isAdmin", true);
        }
        else {
            model.addAttribute("isAdmin", false);
        }
        return "/books/slug";
    }

    @GetMapping("/download/{hash}")
    public ResponseEntity<ByteArrayResource> bookFile(@PathVariable("hash") String hash) throws IOException {

        Path path = storage.getBookFilePath(hash);
        Logger.getLogger(this.getClass().getSimpleName()).info("book file path: " + path);

        MediaType mediaType = storage.getBookFileMime(hash);
        Logger.getLogger(this.getClass().getSimpleName()).info("book file mime type: " + mediaType);

        byte[] data = storage.getBookFileByteArray(hash);
        Logger.getLogger(this.getClass().getSimpleName()).info("book file data len: " + data.length);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + path.getFileName().toString())
                .contentType(mediaType)
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
    }
}
