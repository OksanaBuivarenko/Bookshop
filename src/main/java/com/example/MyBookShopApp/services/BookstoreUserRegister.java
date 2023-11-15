package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.BookstoreUser;
import com.example.MyBookShopApp.data.EnumContactType;
import com.example.MyBookShopApp.data.EnumUserRoles;
import com.example.MyBookShopApp.data.UserContact;
import com.example.MyBookShopApp.dto.UserDto;
import com.example.MyBookShopApp.reposotories.BookstoreUserRepository;
import com.example.MyBookShopApp.reposotories.UserContactRepository;
import com.example.MyBookShopApp.security.*;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.Collections;
@AllArgsConstructor
@Slf4j
@Service
public class BookstoreUserRegister {
    private final BookstoreUserRepository bookstoreUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final JWTUtil jwtUtil;
    private final UserContactRepository userContactRepository;

    public BookstoreUser registerNewUser(RegistrationForm registrationForm) {
        if (bookstoreUserRepository.findBookstoreUserByContactsContact(registrationForm.getEmail()) == null) {
            BookstoreUser user = new BookstoreUser();
            user.setName(registrationForm.getName());
            user.setPassword(passwordEncoder.encode(registrationForm.getPass()));
            user.setHash(String.valueOf(user.hashCode()));
            user.setRegTime(LocalDateTime.now());
            user.setRole(EnumUserRoles.USER);

            UserContact contactEmail = new UserContact();
            contactEmail.setContact(registrationForm.getEmail());
            contactEmail.setApproved(1);
            contactEmail.setType(EnumContactType.EMAIL);
            contactEmail.setUser(user);

            UserContact contactPhone = new UserContact();
            contactPhone.setContact(registrationForm.getPhone());
            contactPhone.setApproved(1);
            contactPhone.setType(EnumContactType.PHONE);
            contactPhone.setUser(user);

            bookstoreUserRepository.save(user);
            userContactRepository.save(contactPhone);
            userContactRepository.save(contactEmail);
            return user;
        }
        return null;
    }

    public boolean correctPassword(UserDto userDto) {
        return userDto.getPassword().equals(userDto.getPasswordReply()) && userDto.getPassword().length() == 6 ||
                userDto.getPassword().isEmpty();
    }

    public BookstoreUser changedUser(BookstoreUser currentUser, Model model) {
        BookstoreUser user = bookstoreUserRepository.findBookstoreUserById(currentUser.getId());
        user.setName(currentUser.getNewName());
        for (UserContact contact : user.getContacts()) {
            if (contact.getType() == EnumContactType.PHONE) {
                contact.setContact(currentUser.getNewPhone());
            }
            if (contact.getType() == EnumContactType.EMAIL) {
                contact.setContact(currentUser.getNewEmail());
            }
        }
        if (!currentUser.getPasswordReply().isEmpty()) {
            user.setPassword(passwordEncoder.encode(currentUser.getPasswordReply()));
            log.info("Password changed successfully " + currentUser.getPasswordReply());
        }
        bookstoreUserRepository.save(user);
        currentUser.setName(currentUser.getNewName());
        model.addAttribute("phone", currentUser.getNewPhone());
        model.addAttribute("email", currentUser.getNewEmail());
        return currentUser;
    }

    public String getPhone(BookstoreUser user) {
        String phone = "";
        for (UserContact contact : user.getContacts()) {
            if (contact.getType() == EnumContactType.PHONE) {
                phone = contact.getContact();
            }
        }
        return phone;
    }

    public String getEmail(BookstoreUser user) {
        String email = "";
        for (UserContact contact : user.getContacts()) {
            if (contact.getType() == EnumContactType.EMAIL) {
                email = contact.getContact();
            }
        }
        return email;
    }

    public ContactConfirmationResponse login(ContactConfirmationPayload payload) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
                (payload.getContact(), payload.getCode()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("true");
        return response;
    }

    public ContactConfirmationResponse jwtLogin(ContactConfirmationPayload payload) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getContact(),
                payload.getCode()));
        BookstoreUserDetails userDetails =
                (BookstoreUserDetails) bookstoreUserDetailsService.loadUserByUsername(payload.getContact());
        String jwtToken = jwtUtil.generateToken(userDetails);
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult(jwtToken);
        return response;
    }

    public BookstoreUser getCurrentUser(OAuth2User principal) {
        BookstoreUser currentUser = (principal != null) ? getCurrentUserOAuth(principal) : getUsuallyUser();
        return currentUser;
    }

    public boolean isAdmin(OAuth2User principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        BookstoreUser currentUser = getCurrentUser(principal);
        BookstoreUser user = bookstoreUserRepository.findBookstoreUserById(currentUser.getId());
        return user.getRole() == EnumUserRoles.ADMIN;
    }

    public BookstoreUser getUsuallyUser() {
        BookstoreUserDetails userDetails = (BookstoreUserDetails) SecurityContextHolder.getContext().
                getAuthentication().getPrincipal();
        return userDetails.getBookstoreUser();
    }

    public BookstoreUser getCurrentUserOAuth(OAuth2User principal) {
        String email = Collections.singletonMap("email", principal.getAttribute("email")).values().toString().
                replaceAll("[\\[\\]]", "");
        BookstoreUser bookstoreUser = bookstoreUserRepository.findBookstoreUserByContactsContact(email);
        if (bookstoreUser != null) {
            return bookstoreUser;
        }
        BookstoreUser user = new BookstoreUser();
        log.info("Create new user with email - " + email);
        UserContact contactEmail = new UserContact();
        contactEmail.setContact(email);
        contactEmail.setApproved(1);
        contactEmail.setType(EnumContactType.EMAIL);
        contactEmail.setUser(user);
        user.setName(Collections.singletonMap("name", principal.getAttribute("name")).values()
                .toString().replaceAll("[\\[\\]]", ""));
        UserContact contactPhone = new UserContact();
        contactPhone.setContact(Collections.singletonMap("name", principal.getAttribute("phone")).values()
                .toString().replaceAll("[\\[\\]]", ""));
        contactPhone.setApproved(1);
        contactPhone.setType(EnumContactType.PHONE);
        contactPhone.setUser(user);
        user.setHash(String.valueOf(user.hashCode()));
        user.setRegTime(LocalDateTime.now());
        bookstoreUserRepository.save(user);
        userContactRepository.save(contactPhone);
        userContactRepository.save(contactEmail);
        return user;
    }
}
