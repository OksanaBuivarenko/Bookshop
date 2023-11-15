package com.example.MyBookShopApp.security;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
public class BlackListToken {

    private List<String> tokenBlackList = new ArrayList<>();

    public void addTokenBlackList(String token) {
        tokenBlackList.add(token);
        System.out.println(tokenBlackList.toString());
    }
}
