package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.Message;
import com.example.MyBookShopApp.services.CartService;
import com.example.MyBookShopApp.services.MessageService;
import com.example.MyBookShopApp.services.PostponedService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
@AllArgsConstructor
@Controller
public class ContactsController {
    private final PostponedService postponedService;
    private final CartService cartService;
    private final MessageService messageService;

    @GetMapping("/contacts")
    public String mainPage(Model model, @AuthenticationPrincipal OAuth2User principal,
                           @CookieValue(name = "postponedContents", required = false) String postponedContents,
                           @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        model.addAttribute("messageForm", new Message());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            model.addAttribute("anonymous", true);
        } else {
            model.addAttribute("anonymous", false);
        }
        return "contacts";
    }

    @PostMapping("/send")
    public String sendMassage(Message message, Model model, @AuthenticationPrincipal OAuth2User principal,
                              @CookieValue(name = "postponedContents", required = false) String postponedContents,
                              @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        model.addAttribute("messageForm", new Message());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            model.addAttribute("anonymous", true);
            messageService.getAnonymousMessage(message);
        } else {
            model.addAttribute("anonymous", false);
            messageService.getAuthUserMessage(message, principal);
        }
        return "contacts";
    }
}
