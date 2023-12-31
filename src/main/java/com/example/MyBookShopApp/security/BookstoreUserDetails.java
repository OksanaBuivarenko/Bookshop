package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.BookstoreUser;
import com.example.MyBookShopApp.data.EnumContactType;
import com.example.MyBookShopApp.data.EnumUserRoles;
import com.example.MyBookShopApp.data.UserContact;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

public class BookstoreUserDetails implements UserDetails {

    private final BookstoreUser bookstoreUser;

    public BookstoreUserDetails(BookstoreUser bookstoreUser) {
        this.bookstoreUser = bookstoreUser;
    }

    public BookstoreUser getBookstoreUser() {
        return bookstoreUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return bookstoreUser.getPassword();
    }

    @Override
    public String getUsername() {
        String email = "";
        for (UserContact contact : bookstoreUser.getContacts()) {
            if (contact.getType() == EnumContactType.EMAIL) {
                email = contact.getContact();
            }
        }
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
