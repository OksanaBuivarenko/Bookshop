package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.BookstoreUser;
import com.example.MyBookShopApp.security.BookstoreUserDetails;
import com.example.MyBookShopApp.reposotories.BookstoreUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
@AllArgsConstructor
@Service
public class BookstoreUserDetailsService implements UserDetailsService {
    private final BookstoreUserRepository bookstoreUserRepository;

    @Override
    public UserDetails loadUserByUsername(String contact) throws UsernameNotFoundException {
        BookstoreUser bookstoreUser = new BookstoreUser();
        bookstoreUser = bookstoreUserRepository.findBookstoreUserByContactsContact(contact);
        if (bookstoreUser != null) {
            return new BookstoreUserDetails(bookstoreUser);
        }
        throw new UsernameNotFoundException("User not found with email/phone: !" + contact);
    }

    public boolean findUserByEmail(String email) {
        BookstoreUser bookstoreUser = bookstoreUserRepository.findBookstoreUserByContactsContact(email);
        return bookstoreUser != null;
    }

    public UsernamePasswordAuthenticationToken getAuthenticationToken(BookstoreUserDetails userDetails,
                                                                      HttpServletRequest httpServletRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        return authenticationToken;
    }
}
