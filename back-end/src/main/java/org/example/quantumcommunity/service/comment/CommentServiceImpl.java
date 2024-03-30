package org.example.quantumcommunity.service.comment;

import com.github.pagehelper.PageHelper;
import org.example.quantumcommunity.component.WebSocketServer;
import org.example.quantumcommunity.exception.GlobalException;
import org.example.quantumcommunity.mapper.CommentMapper;
import org.example.quantumcommunity.model.Comment;
import org.example.quantumcommunity.service.LikeService;
import org.example.quantumcommunity.service.article.ArticleService;
import org.example.quantumcommunity.util.processor.RandomIdProcessor;
import org.example.quantumcommunity.util.security.Result;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author xiaol
 */
@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ArticleService articleSerivce;

    @Autowired
    private WebSocketServer webSocketServer;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Autowired
    private LikeService likeService;

    private AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Comment addComment(Comment comment) {
        try{
            comment.setCreateTime(System.currentTimeMillis());
            RandomIdProcessor.process(comment);
            String key;
            if (comment.getParentCommentId() == 0) {
                // 一级评论
                key = "firstLevelComments:" + comment.getArticleId() + ":" + comment.getId();
            } else {
                // 二级评论
                key = "secondLevelComments:" + comment.getArticleId() + ":" + comment.getParentCommentId() + ":" + comment.getId();
            }
            redisTemplate.opsForList().rightPush(key, comment);
            //将redis中的评论通过websocket发送给用户
            webSocketServer.sendCommentToUser(comment,comment.getCommentator());
            return comment;
        }catch (Exception e) {
            throw new GlobalException(e.getMessage(), Result.ERROR);
        }
    }


    @Override
    public Map<Integer, Comment> getCommentMapByPage(int pageNum,
                                                     int pageSize,
                                                     int articleId,
                                                     String loginUsername) {
        try{
            if(pageNum<=0 || pageSize<=0){
                throw new GlobalException("参数错误", Result.ERROR);
            }
            List<Comment> commentList = new ArrayList<>();
            //从Redis中获取一级评论
            List<Comment> redisComments = getFirstLevelCommentsFromRedis(articleId, pageNum, pageSize);
            commentList.addAll(redisComments);

            // 如果Redis中的一级评论数量不足pageSize个，那么从数据库中获取剩余的一级评论
            if (commentList.size() < pageSize) {
                PageHelper.startPage(pageNum, pageSize - commentList.size());
                List<Comment> dbComments = commentMapper.getFirstLevelComment(articleId);
                commentList.addAll(dbComments);
            }

            Map<Integer, Comment> commentMap = new HashMap<>();

            for (Comment comment : commentList) {
                Boolean isLiked = likeService.hasLikedInDatabase(loginUsername, comment.getId(), 1);
                comment.setIsLiked(isLiked);
                // 从Redis中获取二级评论
                List<Comment> secondLevelCommentList = getSecondLevelCommentsFromRedis(articleId, comment.getId(), 1, 2);

                // 如果Redis中的二级评论数量不足，那么从数据库中获取剩余的二级评论
                if (secondLevelCommentList.size() < 2) {
                    PageHelper.startPage(1, 2 - secondLevelCommentList.size());
                    List<Comment> dbSecondLevelComments = commentMapper.getTopLikedSecondLevelComment(articleId, comment.getId(), 0, 2 - secondLevelCommentList.size());
                    secondLevelCommentList.addAll(dbSecondLevelComments);
                }
                for (Comment secondLevelComment : secondLevelCommentList) {
                    Boolean secondIsLiked = likeService.hasLikedInDatabase(loginUsername, secondLevelComment.getId(), 1);
                    secondLevelComment.setIsLiked(secondIsLiked);
                }

                comment.setSecondLevelComment(secondLevelCommentList);
                commentMap.put(comment.getId(), comment);
            }
            return commentMap;
        }catch (Exception e) {
            throw new GlobalException(e.getMessage(), Result.ERROR);
        }
    }

    private List<Comment> getFirstLevelCommentsFromRedis(int articleId, int pageNum, int pageSize) {
        // 获取所有的一级评论的键
        Set<String> keys = redisTemplate.keys("firstLevelComments:" + articleId + ":*");
        List<Comment> comments = new ArrayList<>();
        for (String key : keys) {
            // 计算开始和结束的索引
            int start = (pageNum - 1) * pageSize;
            int end = start + pageSize - 1;
            // 从Redis中获取评论
            List<Object> objects = redisTemplate.opsForList().range(key, start, end);
            // 将Object转换为Comment
            for (Object object : objects) {
                comments.add((Comment) object);
            }
        }
        return comments;
    }

    private List<Comment> getSecondLevelCommentsFromRedis(int articleId, Integer commentId, int pageNum, int pageSize) {
        // 获取所有的二级评论的键
        Set<String> keys = redisTemplate.keys("secondLevelComments:" + articleId + ":" + commentId + ":*");
        int start = (pageNum - 1) * pageSize;
        int end = start + pageSize - 1;
        keys = keys.stream().limit(end-start).collect(Collectors.toSet());
        List<Comment> comments = new ArrayList<>();
        for (String key : keys) {
            // 从Redis中获取评论
            List<Object> objects = redisTemplate.opsForList().range(key, start, end);
            // 将Object转换为Comment
            for (Object object : objects) {
                comments.add((Comment) object);
            }
        }
        return comments;
    }



    @Override
    public List<Comment> getSubCommentListByPage(int pageNum, int pageSize,int parentCommentId,int articleId){
        List<Comment> commentList = new ArrayList<>();
        // 从Redis中获取二级评论

        List<Comment> redisComments = getSecondLevelCommentsFromRedis(articleId, parentCommentId, pageNum, pageSize);
        commentList.addAll(redisComments);

        // 如果Redis中的二级评论数量不足pageSize个，那么从数据库中获取剩余的二级评论
        if (commentList.size() < pageSize) {
            PageHelper.startPage(pageNum, pageSize - commentList.size());
            List<Comment> dbComments = commentMapper.getSecondLevelComment(articleId, parentCommentId);
            commentList.addAll(dbComments);
        }
        return commentList;
    }

    @Override
    public void deleteComment(int commentId) {
        try{
            Comment comment = commentMapper.getCommentById(commentId);
            if(comment==null){
                throw new GlobalException("评论不存在", Result.ERROR);
            }
            commentMapper.deleteCommentById(commentId);
            articleSerivce.updateArticleComments(comment.getArticleId(), 1);
        }catch (Exception e) {
            throw new GlobalException(e.getMessage(), Result.ERROR);
        }
    }

    @Override
    public void likeComment(int commentId, String commentator,int flag) {
        if(flag==0){
            likeService.addLikeInRedis(commentator,commentId,1);
        }else{
            likeService.addUnlikeInRedis(commentator,commentId,1);
        }
    }

    @Override
    public void unlikeComment(int commentId, String commentator) {
        redisTemplate.opsForSet().remove("comment:"+commentId,commentator);
    }


    @Override
    public void sendCommentToQueue(Map<String,Object> comment) {
        String content = (String) comment.get("content");
        String commentator= (String) comment.get("commentator");
        Integer articleId = (Integer) comment.get("articleId");
        Integer parentCommentId = (Integer) comment.get("parentCommentId");
        String repliedContent = (String) comment.get("repliedContent");
        Integer repliedCommentId = (Integer) comment.get("repliedCommentId");
        String repliedCommentator = (String) comment.get("repliedCommentator");
        Comment newComment = new Comment();
        newComment.setContent(content);
        newComment.setParentCommentId(parentCommentId);
        newComment.setCommentator(commentator);
        newComment.setArticleId(articleId);
        newComment.setRepliedCommentId(repliedCommentId);
        newComment.setRepliedCommentator(repliedCommentator);
        newComment.setLikes(0);
        newComment.setRepliedContent(repliedContent);
        newComment.setCreateTime(System.currentTimeMillis());
        int queueNumber = counter.incrementAndGet() % 2 + 1;
        String queueName = "commentQueue" + queueNumber;
        rabbitTemplate.convertAndSend(queueName, newComment);
    }


    @Override
    public Comment getCommentById(Integer replyCommentId) {
        return commentMapper.getCommentById(replyCommentId);
    }

    @Scheduled(fixedRate = 60*60*1000)
    @Override
    public void syncCommentToDatabase(){
        Set<String> firstLevelCommentKeys = redisTemplate.keys("firstLevelComments:*");
        Set<String> secondLevelCommentKeys = redisTemplate.keys("secondLevelComments:*");

        Set<String> keys = new HashSet<>();
        keys.addAll(firstLevelCommentKeys);
        keys.addAll(secondLevelCommentKeys);
        for(String key:keys){
            List<Object> objectList = redisTemplate.opsForList().range(key, 0, -1);
            String[] parts = key.split(":");
            String commentId = parts[parts.length - 1];
            //如果id已存在，说明已经同步过了
            if(commentMapper.getCommentById(Integer.parseInt(commentId))!=null){
                redisTemplate.delete(key);
                continue;
            }
            List<Comment> commentList = new ArrayList<>();
            for(Object object : objectList){
                commentList.add((Comment) object);
            }
            for(Comment comment:commentList){
                commentMapper.addComment(comment);
                articleSerivce.updateArticleComments(comment.getArticleId(), 0);
            }
            //清除Redis中的评论
            redisTemplate.delete(key);
        }
    }
}
