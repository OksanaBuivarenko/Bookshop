package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.data.Transactions;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TransactionsDto {
    private Integer count;
    private List<Transactions> transactions;

    public TransactionsDto(List<Transactions> transactions) {
        this.transactions = transactions;
        this.count = transactions.size();
    }
}
