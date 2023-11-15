package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.BookstoreUser;
import com.example.MyBookShopApp.data.Message;
import com.example.MyBookShopApp.reposotories.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@AllArgsConstructor
@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final BookstoreUserRegister userRegister;

    public void getAnonymousMessage(Message message) {
        message.setTime(LocalDateTime.now());
        messageRepository.save(message);
    }

    public void getAuthUserMessage(Message message, OAuth2User principal) {
        BookstoreUser currentUser = userRegister.getCurrentUser(principal);
        message.setTime(LocalDateTime.now());
        message.setUser(currentUser);
        messageRepository.save(message);
    }
}
