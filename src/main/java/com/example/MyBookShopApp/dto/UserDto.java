package com.example.MyBookShopApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String name;
    private String email;
    private String phone;
    private String password;
    private String passwordReply;
}
