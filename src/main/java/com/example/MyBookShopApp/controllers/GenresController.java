package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.*;
import com.example.MyBookShopApp.data.book.Book;
import com.example.MyBookShopApp.dto.BookDto;
import com.example.MyBookShopApp.dto.BooksPageDto;
import com.example.MyBookShopApp.dto.WordDto;
import com.example.MyBookShopApp.services.BookstoreUserRegister;
import com.example.MyBookShopApp.services.CartService;
import com.example.MyBookShopApp.services.GenreService;
import com.example.MyBookShopApp.services.PostponedService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
@AllArgsConstructor
@Controller
public class GenresController {
    private final GenreService genreService;
    private final PostponedService postponedService;
    private final CartService cartService;
    private final BookstoreUserRegister userRegister;

    @ModelAttribute("genresAllList")
    public List<Genre> genresAllList() {
        return genreService.getGenre();
    }

    @ModelAttribute("genresList")
    public List<Genre> genresList() {
        return genreService.getGenreWithChildList();
    }

    @ModelAttribute("GenreDto")
    public WordDto genreDto() {
        return new WordDto();
    }

    @ModelAttribute("booksForGenre")
    public List<Book> booksForGenre() {
        return new ArrayList<>();
    }

    @GetMapping("/genres")
    public String genresPage(Model model, @AuthenticationPrincipal OAuth2User principal,
                             @CookieValue(name = "postponedContents", required = false) String postponedContents,
                             @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        Comparator<Genre> genreComparator = java.util.Comparator.comparing(genre -> genre.booksCount());
        genreComparator = genreComparator.reversed();
        model.addAttribute("comporator", genreComparator);
        return "genres/index";
    }

    @GetMapping("/genres/{slug}")
    public String getGenresPage(@PathVariable("slug") WordDto genreDto, Model model,
                                @AuthenticationPrincipal OAuth2User principal,
                                @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        model.addAttribute("genreDto", genreDto);
        model.addAttribute("bookForm", new BookDto());
        model.addAttribute("booksForGenre", genreService.getPageBooksOfGenre(genreDto.getWordName(),
                0, 20).getContent());
        if (userRegister.isAdmin(principal)) {
            model.addAttribute("isAdmin", true);
        } else {
            model.addAttribute("isAdmin", false);
        }
        return "/genres/slug";
    }

    @GetMapping("/books/genre/{slug}")
    @ResponseBody
    public BooksPageDto getGenrePage(@RequestParam("offset") Integer offset,
                                     @RequestParam("limit") Integer limit,
                                     @PathVariable(value = "slug", required = false) WordDto genreDto) {
        return new BooksPageDto(genreService.getPageBooksOfGenre(genreDto.getWordName(), offset, limit).getContent());
    }
}
