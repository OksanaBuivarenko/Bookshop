package com.example.MyBookShopApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookDto {
    private String title;
    private String author;
    private String image;
    private String description;
    private int price;
    private int discount;
    private int bestseller;
    private String tag;
}
