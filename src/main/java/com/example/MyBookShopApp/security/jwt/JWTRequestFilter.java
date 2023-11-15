package com.example.MyBookShopApp.security.jwt;

import com.example.MyBookShopApp.security.BlackListToken;
import com.example.MyBookShopApp.security.BookstoreUserDetails;
import com.example.MyBookShopApp.services.BookstoreUserDetailsService;
import lombok.SneakyThrows;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final JWTUtil jwtUtil;
    private final BlackListToken blackListToken;

    public JWTRequestFilter(BookstoreUserDetailsService bookstoreUserDetailsService, JWTUtil jwtUtil,
                            BlackListToken blackListToken) {
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.jwtUtil = jwtUtil;
        this.blackListToken = blackListToken;
    }

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) {
        String token = null;
        String username = null;
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                try {
                    if (cookie.getName().equals("token")) {
                        token = getToken(cookie);
                        username = getUsername(token);
                    }
                    authenticate(username, token, httpServletRequest);
                } catch (Exception e) {
                    System.out.println("Cannot set user authentication");
                }
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    public void authenticate(String username, String token, HttpServletRequest httpServletRequest) {
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            BookstoreUserDetails userDetails =
                    (BookstoreUserDetails) bookstoreUserDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(token, userDetails)) {
                bookstoreUserDetailsService.getAuthenticationToken(userDetails, httpServletRequest);
            }
        }
    }

    public String getUsername(String token) {
        if (!blackListToken.getTokenBlackList().contains(token)) {
            return jwtUtil.extractUsername(token);
        }
        return null;
    }

    public String getToken(Cookie cookie) {
        String token = cookie.getValue();
        return token;
    }
}