package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.services.BookReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@SpringBootTest
class BookReviewServiceTest {
    private final BookReviewService bookReviewService;

    @Autowired
    BookReviewServiceTest(BookReviewService bookReviewService) {
        this.bookReviewService = bookReviewService;
    }
    private int like = 5;
    private int dislike = 5;

    @Test
    void calculationReviewLikeRating() {

        Double rating = bookReviewService.calculationReviewLikeRating(like, dislike);

        assertNotNull(rating);
        assertEquals(rating, 2.5);
    }
}