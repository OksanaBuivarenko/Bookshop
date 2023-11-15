package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.*;
import com.example.MyBookShopApp.data.book.Book;
import com.example.MyBookShopApp.dto.BooksPageDto;
import com.example.MyBookShopApp.dto.SearchWordDto;
import com.example.MyBookShopApp.dto.WordDto;
import com.example.MyBookShopApp.errs.EmptySearchException;
import com.example.MyBookShopApp.services.*;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@Controller
public class MainPageController {
    private final BookService bookService;
    private final TagService tagService;
    private final PopularService popularService;
    private final RecentService recentService;
    private final PostponedService postponedService;
    private final CartService cartService;
    private final BookstoreUserRegister userRegister;

    @ModelAttribute("recentBooks")
    public List<Book> recentBooks() {
        return recentService.getPageOfRecentBooksWithoutDate(0, 6).getContent();
    }

    @ModelAttribute("popularBooks")
    public List<Book> popularBooks() {
        return popularService.getPageOfPopularBooks(0, 6).getContent();
    }


    @ModelAttribute("tagsList")
    public List<Tag> tagsList() {
        return tagService.getTagData();
    }

    @ModelAttribute("TagDto")
    public WordDto tagDto() {
        return new WordDto();
    }

    @ModelAttribute("booksForTag")
    public List<Book> booksForTag() {
        return new ArrayList<>();
    }

    @ModelAttribute("tagClass")
    public List<Tag> tagClass() {
        return tagService.classForTagCount();
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @ModelAttribute("searchResults")
    public List<Book> searchResults() {
        return new ArrayList<>();
    }

    @GetMapping("/")
    public String mainPage(Model model, @AuthenticationPrincipal OAuth2User principal,
                           @CookieValue(name = "postponedContents", required = false) String postponedContents,
                           @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        List<Book> recommendedBooks;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            recommendedBooks = bookService.getPageOfRecommendedBooksForNotAuthUser(0, 6).getContent();
        } else {
            bookService.getRecommendedBooksList(userRegister.getCurrentUser(principal).getId());
            recommendedBooks = bookService.getPageOfRecommendedBooks(0, 6, principal).getContent();
        }
        model.addAttribute("recommendedBooks", recommendedBooks);
        return "index";
    }

    @GetMapping("/books/recommended")
    @ResponseBody
    public BooksPageDto getBooksPage(@RequestParam("offset") Integer offset,
                                     @RequestParam("limit") Integer limit,
                                     @AuthenticationPrincipal OAuth2User principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return new BooksPageDto(bookService.getPageOfRecommendedBooksForNotAuthUser(offset, limit).getContent());
        } else {
            return new BooksPageDto(bookService.getPageOfRecommendedBooks(offset, limit, principal).getContent());
        }
    }

    @GetMapping("/books/recent/page")
    @ResponseBody
    public BooksPageDto getBooksPageRecent(@RequestParam("offset") Integer offset,
                                           @RequestParam("limit") Integer limit) {
        return new BooksPageDto(recentService.getPageOfRecentBooksWithoutDate(offset, limit).getContent());
    }

    @GetMapping("/books/popular/page")
    @ResponseBody
    public BooksPageDto getBooksPagePopular(@RequestParam("offset") Integer offset,
                                            @RequestParam("limit") Integer limit) {
        return new BooksPageDto(popularService.getPageOfPopularBooks(offset, limit).getContent());
    }

    @GetMapping(value = {"/search", "/search/{searchWord}"})
    public String getSearchResults(@PathVariable(value = "searchWord", required = false) SearchWordDto searchWordDto,
                                   @AuthenticationPrincipal OAuth2User principal, Model model,
                                   @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                   @CookieValue(name = "cartContents", required = false) String cartContents)
                                   throws EmptySearchException {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        if (searchWordDto != null) {
            model.addAttribute("searchWordDto", searchWordDto);
            model.addAttribute("searchResults",
                    bookService.getPageOfSearchResultBooks(searchWordDto.getExample(), 0, 5).getContent());
            return "/search/index";
        }
        throw new EmptySearchException("Поиск по null невозможен");
    }

    @GetMapping("/search/page/{searchWord}")
    @ResponseBody
    public BooksPageDto getNextSearchPage(@RequestParam("offset") Integer offset,
                                          @RequestParam("limit") Integer limit,
                                          @PathVariable(value = "searchWord", required = false) SearchWordDto searchWordDto,
                                          @AuthenticationPrincipal OAuth2User principal, Model model,
                                          @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                          @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        return new BooksPageDto(bookService.getPageOfSearchResultBooks(searchWordDto.getExample(), offset, limit).getContent());
    }


    @GetMapping("/tags/{tagWord}")
    public String getTagPage(@PathVariable("tagWord") WordDto tagDto, Model model, @AuthenticationPrincipal OAuth2User principal,
                             @CookieValue(name = "postponedContents", required = false) String postponedContents,
                             @CookieValue(name = "cartContents", required = false) String cartContents) {
        postponedService.postponedCount(postponedContents, model, principal);
        cartService.cartCount(cartContents, model, principal);
        model.addAttribute("tagDto", tagDto);
        tagService.addTagUsages(tagDto.getWordName());
        model.addAttribute("booksForTag", tagService.getPageBooksOfTag(tagDto.getWordName(), 0, 5).getContent());
        return "/tags/index";
    }

    @GetMapping("/books/tag/{tagWord}")
    @ResponseBody
    public BooksPageDto getTagPage(@RequestParam("offset") Integer offset,
                                   @RequestParam("limit") Integer limit,
                                   @PathVariable(value = "tagWord", required = false) WordDto tagDto) {
        return new BooksPageDto(tagService.getPageBooksOfTag(tagDto.getWordName(), offset, limit).getContent());
    }
}
