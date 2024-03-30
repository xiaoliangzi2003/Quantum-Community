package org.example.quantumcommunity.service.quesiton_answer;

import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.example.quantumcommunity.component.WebSocketServer;
import org.example.quantumcommunity.exception.GlobalException;
import org.example.quantumcommunity.mapper.QuestionMapper;
import org.example.quantumcommunity.model.Question;
import org.example.quantumcommunity.util.processor.RandomIdProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.example.quantumcommunity.util.security.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author xiaol
 */
@Service
@Slf4j
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Autowired
    private WebSocketServer webSocketServer;


    @Override
    public List<Question> getQuestionList(int pageNum, int pageSize) {
        try{
            List<Question> questionList = new ArrayList<>();
            //先在Redis中查找，如果没有再去数据库中查找
            List<Question> questionListFromRedis = getQuestionFromRedis(pageNum, pageSize);
            questionList.addAll(questionListFromRedis);

            //如果Redis中没有数据或者数据不够，再去数据库中查找
            if(questionList.size() < pageSize){
                PageHelper.startPage(pageNum, pageSize-questionList.size());
                List<Question> questionListFromDb = questionMapper.getQuestionList();
                questionList.addAll(questionListFromDb);
            }
            return questionList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void sendQuestionToQueue(Map<String, Object> question) {
        try{
            String questionTitle = (String) question.get("title");
            String questionDescription = (String) question.get("description");
            String questionTags = (String) question.get("tags");
            Integer questionAuthorId = (Integer) question.get("authorId");
            String questionCategory = (String) question.get("category");

            Question question1 = new Question();

            question1.setQuestionTitle(questionTitle);
            question1.setQuestionDescription(questionDescription);
            question1.setTags(questionTags);
            question1.setQuestionAuthorId(questionAuthorId);
            question1.setCategory(questionCategory);

            question1.setPublishTime(System.currentTimeMillis());
            question1.setUpdateTime(question1.getPublishTime());

            //设置问题状态
            question1.setQuestionStatus("published");

            question1.setViews(0);
            question1.setAnswers(0);
            question1.setLikes(0);
            question1.setCollects(0);
            question1.setForwards(0);

            //生成Id
            RandomIdProcessor.process(question1);

            //发送到消息队列
            String queueName = "question";

            rabbitTemplate.convertAndSend(queueName, question1);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Question addQuestion(Question question) {
        try{
            //添加到Redis中
            String key = "question:" + question.getQuestionId();
            redisTemplate.opsForValue().set(key, question);
            //将问题添加成功的消息通过websocket发送给提出该问题的用户
//            webSocketServer.sendInfo("问题添加成功", question.getQuestionAuthorId().toString());
            return question;
        }catch (Exception e){
           throw new GlobalException("添加问题失败",Result.FAILED);
        }
    }

    @Override
    @Scheduled(fixedRate = 2*60*60*1000)
    public void syncQuestionToDatabase() {
        try{
            Set<String> keys = redisTemplate.keys("question:*");
            if(keys == null){
                return;
            }
            for (String key : keys) {
                Question question = (Question) redisTemplate.opsForValue().get(key);
                Integer questionId = question.getQuestionId();
                //如果问题存在于数据库中，则更新问题，否则添加问题
                if(questionMapper.findQuestionById(questionId)){
                    questionMapper.updateQuestion(question);
                    log.info("问题已更新："+question.getQuestionId()+"更新时间："+question.getUpdateTime()+"ms");
                }else{
                    questionMapper.addQuestion(question);
                    log.info("问题已添加:"+question.getQuestionId());
                }
                //删除Redis中的数据
                redisTemplate.delete(key);
                log.info("问题已删除");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Question updateQuestion(Map<String, Object> map) {
        try{
            Integer questionId = (Integer) map.get("id");
            String questionTitle = (String) map.get("title");
            String questionDescription = (String) map.get("description");
            String questionTags = (String) map.get("tags");
            String questionCategory = (String) map.get("category");

            Question question = new Question();
            //判断问题是否存在:数据库或者Redis中
            if(!questionMapper.findQuestionById(questionId) || redisTemplate.opsForValue().get("question:" + questionId) == null){
                throw new GlobalException("问题不存在", Result.FAILED);
            }

            question.setQuestionTitle(questionTitle);
            question.setQuestionDescription(questionDescription);
            question.setTags(questionTags);
            question.setCategory(questionCategory);

            question.setUpdateTime(System.currentTimeMillis());

            //更新Redis中的数据
            String key = "question:" + question.getQuestionId();
            redisTemplate.opsForValue().set(key, question);
            return question;
        }catch (Exception e){
            throw new GlobalException("更新问题失败", Result.FAILED);
        }
    }

    @Override
    public void deleteQuestion(int questionId, int authorId) {
        try{
            //判断问题是否存在:数据库或者Redis中
            if(!questionMapper.findQuestionById(questionId) || redisTemplate.opsForValue().get("question:" + questionId) == null){
                throw new GlobalException("问题不存在", Result.FAILED);
            }
            //判断问题是否属于该用户
            Question question = questionMapper.getQuestionById(questionId);
            if(question.getQuestionAuthorId() != authorId){
                throw new GlobalException("问题不属于该用户", Result.FAILED);
            }
            //先查找Redis中是否存在该问题
            if(redisTemplate.opsForValue().get("question:" + questionId) != null){
                redisTemplate.delete("question:" + questionId);
            }
            //再查找数据库中是否存在该问题
            if(questionMapper.findQuestionById(questionId)){
                questionMapper.deleteQuestion(questionId);
                log.info("问题已删除："+questionId);
            }
        }catch (Exception e){
            throw new GlobalException("删除问题失败", Result.FAILED);
        }
    }

    @Override
    public Question getQuestionDetail(int id) {
        //先从Redis中查找，如果没有再去数据库中查找
        Question question = (Question) redisTemplate.opsForValue().get("question:" + id);
        if(question == null){
            question = questionMapper.getQuestionById(id);
        }
        return question;
    }

    private List<Question> getQuestionFromRedis(int pageNum, int pageSize) {
        Set<String> keys = redisTemplate.keys("question:*");
        List<Question> questions = new ArrayList<>();
        for (String key : keys) {
            int start = (pageNum - 1) * pageSize;
            int end = pageNum * pageSize - 1;
            List<Object> objects= redisTemplate.opsForList().range(key, start, end);
            for (Object object : objects) {
                questions.add((Question) object);
                log.info("从Redis中获取问题："+((Question) object).getQuestionId());
            }
        }
        return questions;
    }

}
