package com.example.MyBookShopApp.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "faq")
@ApiModel(description = "entity representing frequently asked questions and answers")
public class Faq {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("id generated by db automaticaly")
    private Integer id;

    @Column(columnDefinition = "INT DEFAULT 0")
    @ApiModelProperty("ordinal number of the question in the list of questions on the \"Help\" page, default 0")
    private int sortIndex;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    @ApiModelProperty("question")
    private String question;

    @Column(columnDefinition = "TEXT NOT NULL")
    @ApiModelProperty("answer in HTML format")
    private String answer;

}
