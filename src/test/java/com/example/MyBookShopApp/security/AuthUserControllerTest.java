package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.BookstoreUser;
import com.example.MyBookShopApp.data.UserContact;
import com.example.MyBookShopApp.reposotories.BookstoreUserRepository;
import com.example.MyBookShopApp.reposotories.UserContactRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("/application-test.properties")
class AuthUserControllerTest {
    private final MockMvc mockMvc;
    private final BookstoreUserRepository bookstoreUserRepository;
    private final UserContactRepository userContactRepository;

    @Autowired
    AuthUserControllerTest(MockMvc mockMvc, BookstoreUserRepository bookstoreUserRepository, UserContactRepository userContactRepository) {
        this.mockMvc = mockMvc;
        this.bookstoreUserRepository = bookstoreUserRepository;
        this.userContactRepository = userContactRepository;
    }

    @AfterEach
    void tearDown() {
        UserContact phone = userContactRepository.findByContact("+7 (000) 000-00-00");
        if (phone !=null) {
            userContactRepository.delete(phone);
        }
        UserContact email = userContactRepository.findByContact("1@1.ru");
        if (email !=null) {
            userContactRepository.delete(email);
        }
        BookstoreUser user = bookstoreUserRepository.findBookstoreUserByContactsContact("1@1.ru");
        if (user !=null){
        bookstoreUserRepository.delete(user);}
    }


    @Test
    void handleUserRegistration() throws Exception{
        mockMvc.perform(post("/reg")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "1")
                        .param("phone", "+7 (000) 000-00-00")
                        .param("phoneCode", "111+111")
                        .param("email", "1@1.ru")
                        .param("mailCode", "111+111")
                        .param("pass", "123456")
                 )
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void handleUserRegistrationFalse() throws Exception{
        mockMvc.perform(post("/reg")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "2")
                        .param("phone", "+7(222)222-22-22")
                        .param("phoneCode", "111+111")
                        .param("email", "2@2.com")
                        .param("mailCode", "111+111")
                        .param("pass", "123456")
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("errorMessage","User with that email already used, try again with other email"));
    }

    @Test
    void correctLogoutTest() throws Exception {
        mockMvc.perform(get("/logout"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().doesNotExist("JSESSIONID"))
                .andExpect(redirectedUrl("/signin"));
    }


}