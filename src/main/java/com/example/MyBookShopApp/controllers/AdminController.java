package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.Author;
import com.example.MyBookShopApp.data.book.Book;
import com.example.MyBookShopApp.dto.BookDto;
import com.example.MyBookShopApp.dto.WordDto;
import com.example.MyBookShopApp.services.*;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
@AllArgsConstructor
@Controller
public class AdminController {
    private final PostponedService postponedService;
    private final CartService cartService;
    private final AdminService adminService;
    private final ResourceStorage storage;
    private final BookService bookService;
    private final BookReviewService bookReviewService;
    private final AuthorService authorService;

    @PostMapping("admin/addbook/{genre}")
    public String createBook(@PathVariable(value = "genre", required = false) String genre, BookDto bookDto, Model model,
                             @AuthenticationPrincipal OAuth2User principal,
                             @CookieValue(name = "postponedContents", required = false) String postponedContents,
                             @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        model.addAttribute("bookForm", new BookDto());
        Book book = adminService.createBook(bookDto, genre);
        return ("redirect:/books/" + book.getSlug());
    }

    @GetMapping("books/admin/removebook/{slug}")
    public String removeBook(@PathVariable(value = "slug", required = false) String slug, Model model,
                             @AuthenticationPrincipal OAuth2User principal,
                             @CookieValue(name = "postponedContents", required = false) String postponedContents,
                             @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        adminService.removeBook(slug);
        return "redirect:/";
    }

    @PostMapping("admin/changebook/{slug}")
    public String changeBook(@PathVariable(value = "slug", required = false) String slug, Book bookDto, Model model,
                             @AuthenticationPrincipal OAuth2User principal,
                             @CookieValue(name = "postponedContents", required = false) String postponedContents,
                             @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        model.addAttribute("changeBookForm", bookReviewService.getBook(slug));
        Book book = adminService.changeBook(bookDto);
        return ("redirect:/books/" + book.getSlug());
    }

    @PostMapping("books/{slug}/img/save")
    public String saveNewBookImage(@RequestParam("file") MultipartFile file,
                                   @PathVariable("slug") String slug) throws IOException {
        String savePath = storage.saveNewBookImage(file, slug);
        Book bookToUpdate = bookReviewService.getBook(slug);
        bookToUpdate.setImage(savePath);
        bookService.saveBook(bookToUpdate);
        return ("redirect:/books/" + slug);
    }

    @PostMapping("admin/changeauthor/{slug}")
    public String changeAuthor(@PathVariable(value = "slug", required = false) String slug, Author authorDto, Model model,
                               @AuthenticationPrincipal OAuth2User principal,
                               @CookieValue(name = "postponedContents", required = false) String postponedContents,
                               @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        model.addAttribute("changeAuthorForm", authorDto);
        Author author = adminService.changeAuthor(authorDto);
        return ("redirect:/authors/" + author.getSlug());
    }

    @PostMapping("authors/{slug}/img/save")
    public String saveNewAuthorImage(@RequestParam("file") MultipartFile file,
                                   @PathVariable("slug") String slug) throws IOException {
        String savePath = storage.saveNewAuthorImage(file, slug);
        Author authorToUpdate = authorService.getAuthorBySlug(slug);
        authorToUpdate.setPhoto(savePath);
        authorService.saveAuthor(authorToUpdate);
        return ("redirect:/authors/" + slug);
    }

    @GetMapping("books/admin/removebookreview/{slug}/{review}")
    public String removeReview(@PathVariable(value = "slug", required = false) String slug,
                             @PathVariable(value = "review", required = false) String reviewId, Model model,
                             @AuthenticationPrincipal OAuth2User principal,
                             @CookieValue(name = "postponedContents", required = false) String postponedContents,
                             @CookieValue(name = "cartContents", required = false) String cartContents ) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        adminService.removeReview(Integer.parseInt(reviewId));
        return ("redirect:/books/" + slug);
    }

    @GetMapping("books/admin/blockuser/{slug}/{user}")
    public String blockUser(@PathVariable(value = "slug", required = false) String slug,
                             @PathVariable(value = "user", required = false) String userId,
                             Model model, @AuthenticationPrincipal OAuth2User principal,
                             @CookieValue(name = "postponedContents", required = false) String postponedContents,
                            @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        adminService.blockUser(Integer.parseInt(userId));
        return ("redirect:/books/" + slug);
    }

    @GetMapping("books/admin/addpromobookallusers/{slug}")
    public String addPromoBookAllUsers(@PathVariable(value = "slug", required = false) String slug,
                            Model model, @AuthenticationPrincipal OAuth2User principal,
                            @CookieValue(name = "postponedContents", required = false) String postponedContents,
                            @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        adminService.addPromoBookAllUsers(slug);
        return ("redirect:/books/" + slug);
    }

    @PostMapping("admin/addpromobookbybookcount/{slug}")
    public String addPromoBookByBookCount(@PathVariable(value = "slug", required = false) String slug, WordDto wordDto,
                                       Model model, @AuthenticationPrincipal OAuth2User principal,
                                       @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                       @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        adminService.addPromoBookByBookCount(slug, Integer.parseInt(wordDto.getWordName()));
        return ("redirect:/books/" + slug);
    }

    @PostMapping("admin/addpromobookbycheckamount/{slug}")
    public String addPromoBookByCheckAmount(@PathVariable(value = "slug", required = false) String slug, WordDto wordDto,
                                         Model model, @AuthenticationPrincipal OAuth2User principal,
                                         @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                         @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        adminService.addPromoBookByCheckAmount(slug, Integer.parseInt(wordDto.getWordName()));
        return ("redirect:/books/" + slug);
    }
}
