package org.example.quantumcommunity.controller;

import com.github.pagehelper.PageHelper;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.example.quantumcommunity.mapper.UserMapper;
import org.example.quantumcommunity.model.Answer;
import org.example.quantumcommunity.model.Question;
import org.example.quantumcommunity.service.quesiton_answer.AnswerService;
import org.example.quantumcommunity.service.quesiton_answer.QuestionService;
import org.example.quantumcommunity.util.security.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaol
 */
@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    @Qualifier("taskExecutor")
    private TaskExecutor taskExecutor;

    @Autowired
    private UserMapper userMapper;

    private final RateLimiter rateLimiter = RateLimiter.create(100, 1, TimeUnit.SECONDS);

    @PostMapping("/question-list")
    public Result getQuestionList(@RequestBody Map<String,Object> map){
        try{
            int pageNum = (int) map.get("pageNum");
            int pageSize = (int) map.get("pageSize");
            List<Question> questionList = questionService.getQuestionList(pageNum, pageSize);
            return new Result(Result.OK, "获取问题列表成功", questionList);
        }catch (Exception e){
            return new Result(Result.ERROR, "获取问题列表失败", null);
        }
    }

    @PostMapping("/answer-list")
    public Result getAnswerList(@RequestBody Map<String,Object> map){
        try{
            int pageNum = (int) map.get("pageNum");
            int pageSize = (int) map.get("pageSize");
            int questionId = (int) map.get("questionId");
            List<Answer> answerlist = answerService.getAnswerList(questionId, pageNum, pageSize);
            return new Result(Result.OK, "获取回答列表成功", answerlist);
        }catch (Exception e){
            return new Result(Result.ERROR, "获取回答列表失败", null);
        }
    }

    @PostMapping("/add-question")
    public Result addQuestion(@RequestBody Map<String,Object> map){
        try{
            rateLimiter.acquire(15);
            //异步处理：将问题存入消息队列
            taskExecutor.execute(() -> questionService.sendQuestionToQueue(map));
            return new Result(Result.OK, "问题已提交，正在处理中", null);
        }catch (Exception e){
            return new Result(Result.ERROR, "添加问题失败", null);
        }
    }

    @PostMapping("/add-answer")
    public Result addAnswer(@RequestBody Map<String,Object> map){
        try{
            rateLimiter.acquire(15);
            //异步处理：将回答存入消息队列
            taskExecutor.execute(() -> answerService.sendAnswerToQueue(map));
            return new Result(Result.OK, "回答已提交，正在处理中", null);
        }catch (Exception e){
            return new Result(Result.ERROR, "添加回答失败", null);
        }
    }

    @PutMapping("/update-question")
    public Result updateQuestion(@RequestBody Map<String,Object> map){
        try{
            Question quesiton = questionService.updateQuestion(map);
            return new Result(Result.OK, "更新问题成功", quesiton);
        }catch (Exception e){
            return new Result(Result.ERROR, "更新问题失败", null);
        }
    }

    @PutMapping("/update-answer")
    public Result updateAnswer(@RequestBody Map<String,Object> map){
        try{
            Answer answer = answerService.updateAnswer(map);
            return new Result(Result.OK, "更新回答成功", answer);
        }catch (Exception e){
            return new Result(Result.ERROR, "更新回答失败", null);
        }
    }

    @DeleteMapping("/delete-question")
    public Result deleteQuestion(@RequestBody Map<String,Object> map){
        try{
            int questionId = (int) map.get("questionId");
            int authorId = (int) map.get("authorId");
            questionService.deleteQuestion(questionId,authorId);
            return new Result(Result.OK, "删除问题成功", null);
        }catch (Exception e){
            return new Result(Result.ERROR, "删除问题失败", null);
        }
    }

    @DeleteMapping("/delete-answer")
    public Result deleteAnswer(@RequestBody Map<String,Object> map){
        try{
            int answerId = (int) map.get("answerId");
            int authorId = (int) map.get("authorId");
            answerService.deleteAnswer(answerId,authorId);
            return new Result(Result.OK, "删除回答成功", null);
        }catch (Exception e){
            return new Result(Result.ERROR, "删除回答失败", null);
        }
    }

    @GetMapping("/question-detail/{id}")
    public Result getQuestionDetail(@PathVariable("id") int id){
        try{
            Map<String,Object> result = new HashMap<>();
            Question question = questionService.getQuestionDetail(id);
            Integer questionAuthorId = question.getQuestionAuthorId();
            String questionAuthor = userMapper.getUserById(questionAuthorId).getUsername();
            result.put("question",question);
            result.put("questionAuthor",questionAuthor);

            //获取问题更新时间
            Instant createInstant = Instant.ofEpochMilli(question.getUpdateTime());
            LocalDateTime createLocalDateTime = LocalDateTime.ofInstant(createInstant, ZoneId.systemDefault());
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String updateTime = createLocalDateTime.format(dateTimeFormatter);
            result.put("updateTime",updateTime);
            return new Result(Result.OK, "获取问题详情成功", result);
        }catch (Exception e){
            return new Result(Result.ERROR, "获取问题详情失败", null);
        }
    }
}
