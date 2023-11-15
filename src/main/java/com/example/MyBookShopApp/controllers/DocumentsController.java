package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.services.CartService;
import com.example.MyBookShopApp.services.PostponedService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
@AllArgsConstructor
@Controller
public class DocumentsController {
    private final PostponedService postponedService;
    private final CartService cartService;

    @GetMapping("/documents")
    public String mainPage(Model model, @AuthenticationPrincipal OAuth2User principal,
                           @CookieValue(name = "postponedContents", required = false) String postponedContents,
                           @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model,principal);
        cartService.cartCount(cartContents, model, principal);
        return "documents/index";
    }
}
