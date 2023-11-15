package com.example.MyBookShopApp.data.book;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "book2tag")
public class Book2Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "book_id", columnDefinition = "INT NOT NULL")
    private int bookId;

    @Column(name = "tag_id", columnDefinition = "INT NOT NULL")
    private int tagId;
}
