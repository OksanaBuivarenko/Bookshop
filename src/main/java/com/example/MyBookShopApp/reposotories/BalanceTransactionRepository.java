package com.example.MyBookShopApp.reposotories;

import com.example.MyBookShopApp.data.Transactions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceTransactionRepository extends JpaRepository<Transactions, Integer> {

    Page<Transactions> findAllByUserIdOrderByTimeAsc(int userId, Pageable nextPage);

    Page<Transactions> findAllByUserIdOrderByTimeDesc(int userId, Pageable nextPage);

    Page<Transactions> findAllByUserId(int userId, Pageable nextPage);
}
