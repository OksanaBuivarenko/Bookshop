package com.example.MyBookShopApp.reposotories;

import com.example.MyBookShopApp.data.BookstoreUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookstoreUserRepository extends JpaRepository<BookstoreUser, Integer> {

    BookstoreUser findBookstoreUserByContactsContact(String email);

    BookstoreUser findBookstoreUserById(Integer id);

}