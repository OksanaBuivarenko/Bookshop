package com.example.MyBookShopApp.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "email_links")
public class EmailLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String link;
    private LocalDateTime expireTime;

    public EmailLink(String link, Integer expireIn) {
        this.link = link;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

    public EmailLink() {
    }

    public Boolean isExpired() {
        return LocalDateTime.now().isAfter(expireTime);
    }
}
