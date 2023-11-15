package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.book.Book;
import com.example.MyBookShopApp.dto.BooksPageDto;
import com.example.MyBookShopApp.dto.DateDto;
import com.example.MyBookShopApp.services.CartService;
import com.example.MyBookShopApp.services.PostponedService;
import com.example.MyBookShopApp.services.RecentService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
@AllArgsConstructor
@Controller
public class RecentController {
    private final RecentService recentService;
    private final PostponedService postponedService;
    private final CartService cartService;
    private DateDto dateDto = new DateDto();

    @Autowired
    public RecentController(RecentService recentService, PostponedService postponedService, CartService cartService) {
        this.recentService = recentService;
        this.postponedService = postponedService;
        this.cartService = cartService;
    }

    @ModelAttribute("recentBooks")
    public List<Book> recentBooks() {
        return recentService.getPageOfRecentBooks(dateDto.getDateFrom(), dateDto.getDateTo(), 0, 20).getContent();
    }

    @GetMapping("/books/recent")
    public String recentPage(Model model, @AuthenticationPrincipal OAuth2User principal,
                             @CookieValue(name = "postponedContents", required = false) String postponedContents,
                             @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        return "books/recent.html";
    }

    @GetMapping("/books/recent/more")
    @ResponseBody
    public BooksPageDto getBooksRecentMore(@RequestParam("from") String from,
                                           @RequestParam("to") String to,
                                           @RequestParam("offset") Integer offset,
                                           @RequestParam("limit") Integer limit) {
        Date dateTo = null;
        if (from == "" || from.equals("0")) {
            from = "01.01.1700";
        }
        Date dateFrom = recentService.convertDate(from);
        dateTo = (to == "" || to.equals("0")) ? new Date() : recentService.convertDate(to);
        dateDto.setDateFrom(dateFrom);
        dateDto.setDateTo(dateTo);
        return new BooksPageDto(recentService.getPageOfRecentBooks(dateFrom, dateTo, offset, limit).getContent());
    }
}


