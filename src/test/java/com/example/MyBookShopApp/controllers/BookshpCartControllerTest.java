package com.example.MyBookShopApp.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;


import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookshpCartControllerTest {
    private final MockMvc mockMvc;
@Autowired
    BookshpCartControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }



    @Test
    void handleRemoveBookFromCartRequest() throws Exception {
    mockMvc.perform(post("/books/changeBookStatus/cart/remove/book-ymt-908"))
            .andDo(print())
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/books/cart"));

    }

    @Test
    void handleChangeBookStatus() throws Exception {
        mockMvc.perform(post("/books/changeBookStatus/book-ymt-908"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/book-ymt-908"));
    }
}