package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.BookstoreUser;
import com.example.MyBookShopApp.data.EnumContactType;
import com.example.MyBookShopApp.data.UserContact;
import com.example.MyBookShopApp.reposotories.BookstoreUserRepository;
import com.example.MyBookShopApp.services.BookstoreUserRegister;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookstoreUserRegisterTest {

    private final BookstoreUserRegister bookstoreUserRegister;
    private final PasswordEncoder passwordEncoder;
    private RegistrationForm registrationForm;

    @MockBean
    private BookstoreUserRepository bookstoreUserRepositoryMock;

    @Autowired
    BookstoreUserRegisterTest(BookstoreUserRegister bookstoreUserRegister, PasswordEncoder passwordEncoder) {
        this.bookstoreUserRegister = bookstoreUserRegister;
        this.passwordEncoder = passwordEncoder;
    }


    @BeforeEach
    void setUp() {
        registrationForm = new RegistrationForm();
        registrationForm.setEmail("test@email.com");
        registrationForm.setName("Tester");
        registrationForm.setPhone("89000000000");
        registrationForm.setPass("111111");
    }

    @AfterEach
    void tearDown() {
        registrationForm = null;
    }

    @Test
    @Transactional
    void registerNewUser() {
        BookstoreUser user = bookstoreUserRegister.registerNewUser(registrationForm);

//        List<UserContact> contacts = user.getContacts();
//        String email = "";
//        String phone = "";
//        for (UserContact contact: contacts){
//            if (contact.getType()== EnumContactType.EMAIL){
//                assertTrue(CoreMatchers.is(contact.getContact()).matches(registrationForm.getEmail()));
//            }
//            else {
//                assertTrue(CoreMatchers.is(contact.getContact()).matches(registrationForm.getPhone()));
//
//            }
//        }
        assertNotNull(user);
        assertTrue(passwordEncoder.matches(registrationForm.getPass(), user.getPassword()));
        assertTrue(CoreMatchers.is(user.getName()).matches(registrationForm.getName()));

        Mockito.verify(bookstoreUserRepositoryMock, Mockito.times(1))
                .save(Mockito.any(BookstoreUser.class));
    }

    @Test
    void registerNewUserFail() {
        Mockito.doReturn(new BookstoreUser())
                .when(bookstoreUserRepositoryMock)
                .findBookstoreUserByContactsContact(registrationForm.getEmail());

        BookstoreUser user = bookstoreUserRegister.registerNewUser(registrationForm);
        assertNull(user);
    }
}