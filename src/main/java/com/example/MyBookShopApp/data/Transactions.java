package com.example.MyBookShopApp.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Setter
@Entity
@Table(name = "balance_transaction")
@ApiModel(description = "entity representing a balance transaction")
public class Transactions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty("id generated by db automaticaly")
    private Integer id;

    @Column(columnDefinition = "TIMESTAMP NOT NULL")
    @ApiModelProperty("date and time of the transaction")
    private LocalDateTime time;

    @Column(columnDefinition = "INT DEFAULT 0")
    @ApiModelProperty("transaction size (positive — enrollment, negative — debit)")
    private int value;

    @Column(columnDefinition = "TEXT NOT NULL")
    @ApiModelProperty("description of the transaction: if crediting, then from where, if debiting, then to what")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private BookstoreUser user;

    @Column(name = "book_id", columnDefinition = "INT DEFAULT NULL")
    @JsonIgnore
    private int bookId;

    public String getTime() {
        return String.valueOf((time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
    }

    public String getFormattedTime() {
        String[] monthNames = {"января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентябрь",
                "октября", "ноября", "декабря"};
        String month = monthNames[time.getMonthValue() - 1];
        String dateTime = time.getDayOfMonth() + " " + month + " " + time.getYear()
                + " " + time.getHour() + ":" + time.getMinute();
        return dateTime;
    }
}
