package com.example.MyBookShopApp.reposotories;

import com.example.MyBookShopApp.data.UserContact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserContactRepository extends JpaRepository<UserContact, Integer> {
    UserContact findByContact(String s);
}
