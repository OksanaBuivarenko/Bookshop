package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.book.Book;
import com.example.MyBookShopApp.services.CartService;
import com.example.MyBookShopApp.services.PostponedService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@Controller
@RequestMapping("/books")
public class CartController {
    private final PostponedService postponedService;
    private final CartService cartService;

    @ModelAttribute(name = "bookCart")
    public List<Book> bookCart() {
        return new ArrayList<>();
    }

    @GetMapping("/cart")
    public String handleCartRequest(Model model, @AuthenticationPrincipal OAuth2User principal,
                                    @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                    @CookieValue(name = "cartContents", required = false) String cartContents ) {
        postponedService.postponedCount(postponedContents, model,principal);
        cartService.cartCount(cartContents, model, principal);
        cartService.cartPage(cartContents,model,principal);
        return "cart";
    }

    @PostMapping("/changeBookStatus/cart/remove/{slug}")
    public String handleRemoveBookFromCartRequest(@PathVariable("slug") String slug, @CookieValue(name =
            "cartContents", required = false) String cartContents, HttpServletResponse response, Model model,
                                                  @AuthenticationPrincipal OAuth2User principal) {
        cartService.removeBookFromCart(slug,cartContents, response, model, principal);
        return "redirect:/books/cart";
    }

    @PostMapping("/changeBookStatus/{slug}")
    public String handleChangeBookStatus(@PathVariable("slug") String slug, @CookieValue(name = "cartContents",
            required = false) String cartContents, HttpServletResponse response, Model model,
                                         @AuthenticationPrincipal OAuth2User principal) {
        cartService.changeBookStatus(slug,cartContents, response, model, principal);
        return "redirect:/books/" + slug;
    }

    @GetMapping("/payBooks")
    public String handlePayBooks(@AuthenticationPrincipal OAuth2User principal) throws NoSuchAlgorithmException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return "signin";
        }
        if(cartService.payBooks(principal)){
            return "redirect:/";
        }
        return "redirect:/profile";
    }
}
