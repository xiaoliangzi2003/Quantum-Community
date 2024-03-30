package org.example.quantumcommunity.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.quantumcommunity.annotation.NotNull;
import org.example.quantumcommunity.annotation.RandomId;

import java.io.Serializable;
import java.util.List;
/**
 * @author xiaol
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment implements Serializable {
    @RandomId
    private Integer id;
    @NotNull
    private String content;
    private Integer articleId;
    private String commentator;
    private Integer parentCommentId;
    private Integer repliedCommentId;
    private Integer likes;
    private Long createTime;
    private String status;
    private List<Comment> secondLevelComment;
    private String repliedContent;
    private Boolean isLiked;
    private String repliedCommentator;
}
