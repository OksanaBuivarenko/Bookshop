package com.example.MyBookShopApp.data.book;

import com.example.MyBookShopApp.data.BookstoreUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "book_review")
@ApiModel(description = "entity representing a book reviews")
public class BookReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("id generated by db automaticaly")
    private int id;

    @Column(columnDefinition = "DATE NOT NULL")
    @ApiModelProperty(" time of review")
    private LocalDateTime time;

    @Column(columnDefinition = "TEXT NOT NULL")
    @ApiModelProperty("review text")
    private String text;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private BookstoreUser user;

    @OneToMany(mappedBy = "bookReview")
    private List<BookReviewLike> bookReviewLikes = new ArrayList<>();

    transient int bookReviewLikeRating;
    transient int likeCount;
    transient int dislikeCount;
    transient List<String> starsColorsReview;
}