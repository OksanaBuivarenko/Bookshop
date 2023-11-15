package com.example.MyBookShopApp.reposotories;


import com.example.MyBookShopApp.data.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Integer> {
}
