package org.example.quantumcommunity.mapper;


import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.quantumcommunity.model.Comment;

import java.util.List;

/**
 * @author xiaol
 */
@Mapper
public interface CommentMapper {
    //添加评论
    @Insert("INSERT INTO Comment (" +
            "content, " +
            "commentator," +
            " parentCommentId," +
            " likes, " +
            "createTime," +
            "id," +
            "articleId,"+
            "repliedCommentId,"+
            "repliedCommentator,"+
            "repliedContent) " +
            "VALUES (#{content}, " +
            "#{commentator}," +
            " #{parentCommentId}, " +
            "#{likes}, " +
            "#{createTime}," +
            "#{id}," +
            "#{articleId},"+
            "#{repliedCommentId},"+
            "#{repliedCommentator},"+
            "#{repliedContent})")
    void addComment(Comment comment);

    //获取一级评论
    @Select("SELECT * FROM Comment " +
            "WHERE articleId = #{articleId} AND parentCommentId = 0 " +
            "ORDER BY likes DESC, createTime DESC")
    List<Comment> getFirstLevelComment(int articleId);



    //获取二级评论
    @Select("SELECT * FROM Comment WHERE articleId = #{articleId} AND parentCommentId = #{parentCommentId}")
    List<Comment> getSecondLevelComment(int articleId, Integer parentCommentId);

    //获取点赞量最高的二级评论
    @Select("SELECT * FROM Comment " +
            "WHERE articleId = #{articleId} AND parentCommentId = #{parentCommentId} " +
            "ORDER BY likes DESC, createTime DESC LIMIT #{pageNum}, #{pageSize}")
    List<Comment> getTopLikedSecondLevelComment(int articleId, Integer parentCommentId,int pageNum, int pageSize);

    @Select("SELECT * FROM Comment WHERE id = #{replyCommentId}")
    Comment getCommentById(Integer replyCommentId);

    @Select("SELECT * FROM Comment WHERE articleId = #{id}")
    List<Comment> getCommentListByArticleId(Integer id);

    void deleteCommentById(Integer id);

    //获取评论的点赞数
    @Select("SELECT likes FROM Comment WHERE id = #{id}")
    long getLikesCount(Integer id);

    @Select("UPDATE Comment SET likes = #{likesCount} WHERE id = #{commentId}")
    void updateLikesCount(Integer commentId, Integer likesCount);

    @Select("SELECT COUNT(*) FROM Comment WHERE id = #{id}")
    Integer checkIfIdExists(Integer id);
}
