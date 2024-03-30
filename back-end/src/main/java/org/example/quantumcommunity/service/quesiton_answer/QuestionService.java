package org.example.quantumcommunity.service.quesiton_answer;

import org.example.quantumcommunity.model.Question;

import java.util.List;
import java.util.Map;

/**
 * @author xiaol
 */
public interface QuestionService {

    List<Question> getQuestionList(int pageNum, int pageSize);

    void sendQuestionToQueue(Map<String,Object> question);

    Question addQuestion(Question question);

    public void syncQuestionToDatabase();
    Question updateQuestion(Map<String, Object> map);

    void deleteQuestion(int questionId, int authorId);

    Question getQuestionDetail(int id);
}
