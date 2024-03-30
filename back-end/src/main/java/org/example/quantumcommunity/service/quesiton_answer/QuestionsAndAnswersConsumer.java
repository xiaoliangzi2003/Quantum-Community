package org.example.quantumcommunity.service.quesiton_answer;

import lombok.extern.slf4j.Slf4j;
import org.example.quantumcommunity.model.Answer;
import org.example.quantumcommunity.model.Question;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author xiaol
 */
@Service
@Slf4j
public class QuestionsAndAnswersConsumer {
    @Autowired
    QuestionService questionService;

    @Autowired
    AnswerService answerService;

    @RabbitListener(queues = "question")
    public void receiveQuestion(Question question) {
        questionService.addQuestion(question);
    }

    @RabbitListener(queues = "answer")
    public void receiveAnswer(Answer answer) {
        answerService.addAnswer(answer);
    }
}
