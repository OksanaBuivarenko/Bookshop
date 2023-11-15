package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.reposotories.BalanceTransactionRepository;
import com.example.MyBookShopApp.data.book.Book;
import com.example.MyBookShopApp.data.Transactions;
import com.example.MyBookShopApp.data.BookstoreUser;
import com.example.MyBookShopApp.reposotories.BookstoreUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
@AllArgsConstructor
@Service
public class PaymentService {

    @Value("${robokassa.merchant.login}")
    private String merchantLogin;

    @Value("${robokassa.pass.first.test}")
    private String firstTestPass;

    private final BalanceTransactionRepository balanceTransactionRepository;
    private final BookstoreUserRepository userRepository;

    @Autowired
    public PaymentService(BalanceTransactionRepository balanceTransactionRepository, BookstoreUserRepository userRepository) {
        this.balanceTransactionRepository = balanceTransactionRepository;
        this.userRepository = userRepository;
    }

    public String getPaymentUrl(List<Book> booksFromCookieSlugs) throws NoSuchAlgorithmException {
        Double paymentSumTotal = booksFromCookieSlugs.stream().mapToDouble(Book::discountPrice).sum();
        MessageDigest md = MessageDigest.getInstance("MD5");
        String invId = "5"; //just for testing TODO order indexing later
        md.update((merchantLogin + ":" + paymentSumTotal.toString() + ":" + invId + ":" + firstTestPass).getBytes());
        return "https://auth.robokassa.ru/Merchant/Index.aspx" +
                "?MerchantLogin=" + merchantLogin +
                "&InvId=" + invId +
                "&Culture=ru" +
                "&Encoding=utf-8" +
                "&OutSum=" + paymentSumTotal.toString() +
                "&SignatureValue=" + DatatypeConverter.printHexBinary(md.digest()).toUpperCase() +
                "&IsTest=1";
    }

    public Transactions enrollmentTransaction(int paymentSum, BookstoreUser user) {
        Transactions transaction = new Transactions();
        transaction.setUser(user);
        transaction.setTime(LocalDateTime.now());
        transaction.setValue(+paymentSum);
        transaction.setDescription("Пополнение счета");
        balanceTransactionRepository.save(transaction);
        return transaction;
    }

    public void balanceEnrollment(int paymentSum, BookstoreUser user) {
        user.setBalance(user.getBalance() + paymentSum);
        userRepository.save(user);
    }

    public int getPaymentSumTotal(List<Book> books) {
        return books.stream().mapToInt(Book::discountPrice).sum();
    }

    public void BalanceDebit(BookstoreUser user, int paymentSumTotal) {
        user.setBalance(user.getBalance() - paymentSumTotal);
        userRepository.save(user);
    }

    public void debitTransactions(BookstoreUser user, List<Book> booksFromCookieSlugs) {
        for (Book book : booksFromCookieSlugs) {
            Transactions transaction = new Transactions();
            transaction.setBookId(book.getId());
            transaction.setUser(user);
            transaction.setTime(LocalDateTime.now());
            transaction.setValue(-book.discountPrice());
            transaction.setDescription("Покупка книги " + book.getTitle());
            balanceTransactionRepository.save(transaction);
        }
    }

    public Page<Transactions> getTransactionsList(BookstoreUser user, Integer offset, Integer limit, String sort) {
        Pageable nextPage = PageRequest.of(offset, limit);
        if (sort.contains("asc")) {
            return balanceTransactionRepository.findAllByUserIdOrderByTimeAsc(user.getId(), nextPage);
        }
        return balanceTransactionRepository.findAllByUserIdOrderByTimeDesc(user.getId(), nextPage);
    }
}
