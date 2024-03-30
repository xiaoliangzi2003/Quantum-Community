package org.example.quantumcommunity.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.quantumcommunity.model.Answer;

import java.util.List;

/**
 * @author xiaol
 */
@Mapper
public interface AnswerMapper {

    @Select("SELECT * FROM answer WHERE questionId = #{questionId} LIMIT #{pageNum}, #{pageSize}")
    List<Answer> getAnswerList(int questionId, int pageNum, int pageSize);

    @Insert("INSERT INTO answer (" +
            "id," +
            "title, " +
            "summary, " +
            "top," +
            "authorId, " +
            "views," +
            "likes," +
            "comments," +
            "collects," +
            "forwards," +
            "createTimeStamp," +
            "updateTimeStamp," +
            "author," +
            "category," +
            "tags," +
            "status," +
            "visibility," +
            "questionId"+") VALUES (" +
            "#{id}, " +
            "#{title}, " +
            "#{summary}, " +
            "#{top}, " +
            "#{authorId}," +
            "#{views}, " +
            "#{likes}," +
            "#{comments}, " +
            "#{collects}, " +
            "#{forwards}, " +
            "#{createTimeStamp}, " +
            "#{updateTimeStamp}, " +
            "#{author}, " +
            "#{category}, " +
            "#{tags}, " +
            "#{status}, " +
            "#{visibility}, " +
            "#{questionId})")
    Answer addAnswer(Answer answer);

    void updateAnswer(Answer answer);

    @Select("SELECT * FROM answer WHERE id = #{answerId}")
    Answer getAnswerById(Integer answerId);

    @Delete("DELETE FROM answer WHERE id = #{answerId}")
    void deleteAnswer(int answerId);
}
