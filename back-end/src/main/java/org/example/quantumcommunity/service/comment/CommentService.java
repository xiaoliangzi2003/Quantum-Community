package org.example.quantumcommunity.service.comment;

import org.example.quantumcommunity.model.Comment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author xiaol
 */
@Service
public interface CommentService {
    Comment addComment(Comment comment);

    Map<Integer,Comment> getCommentMapByPage(int pageNum, int pageSize, int articleId,String loginUsername);

    List<Comment> getSubCommentListByPage(int pageNum, int pageSize,  int commentId,int articleId);

    void deleteComment(int commentId);

    void likeComment(int commentId, String commentator,int flag);

    void unlikeComment(int commentId, String commentator);

    void sendCommentToQueue(Map<String,Object> comment);

    Comment getCommentById(Integer replyCommentId);

    void syncCommentToDatabase();
}
