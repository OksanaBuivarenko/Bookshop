package com.example.MyBookShopApp.security;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.util.List;
import java.util.Map;

@Component
@Getter
@Setter
public class CookieHandlerJwt {
    private final HttpServletRequest request;

    public CookieHandlerJwt(HttpServletRequest request) {
        this.request = request;
    }

    public String getToken() {
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")) {
                token = cookie.getValue();
            }
        }
        return token;
    }
}
