package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.BookstoreUser;
import com.example.MyBookShopApp.data.EnumContactType;
import com.example.MyBookShopApp.data.UserContact;
import com.example.MyBookShopApp.services.BookstoreUserDetailsService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@SpringBootTest
class BookstoreUserDetailsServiceTest {

    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private BookstoreUserDetails userDetails;
    private BookstoreUser user;
    private HttpServletRequest httpServletRequest;


    @Autowired
    BookstoreUserDetailsServiceTest(BookstoreUserDetailsService bookstoreUserDetailsService) {
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
    }



    @BeforeEach
    void setUp() {
        httpServletRequest = mock(HttpServletRequest.class);
        user = new BookstoreUser();
        UserContact contactPhone = new UserContact();
        contactPhone.setContact("+7 (000) 000-00-00");
        contactPhone.setType(EnumContactType.PHONE);
        contactPhone.setUser(user);
        UserContact contactEmail = new UserContact();
        contactPhone.setContact("1@1.ru");
        contactPhone.setType(EnumContactType.EMAIL);
        contactPhone.setUser(user);
        user.setId(29);
        user.setName("1");
        user.setPassword("$2a$10$MsGUSMrnO5p6c5rB1wN81Ob24/QKi5lkFscyHJeFpLANTST/SJ61u");
        userDetails = new BookstoreUserDetails(user);
    }

    @AfterEach
    void tearDown() {
        user = null;
        userDetails = null;
    }

    @Test
    @Transactional
    void getAuthenticationToken() {
        UsernamePasswordAuthenticationToken authenticationToken = bookstoreUserDetailsService
                .getAuthenticationToken(userDetails, httpServletRequest);
        assertNotNull(authenticationToken);
        assertThat(authenticationToken.toString(), Matchers.containsString("Granted Authorities: ROLE_USER"));
    }
}