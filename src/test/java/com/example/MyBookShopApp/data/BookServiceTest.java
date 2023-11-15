package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.data.book.Book;
import com.example.MyBookShopApp.services.BookService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class BookServiceTest {
    private final BookService bookService;
    private int userId = 1;
//    private Book book;
//    private Author author;
//    private Genre genre;
//    private Tag tag;

    @Autowired
    BookServiceTest(BookService bookService) {
        this.bookService = bookService;
    }
    @BeforeEach
    void setUp() throws ParseException {
//        book = new Book();
//        book.setId(1);
//        book.setDescription("Pellentesque viverra pede ac diam. Cras pellentesque volutpat dui. Maecenas tristique, est et tempus semper, est quam pharetra magna, ac consequat metus sapien ut nunc. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Mauris viverra diam vitae quam. Suspendisse potenti.");
//        book.setImage("http://dummyimage.com/403x305.png/cc0000/ffffff");
//        book.setIsBestseller(1);
//        book.setPrice(0.19);
//        book.setPriceOld(2100);
//        book.setRatingPopularity(21);
//        book.setSlug("book-vxg-586");
//        book.setTitle("Sudden Manhattan");
//
////        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
////        Date dateToUse = df.parse("2019-09-03");
////        book.setPubDate((java.sql.Date) dateToUse);
//
////        author = new Author();
////        author.setId(1);
////        author.setDescription("Failed to resolve parameter ");
////        author.setFirstName("Philly");
////        author.setLastName("Epine");
////        author.setPhoto("http://dummyimage.com/144x100.png/5fa2dd/ffffff");
////        author.setSlug("1");
////        book.setAuthor(author);
//
//        genre = new Genre();
//        genre.setId(14);
//        genre.setSlug("Биология");
//        genre.setGenreName("Биология");
//        List<Genre> genres = new ArrayList<>();
//        genres.add(genre);
//        book.setGenres(genres);
//
//        tag = new Tag();
//        tag.setId(10);
//        tag.setTagName("приключенская литература");
//        List<Tag> tags = new ArrayList<>();
//        tags.add(tag);
//        book.setTags(tags);
    }

    @AfterEach
    void tearDown() {
//
//        book = null;
//        genre = null;
//        tag = null;
    }

    @Test
    @Transactional
    void recommendedBooksList() {
        List <Book> actualList = bookService.getRecommendedBooksList(userId);
       // assertTrue(actualList.contains(book));
        assertThat(actualList, hasSize(30));
        assertThat(actualList.toString(), Matchers.containsString("Silence"));
        assertThat(actualList, is(not(emptyIterable())));

//        assertThat(
//                actualList, containsInAnyOrder(hasProperty("title"), is("Sudden Manhattan"))
//        );
    }


}