package com.example.MyBookShopApp.data.book;

import com.example.MyBookShopApp.data.BookstoreUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "book_review_like")
@ApiModel(description = "entity representing a book review likes")
public class BookReviewLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("id generated by db automaticaly")
    private int id;

    @ManyToOne
    @JoinColumn(name = "review_id", referencedColumnName = "id")
    private BookReview bookReview;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private BookstoreUser user;

    @Column(columnDefinition = "DATE NOT NULL")
    @ApiModelProperty("date and time at which the like or dislike was placed")
    private LocalDateTime time;

    @Column(columnDefinition = "SMALLINT NOT NULL")
    @ApiModelProperty("like = 1 or dislike = -1")
    private short value;
}
