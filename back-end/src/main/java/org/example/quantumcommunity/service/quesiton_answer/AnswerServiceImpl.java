package org.example.quantumcommunity.service.quesiton_answer;

import com.github.pagehelper.PageHelper;
import org.example.quantumcommunity.component.WebSocketServer;
import org.example.quantumcommunity.exception.GlobalException;
import org.example.quantumcommunity.mapper.AnswerMapper;
import org.example.quantumcommunity.mapper.UserMapper;
import org.example.quantumcommunity.model.Answer;
import org.example.quantumcommunity.model.Question;
import org.example.quantumcommunity.model.User;
import org.example.quantumcommunity.service.file.ContentUtil;
import org.example.quantumcommunity.util.processor.RandomIdProcessor;
import org.example.quantumcommunity.util.security.Result;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author xiaol
 */
@Service
public class AnswerServiceImpl implements AnswerService {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private WebSocketServer webSocketServer;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Answer> getAnswerList(int questionId, int pageNum, int pageSize) {
        try{
            List<Answer> answerList = new ArrayList<>();
            //先在Redis中查找，如果没有再去数据库中查找
            List<Answer> answerListFromRedis = getAnswerFromRedis(questionId, pageNum, pageSize);
            answerList.addAll(answerListFromRedis);

            //如果Redis中没有数据或者数据不够，再去数据库中查找
            if(answerList.size() < pageSize){
                PageHelper.startPage(pageNum, pageSize-answerList.size());
                List<Answer> answerListFromDb = answerMapper.getAnswerList(questionId, pageNum, pageSize-answerList.size());
                answerList.addAll(answerListFromDb);
            }
            return answerList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void sendAnswerToQueue(Map<String, Object> map) {
        try{
            Answer answer = new Answer();
            String answerTitle = (String) map.get("title");
            String content = (String) map.get("content");
            String summary = ContentUtil.getSummaryFromContent(content);
            Integer authorId = (Integer) map.get("authorId");
            Integer questionId = (Integer) map.get("questionId");
            String category = (String) map.get("category");
            String tags = (String) map.get("tags");

            answer.setTitle(answerTitle);
            answer.setContent(content);
            answer.setSummary(summary);
            answer.setAuthorId(authorId);
            answer.setQuestionId(questionId);
            answer.setCategory(category);
            answer.setTags(tags);

            RandomIdProcessor.process(answer);

            answer.setCreateTimeStamp(System.currentTimeMillis());
            answer.setUpdateTimeStamp(answer.getCreateTimeStamp());

            answer.setTop(false);
            answer.setViews(0);
            answer.setLikes(0);
            answer.setComments(0);
            answer.setCollects(0);
            answer.setForwards(0);
            answer.setStatus("published");
            answer.setVisibility(true);
            User user = userMapper.getUserById(authorId);
            answer.setAuthor(user.getUsername());

            //发送到消息队列
            rabbitTemplate.convertAndSend("answer", answer);
        }catch (Exception e) {
            throw  new GlobalException("添加回答失败", Result.FAILED);
        }
    }

    @Override
    public Answer addAnswer(Answer answer) {
        try{
            //添加到Redis
            String key = "answer:" + answer.getQuestionId() + ":" + answer.getId();
            redisTemplate.opsForList().leftPush(key, answer);
            //通过WebSocket通知前端
//            webSocketServer.sendInfo("有新回答", String.valueOf(answer.getAuthorId()));
            return answer;
        }catch (Exception e) {
            throw new GlobalException("添加回答失败", Result.FAILED);
        }
    }

    @Scheduled(fixedRate = 2*60*60*1000)
    @Override
    public void syncAnswerToDatabase() {
        Set<String> keys = redisTemplate.keys("answer:*");
        if(keys == null){
            return;
        }
        for (String key : keys) {
            List<Object> objects = redisTemplate.opsForList().range(key, 0, -1);
            for (Object object : objects) {
                Answer answer = (Answer) object;
                Integer answerId = answer.getId();
                if(answerMapper.getAnswerById(answerId)!=null){
                    answerMapper.updateAnswer(answer);
                }else{
                    answerMapper.addAnswer(answer);
                }
                redisTemplate.delete(key);
            }
        }
    }

    @Override
    public Answer updateAnswer(Map<String, Object> map) {
        try{
            Integer answerId = (Integer) map.get("id");
            String title = (String) map.get("title");
            String content = (String) map.get("content");
            String summary = ContentUtil.getSummaryFromContent(content);
            String category = (String) map.get("category");
            String tags = (String) map.get("tags");

            Answer answer = new Answer();
            if(answerMapper.getAnswerById(answerId)==null){
                throw new GlobalException("回答不存在", Result.FAILED);
            }
            answer.setTitle(title);
            answer.setContent(content);
            answer.setSummary(summary);
            answer.setCategory(category);
            answer.setTags(tags);

            answer.setUpdateTimeStamp(System.currentTimeMillis());

            //更新Redis中的数据
            String key = "answer:" + answer.getQuestionId() + ":" + answerId;
            redisTemplate.opsForList().leftPush(key, answer);
            return answer;
        }catch (Exception e){
            throw new GlobalException("更新回答失败", Result.FAILED);
        }
    }

    @Override
    public void deleteAnswer(int answerId, int authorId) {
        try{
            if(answerMapper.getAnswerById(answerId) == null){
                throw new GlobalException("回答不存在", Result.FAILED);
            }
            Answer answer = answerMapper.getAnswerById(answerId);
            if(answer.getAuthorId() != authorId){
                throw new GlobalException("无权删除回答", Result.FAILED);
            }
            answerMapper.deleteAnswer(answerId);
            //先查找Redis中是否存在该回答
            String key = "answer:" + answer.getQuestionId() + ":" + answerId;
            if(redisTemplate.opsForList().size(key) != null){
                redisTemplate.delete(key);
            }
            //再查找数据库中是否存在该回答
            if(answerMapper.getAnswerById(answerId)!=null){
                answerMapper.deleteAnswer(answerId);
            }
        }catch (Exception e){
            throw new GlobalException("删除回答失败", Result.FAILED);
        }
    }

    private List<Answer> getAnswerFromRedis(int questionId, int pageNum, int pageSize) {
        Set<String> keys = redisTemplate.keys("answer:" + questionId + ":*");
        List<Answer> answers = new ArrayList<>();
        for (String key : keys) {
            int start = (pageNum - 1) * pageSize;
            int end = pageNum * pageSize - 1;
            List<Object> objects= redisTemplate.opsForList().range(key, start, end);
            for (Object object : objects) {
                answers.add((Answer) object);
            }
        }
        return answers;
    }


}
