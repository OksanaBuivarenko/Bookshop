package com.example.MyBookShopApp.errs;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import javax.security.sasl.AuthenticationException;

public class JwtAuthException extends Exception {
    public JwtAuthException(String message) {
        super(message);
    }
}
