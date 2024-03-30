package org.example.quantumcommunity.service.quesiton_answer;

import org.checkerframework.checker.units.qual.A;
import org.example.quantumcommunity.model.Answer;
import org.example.quantumcommunity.model.Question;

import java.util.List;
import java.util.Map;

/**
 * @author xiaol
 */
public interface AnswerService {
    List<Answer> getAnswerList(int questionId, int pageNum, int pageSize);

    void sendAnswerToQueue(Map<String, Object> map);

    Answer addAnswer(Answer answer);


    void syncAnswerToDatabase();

    Answer updateAnswer(Map<String, Object> map);

    void deleteAnswer(int answerId, int authorId);
}
