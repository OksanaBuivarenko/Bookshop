package com.example.MyBookShopApp.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactConfirmationPayload {
    private String contact;
    private String code;
}
