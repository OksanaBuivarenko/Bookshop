package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.book.Book;
import com.example.MyBookShopApp.services.CartService;
import com.example.MyBookShopApp.services.MyService;
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
public class MyController {
    private final MyService myService;
    private final PostponedService postponedService;
    private final CartService cartService;

    @GetMapping("/my")
    public String handleMy(Model model, @AuthenticationPrincipal OAuth2User principal,
                           @CookieValue(name = "postponedContents", required = false) String postponedContents,
                           @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        List<Book> myArchiveBookList = myService.getMyArchiveBookList(principal, model);
        List<Book> myBookList = myService.getMyBookList(principal, model);
        myService.getMyBookCount(myBookList.size(), myArchiveBookList.size(), model);
        return "my";
    }

    @GetMapping("/myarchive")
    public String handleMyArchive(Model model, @AuthenticationPrincipal OAuth2User principal,
                                  @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                  @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        List<Book> myArchiveBookList = myService.getMyArchiveBookList(principal, model);
        List<Book> myBookList = myService.getMyBookList(principal, model);
        myService.getMyBookCount(myBookList.size(), myArchiveBookList.size(), model);
        return "myarchive";
    }

    @PostMapping("/changeBookStatus/arch/{slug}")
    public String handleChangeBookStatusArchived(@PathVariable("slug") String slug,
                                                 @AuthenticationPrincipal OAuth2User principal) {
        myService.changeBookStatusByStatusId(slug, principal);
        return "redirect:/books/" + slug;
    }
}
