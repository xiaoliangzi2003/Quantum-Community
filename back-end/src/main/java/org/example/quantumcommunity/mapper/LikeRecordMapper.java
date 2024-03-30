package org.example.quantumcommunity.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.quantumcommunity.model.LikeRecord;

/**
 * @author xiaol
 */
@Mapper
public interface LikeRecordMapper {

    @Select("SELECT * FROM like_record WHERE userId = #{userId} AND articleOrCommentId = #{articleOrCommentId} AND type = #{type}")
    LikeRecord findUserLikeArticleInDb(Integer userId, Integer articleOrCommentId, Integer type);

    @Select("INSERT INTO like_record (userId, articleOrCommentId, instrumentTime, type) VALUES (#{userId}, #{articleOrCommentId}, #{instrumentTime}, #{type})")
    void insertLikeRecord(LikeRecord likeRecord);

    @Delete("DELETE FROM like_record WHERE userId = #{userId} AND articleOrCommentId = #{articleOrCommentId} AND type = #{type}")
    void deleteUserLikeArticleInDb(Integer userId, Integer articleOrCommentId, Integer type);
}
