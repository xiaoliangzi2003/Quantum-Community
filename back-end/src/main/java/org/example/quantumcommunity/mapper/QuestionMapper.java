package org.example.quantumcommunity.mapper;

import org.apache.ibatis.annotations.*;
import org.example.quantumcommunity.model.Question;

import java.util.List;

/**
 * @author xiaol
 */
@Mapper
public interface QuestionMapper {

    @Select("SELECT * FROM Question ORDER BY publishTime DESC")
    List<Question> getQuestionList();

    @Insert("INSERT INTO Question (" +
            "questionId,"+
            "questionTitle," +
            "publishTime, " +
            "updateTime, " +
            "questionDescription," +
            "questionAuthorId, " +
            "category," +
            "questionStatus," +
            "views," +
            "answers," +
            "likes, " +
            "collects, " +
            "forwards, " +
            "tags) VALUES (" +
            "#{questionId}," +
            "#{questionTitle}, " +
            "#{publishTime}, " +
            "#{updateTime}, " +
            "#{questionDescription}, " +
            "#{questionAuthorId}," +
            "#{category}, " +
            "#{questionStatus}," +
            "#{views}, " +
            "#{answers}, " +
            "#{likes}," +
            "#{collects}, " +
            "#{forwards}, " +
            "#{tags})")
    void addQuestion(Question question);

    @Select("SELECT * FROM Question WHERE questionId = #{questionId}")
    boolean findQuestionById(Integer questionId);

    @Update("UPDATE Question SET " +
            "questionTitle = #{questionTitle}, " +
            "publishTime = #{publishTime}, " +
            "updateTime = #{updateTime}, " +
            "questionDescription = #{questionDescription}, " +
            "questionAuthorId = #{questionAuthorId}, " +
            "category = #{category}, " +
            "questionStatus = #{questionStatus}, " +
            "views = #{views}, " +
            "answers = #{answers}, " +
            "likes = #{likes}, " +
            "collects = #{collects}, " +
            "forwards = #{forwards}, " +
            "tags = #{tags} " +
            "WHERE questionId = #{questionId}")
    void updateQuestion(Question question);

    @Select("SELECT * FROM Question WHERE questionId = #{questionId}")
    Question getQuestionById(int questionId);

    @Delete("DELETE FROM Question WHERE questionId = #{questionId}")
    void deleteQuestion(int questionId);
}
