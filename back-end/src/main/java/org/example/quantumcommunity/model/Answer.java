package org.example.quantumcommunity.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.quantumcommunity.annotation.NotNull;
import org.example.quantumcommunity.annotation.RandomId;

import java.io.Serializable;

/**
 * @author xiaol
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Answer implements Serializable {
    @RandomId
    private Integer id;
    @NotNull
    private String title;
    private String summary;
    private Boolean top;
    private Integer authorId;
    private Integer views;
    private Integer likes;
    private Integer comments;
    private Integer collects;
    private Integer forwards;
    private long createTimeStamp;
    private long updateTimeStamp;
    private String author;
    private String category;
    private String tags;
    private String status;
    private String content;
    private boolean visibility;
    private Integer questionId;
}
