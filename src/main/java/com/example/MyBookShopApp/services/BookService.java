package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.*;
import com.example.MyBookShopApp.data.book.Book;
import com.example.MyBookShopApp.data.book.Book2User;
import com.example.MyBookShopApp.data.book.BookRating;
import com.example.MyBookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.reposotories.BookstoreUserRepository;
import com.example.MyBookShopApp.reposotories.book.*;
import com.example.MyBookShopApp.data.BookstoreUser;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
@AllArgsConstructor
@Service
public class BookService {
    private final BookRepository bookRepository;
    private final BookRatingRepository bookRatingRepository;
    private final BookstoreUserRepository bookstoreUserRepository;
    private final Book2UserRepository book2UserRepository;
    private final PopularService popularService;
    private final BookstoreUserRegister userRegister;
    private final BookRatingGradeRepository bookRatingGradeRepository;
    private List<Book> recommendedBooksList = new ArrayList<>();

    public List<Book> getBooksByTitle(String title) throws BookstoreApiWrongParameterException {
        if (title.equals("") || title.length() <= 1) {
            throw new BookstoreApiWrongParameterException("Wrong values passed to one or more parameters");
        }
        List<Book> data = bookRepository.findBooksByTitleContaining(title);
        if (data.size() > 0) {
            return data;
        }
        throw new BookstoreApiWrongParameterException("No data found with specified parameters...");
    }

    public List<Book> getBooksWithPriceBetween(Integer min, Integer max) {
        return bookRepository.findBooksByPriceOldBetween(min, max);
    }

    public List<Book> getBooksWithMaxPrice() {
        return bookRepository.getBooksWithMaxDiscount();
    }

    public List<Book> getBestsellers() {
        return bookRepository.getBestsellers();
    }

    public Page<Book> getPageOfRecommendedBooksForNotAuthUser(Integer offset, Integer limit) {
        return popularService.getPageOfPopularBooks(offset, limit);
    }

    public Page<Book> getPageOfRecommendedBooks(Integer offset, Integer limit, OAuth2User principal) {
        BookstoreUser currentUser = userRegister.getCurrentUser(principal);
        BookstoreUser user = bookstoreUserRepository.findBookstoreUserById(currentUser.getId());
        if (user.getBook2Users().isEmpty()) {
            return getPageOfRecommendedBooksForNotAuthUser(offset, limit);
        }
        PagedListHolder page = new PagedListHolder(recommendedBooksList);
        page.setPageSize(limit);
        page.setPage(offset);
        Page<Book> bookPage = new PageImpl<>(page.getPageList());
        return bookPage;
    }

    public List<Book> getRecommendedBooksList(int userId) {
        recommendedBooksList.removeAll(recommendedBooksList);
        List<Book> booksList = bookRepository.findBooksByUsersId(userId);
        for (Book book : booksList) {
            addRecommendedBookByAuthor(book);
            addRecommendedBookByTag(book);
            addRecommendedBookByGenre(book);
        }
        recommendedBooksList.sort(Comparator.comparing(Book::getPubDate).reversed());
        return recommendedBooksList;
    }

    private List<Book> addRecommendedBookByGenre(Book book) {
        for (Genre genre : book.getGenres()) {
            List<Book> booksByGenre = bookRepository.findBooksByGenresGenreName(genre.getGenreName());
            for (Book bookByGenre : booksByGenre) {
                if (!recommendedBooksList.contains(bookByGenre)) {
                    recommendedBooksList.add(bookByGenre);
                }
            }
        }
        return recommendedBooksList;
    }

    private List<Book> addRecommendedBookByTag(Book book) {
        for (Tag tag : book.getTags()) {
            List<Book> booksByTag = bookRepository.findBooksByTagsTagName(tag.getTagName());
            for (Book bookByTag : booksByTag) {
                if (!recommendedBooksList.contains(bookByTag)) {
                    recommendedBooksList.add(bookByTag);
                }
            }
        }
        return recommendedBooksList;
    }

    private List<Book> addRecommendedBookByAuthor(Book book) {
        for (Author author : book.getAuthors()) {
            List<Book> booksByAuthor = bookRepository.findBooksByAuthorsSlug(author.getSlug());
            for (Book bookByAuthor : booksByAuthor) {
                if (!recommendedBooksList.contains(bookByAuthor)) {
                    recommendedBooksList.add(bookByAuthor);
                }
            }
        }
        return recommendedBooksList;
    }

    public Page<Book> getPageOfSearchResultBooks(String searchWord, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findBooksByTitleContainingIgnoreCase(searchWord, nextPage);
    }

    public List<String> starsColors(int rating) {
        List<String> starsColors = new ArrayList<>();
        String starsClass = "Rating-star";
        for (int i = 0; i < 5; i++) {
            starsColors.add(starsClass);
        }
        for (int i = 0; i < rating; i++) {
            starsColors.set(i, "Rating-star Rating-star_view");
        }
        return starsColors;
    }

    public void changeRating(Book book, Integer grade) {
        changeGrade(book.getBookRating(), grade);
        if (book.getBookRating().getRating() == 0) {
            book.getBookRating().setRating(book.getBookRating().getRating() + grade);
        } else {
            book.getBookRating().setRating((book.getBookRating().getRating() + grade) / 2);
        }
        bookRatingRepository.save(book.getBookRating());
        bookRepository.save(book);
    }

    public void changeGrade(BookRating bookRating, Integer grade) {
        for (int i = 0; i <= bookRating.getBookRatingGradeList().size(); i++) {
            if (i + 1 == grade) {
                bookRating.getBookRatingGradeList().get(i).
                        setCount(bookRating.getBookRatingGradeList().get(i).getCount() + 1);
                bookRatingGradeRepository.save(bookRating.getBookRatingGradeList().get(i));
            }
        }
    }

    public void saveBook(Book book) {
        bookRepository.save(book);
    }

    public void addViewedBook(String slug, BookstoreUser currentUser) {
        Book book = bookRepository.findBookBySlug(slug);
        Book2User book2User = book2UserRepository.findBook2UserByBookIdAndUserId(book.getId(), currentUser.getId());
        if (book2User == null) {
            book2User = new Book2User();
            book2User.setBook(book);
            book2User.setUser(currentUser);
            book2User.setTime(LocalDateTime.now());
            book2User.setTypeId(5);
            book2UserRepository.save(book2User);
        }
    }
}
