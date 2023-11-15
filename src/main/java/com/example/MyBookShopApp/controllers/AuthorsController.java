package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.*;
import com.example.MyBookShopApp.data.book.Book;
import com.example.MyBookShopApp.dto.BooksPageDto;
import com.example.MyBookShopApp.dto.SearchWordDto;
import com.example.MyBookShopApp.dto.WordDto;
import com.example.MyBookShopApp.services.AuthorService;
import com.example.MyBookShopApp.services.BookstoreUserRegister;
import com.example.MyBookShopApp.services.CartService;
import com.example.MyBookShopApp.services.PostponedService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@AllArgsConstructor
@Controller
@Api("authors data")
public class AuthorsController {

    private final AuthorService authorService;
    private final PostponedService postponedService;
    private final CartService cartService;
    private final BookstoreUserRegister userRegister;

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @ModelAttribute("AuthorDto")
    public Author author() {
        return new Author();
    }

    @ModelAttribute("authorsMap")
    public Map<String, List<Author>> authorsMap() {
        return authorService.getAuthorsMap();
    }

    @ModelAttribute("booksForAuthor")
    public List<Book> booksForAuthor() {
        return new ArrayList<>();
    }

    @ModelAttribute("authorBiography")
    public List<String> authorBiography() {
        return new ArrayList<>();
    }

    @GetMapping("/authors")
    public String authorsPage(Model model, @AuthenticationPrincipal OAuth2User principal,
                              @CookieValue(name = "postponedContents", required = false) String postponedContents,
                              @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        return "/authors/index";
    }

    @GetMapping("/authors/{slug}")
    public String getAuthorPage(@PathVariable("slug") String slug, Model model, @AuthenticationPrincipal OAuth2User principal,
                                @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        model.addAttribute("authorBiography", authorService.getParagraphs(slug));
        model.addAttribute("authorDto", authorService.getAuthorBySlug(slug));
        model.addAttribute("booksForAuthor", authorService.getPageBooksOfAuthor(slug, 0, 5).getContent());
        if (userRegister.isAdmin(principal)) {
            model.addAttribute("isAdmin", true);
        } else {
            model.addAttribute("isAdmin", false);
        }
        model.addAttribute("changeAuthorForm", authorService.getAuthorBySlug(slug));
        return "/authors/slug";
    }

    @GetMapping("/books/author/{slug}")
    public String getBooksByAuthorPage(@PathVariable("slug") WordDto authorDto, Model model,
                                       @AuthenticationPrincipal OAuth2User principal,
                                       @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                       @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        model.addAttribute("authorDto", authorService.getAuthorBySlug(authorDto.getWordName()));
        model.addAttribute("booksForAuthor", authorService.getPageBooksOfAuthor(authorDto.getWordName(),
                0, 10).getContent());
        return "/books/author";
    }

    @GetMapping("/books/author/more/{slug}")
    @ResponseBody
    public BooksPageDto geAuthorBookPage(@RequestParam("offset") Integer offset,
                                         @RequestParam("limit") Integer limit,
                                         @PathVariable(value = "slug", required = false) WordDto authorDto) {
        return new BooksPageDto(authorService.getPageBooksOfAuthor(authorDto.getWordName(), offset, limit).getContent());
    }

    @ApiOperation("method to get map of authors")
    @GetMapping("/api/authors")
    @ResponseBody
    public Map<String, List<Author>> authors() {
        return authorService.getAuthorsMap();
    }
}
