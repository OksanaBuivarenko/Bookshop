package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.book.Book;
import com.example.MyBookShopApp.dto.BooksPageDto;
import com.example.MyBookShopApp.services.CartService;
import com.example.MyBookShopApp.services.PostponedService;
import com.example.MyBookShopApp.services.RecentlyViewedService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@AllArgsConstructor
@Controller
public class RecentlyViewedController {
    private final RecentlyViewedService recentlyViewedService;
    private final PostponedService postponedService;
    private final CartService cartService;

    @ModelAttribute("recentlyBooks")
    public List<Book> popularBooks(@AuthenticationPrincipal OAuth2User principal) {
        return recentlyViewedService.getPageOfRecentlyViewedBooks(0, 20, principal).getContent();
    }

    @GetMapping("/books/viewed")
    public String viewedPage(Model model, @AuthenticationPrincipal OAuth2User principal,
                             @CookieValue(name = "postponedContents", required = false) String postponedContents,
                             @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        return "books/recentlyviewed";
    }

    @GetMapping("/books/viewed/more")
    @ResponseBody
    public BooksPageDto getBooksRecentMore(@RequestParam("offset") Integer offset,
                                           @RequestParam("limit") Integer limit,
                                           @AuthenticationPrincipal OAuth2User principal) {
        return new BooksPageDto(recentlyViewedService.getPageOfRecentlyViewedBooks(offset, limit, principal).getContent());
    }
}
