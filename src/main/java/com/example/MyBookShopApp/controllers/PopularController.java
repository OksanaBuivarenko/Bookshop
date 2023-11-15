package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.book.Book;
import com.example.MyBookShopApp.dto.BooksPageDto;
import com.example.MyBookShopApp.services.CartService;
import com.example.MyBookShopApp.services.PopularService;
import com.example.MyBookShopApp.services.PostponedService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@AllArgsConstructor
@Controller
public class PopularController {
    private final PopularService popularService;
    private final PostponedService postponedService;
    private final CartService cartService;

    @ModelAttribute("popularBooks")
    public List<Book> popularBooks() {
        return popularService.getPageOfPopularBooks(0, 20).getContent();
    }

    @GetMapping("/books/popular")
    public String recentPage(Model model, @AuthenticationPrincipal OAuth2User principal,
                             @CookieValue(name = "postponedContents", required = false) String postponedContents,
                             @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        return "books/popular.html";
    }

    @GetMapping("/books/popular/more")
    @ResponseBody
    public BooksPageDto getBooksRecentMore(@RequestParam("offset") Integer offset,
                                           @RequestParam("limit") Integer limit) {
        return new BooksPageDto(popularService.getPageOfPopularBooks(offset, limit).getContent());
    }
}
