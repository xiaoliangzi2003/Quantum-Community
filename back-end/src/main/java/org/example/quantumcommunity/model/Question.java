package org.example.quantumcommunity.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.quantumcommunity.annotation.NotNull;
import org.example.quantumcommunity.annotation.RandomId;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.Serializable;
import java.util.Date;

/**
 * @author xiaol
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question implements Serializable {
    @RandomId
    private Integer questionId;
    @NotNull
    private String questionTitle;
    private long publishTime;
    private long updateTime;
    private String questionDescription;
    private Integer questionAuthorId;
    private String category;
    private String questionStatus;
    private int views;
    private int answers;
    private int likes;
    private int collects;
    private int forwards;
    private String tags;
}
