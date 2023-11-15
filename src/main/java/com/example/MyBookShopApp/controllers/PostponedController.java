package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.book.Book;
import com.example.MyBookShopApp.services.CartService;
import com.example.MyBookShopApp.services.PostponedService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@Controller
public class PostponedController {
    private final CartService cartService;
    private final PostponedService postponedService;

    @ModelAttribute(name = "bookPostponed")
    public List<Book> bookPostponed() {
        return new ArrayList<>();
    }

    @GetMapping("/postponed")
    public String postponedPage(Model model, @AuthenticationPrincipal OAuth2User principal,
                                @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        postponedService.postponedPage(postponedContents, model, principal);
        return "postponed";
    }

    @PostMapping("/books/changeBookStatus/postponed/remove/{slug}")
    public String handleRemoveBookFromPostponedRequest(@PathVariable("slug") String slug,
                                                       @CookieValue(name = "postponedContents", required = false)
                                                               String postponedContents,
                                                       HttpServletResponse response, Model model,
                                                       @AuthenticationPrincipal OAuth2User principal) {
        postponedService.removeBookFromPostponedRequest(slug, postponedContents, response, model, principal);
        return "redirect:/postponed";
    }

    @PostMapping("/books/changeBookStatus/postponed/cart/{slug}")
    public String handleCartBookFromPostponedRequest(@PathVariable("slug") String slug,
                                                     @CookieValue(name = "postponedContents", required = false) String postponedContents, @CookieValue(name = "cartContents",
            required = false) String cartContents, HttpServletResponse response, Model model,
                                                     @AuthenticationPrincipal OAuth2User principal) {
        cartService.changeBookStatus(slug, cartContents, response, model, principal);
        postponedService.removeBookFromPostponedRequest(slug, postponedContents, response, model, principal);
        return "redirect:/postponed";
    }

    @PostMapping("/books/changeBookStatus/postponed/cartAll")
    public String handleCartAllBookFromPostponedRequest(@CookieValue(name = "postponedContents", required = false) String postponedContents,
                                                        @CookieValue(name = "cartContents", required = false) String cartContents,
                                                        HttpServletResponse response, Model model, @AuthenticationPrincipal OAuth2User principal) {
        cartService.changeAllBookStatus(cartContents, postponedContents, response, model, principal);
        postponedService.removeAllBookFromPostponedRequest(postponedContents, response, model, principal);
        return "redirect:/postponed";
    }

    @PostMapping("/books/changeBookStatus/postponed/{slug}")
    public String handleChangeBookStatusPostponed(@PathVariable("slug") String slug,
                                                  @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                                  HttpServletResponse response, Model model, @AuthenticationPrincipal OAuth2User principal) {
        postponedService.changeBookStatusPostponed(slug, postponedContents, response, model, principal);
        return "redirect:/books/" + slug;
    }
}
