package com.example.MyBookShopApp.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class DateDto {
    private Date dateFrom = new Date();
    private Date dateTo = new Date();
}
