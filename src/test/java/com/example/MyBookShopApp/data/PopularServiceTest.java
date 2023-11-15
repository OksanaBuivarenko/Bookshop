package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.services.PopularService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class PopularServiceTest {
    private final PopularService popularService;
    private int paid = 10;
    private int cart = 20;
    private int kept = 30;
    private int viewed = 50;
    @Autowired
    PopularServiceTest(PopularService popularService) {
        this.popularService = popularService;
    }


    @Test
    void calculationPopularity() {
        int popularity = popularService.calculationPopularity(paid, cart, kept, viewed);

        assertNotNull(popularity);
        assertEquals(popularity, 46);
    }
}