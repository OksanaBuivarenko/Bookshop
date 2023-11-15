package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.EmailLink;
import com.example.MyBookShopApp.reposotories.EmailLinksRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@AllArgsConstructor
@Service
public class EmailLinkService {
    private final EmailLinksRepository linksRepository;

    public String generateLink(String userName) {
        String link = "http://localhost:8085/profile_changed/" + userName + "!!!" + LocalDateTime.now().toString();
        return link;
    }

    public void saveNewLink(EmailLink link) {
        if (linksRepository.findByLink(link.getLink()) == null) {
            linksRepository.save(link);
        }
    }

    public Boolean verifyLink(String link) {
        EmailLink emailLink = linksRepository.findByLink(link);
        return (emailLink != null && !emailLink.isExpired());
    }
}
