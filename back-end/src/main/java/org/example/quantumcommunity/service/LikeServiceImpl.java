package org.example.quantumcommunity.service;

import lombok.extern.slf4j.Slf4j;
import org.example.quantumcommunity.mapper.ArticleMapper;
import org.example.quantumcommunity.mapper.CommentMapper;
import org.example.quantumcommunity.mapper.LikeRecordMapper;
import org.example.quantumcommunity.mapper.UserMapper;
import org.example.quantumcommunity.model.LikeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author xiaol
 */
@Service
@Slf4j
public class LikeServiceImpl implements LikeService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private LikeRecordMapper likeRecordMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private CommentMapper commentMapper;



    @Override
    public boolean hasLikedInRedis(String username, Integer articleOrCommentId, Integer type) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember("like:" + type + ":" + articleOrCommentId, username));
    }

    @Override
    public boolean hasLikedInDatabase(String username, Integer articleOrCommentId,Integer type) {
        Integer userId = userMapper.getUserByUsername(username).getId();
        LikeRecord likeRecord = likeRecordMapper.findUserLikeArticleInDb(userId, articleOrCommentId, type);
        return likeRecord != null;
    }

    @Override
    public boolean hasUnlikedInRedis(String username, Integer articleOrCommentId, Integer type) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember("unlike:" + type + ":" + articleOrCommentId, username));
    }

    @Override
    public void addLikeInRedis(String username, Integer articleOrCommentId, Integer type) {
        //如果Redis中存在该用户的取消点赞记录，则移除取消点赞记录
        if (hasUnlikedInRedis(username, articleOrCommentId, type)) {
            redisTemplate.opsForSet().remove("unlike:" + type + ":" + articleOrCommentId, username);
            //点赞数+1
            redisTemplate.opsForValue().increment("like:count:" + type + ":" + articleOrCommentId);
            return;
        }
        //如果Redis中不存在并且数据库中不存在该用户的点赞记录，则添加点赞记录到Redis
        if (!hasLikedInRedis(username, articleOrCommentId, type) && !hasLikedInDatabase(username, articleOrCommentId, type)) {
            redisTemplate.opsForSet().add("like:" + type + ":" + articleOrCommentId, username);
        }

        //如果Redis中不存在这篇文章的点赞数记录，则添加点赞数记录到Redis
        if (redisTemplate.opsForValue().get("like:count:" + type + ":" + articleOrCommentId) == null) {
            //文章点赞
            if(type==0){
                long likesCount = articleMapper.getLikesCount(articleOrCommentId);
                redisTemplate.opsForValue().set("like:count:" + type + ":" + articleOrCommentId, likesCount);
            }else{
                //评论点赞
                long likesCount = commentMapper.getLikesCount(articleOrCommentId);
                redisTemplate.opsForValue().set("like:count:" + type + ":" + articleOrCommentId, likesCount);
            }
        }
        incrementLikeCountInRedis(articleOrCommentId, type);
    }

    @Override
    @Transactional
    public void addLikeInDatabase(String username, Integer articleOrCommentId, Integer type) {
        //如果数据库中不存在该用户的点赞记录，则添加点赞记录到数据库
        if (!hasLikedInDatabase(username, articleOrCommentId, type)) {
            Integer userId = userMapper.getUserByUsername(username).getId();
            LikeRecord likeRecord = new LikeRecord();
            likeRecord.setUserId(Long.valueOf(userId));
            likeRecord.setArticleOrCommentId(Long.valueOf(articleOrCommentId));
            likeRecord.setType(type);
            likeRecord.setInstrumentTime(System.currentTimeMillis());
            likeRecordMapper.insertLikeRecord(likeRecord);
        }else{
            //如果数据库中存在该用户的点赞记录，则检查版本号是否一致
            LikeRecord dbLikeRecord = likeRecordMapper.findUserLikeArticleInDb(userMapper.getUserByUsername(username).getId(), articleOrCommentId, type);
            if(!Objects.equals(dbLikeRecord.getVersion(), likeRecordMapper.findUserLikeArticleInDb(userMapper.getUserByUsername(username).getId(), articleOrCommentId, type).getVersion())){
                throw new OptimisticLockingFailureException("LikeRecord has been updated by other transaction");
            }
        }
    }

    @Override
    public void addUnlikeInRedis(String username, Integer articleOrCommentId, Integer type) {
        //如果Redis中存在点赞记录，则移除点赞记录
        if (hasLikedInRedis(username, articleOrCommentId, type)) {
            removeLikeInRedis(username, articleOrCommentId, type);
            //点赞数-1
            decrementLikeCountInRedis(articleOrCommentId, type);
            return;
        }
        //如果Redis中不存在并且数据库中不存在该用户的取消点赞记录，则添加取消点赞记录到Redis
        if (!hasUnlikedInRedis(username, articleOrCommentId, type)) {
            redisTemplate.opsForSet().add("unlike:" + type + ":" + articleOrCommentId, username);
        }
        //如果Redis中不存在这篇文章的点赞数记录，则添加点赞数记录到Redis
        if (redisTemplate.opsForValue().get("like:count:" + type + ":" + articleOrCommentId) == null) {
            //文章点赞
            if(type==0){
                long likesCount = articleMapper.getLikesCount(articleOrCommentId);
                redisTemplate.opsForValue().set("like:count:" + type + ":" + articleOrCommentId, likesCount);
            }else{
                //评论点赞
                long likesCount = commentMapper.getLikesCount(articleOrCommentId);
                redisTemplate.opsForValue().set("like:count:" + type + ":" + articleOrCommentId, likesCount);
            }
        }
        //如果Redis中不存在该用户的取消点赞记录，则减少点赞数
        decrementLikeCountInRedis(articleOrCommentId, type);
    }

    @Override
    public void removeLikeInRedis(String username, Integer articleOrCommentId, Integer type) {
        //如果Redis中存在该用户的点赞记录，则移除点赞记录
        if (hasLikedInRedis(username, articleOrCommentId, type)) {
            redisTemplate.opsForSet().remove("like:" + type + ":" + articleOrCommentId, username);
        }
    }

    @Override
    @Transactional
    public void removeLikeInDatabase(String username, Integer articleOrCommentId, Integer type) {
        Integer userId = userMapper.getUserByUsername(username).getId();
        LikeRecord likeRecord = likeRecordMapper.findUserLikeArticleInDb(userId, articleOrCommentId, type);
        if (likeRecord != null) {
            LikeRecord dbLikeRecord = likeRecordMapper.findUserLikeArticleInDb(userId,articleOrCommentId, type);
            if (!Objects.equals(dbLikeRecord.getVersion(), likeRecord.getVersion())) {
                throw new OptimisticLockingFailureException("LikeRecord has been updated by other transaction");
            }
            likeRecordMapper.deleteUserLikeArticleInDb(userId, articleOrCommentId, type);
        }
    }

    @Override
    public long getLikeCountFromDb(Integer articleOrCommentId, Integer type) {
        if(type==0){
            return articleMapper.getLikesCount(articleOrCommentId);
        }else{
            return commentMapper.getLikesCount(articleOrCommentId);
        }
    }

    @Override
    public void incrementLikeCountInRedis(Integer articleOrCommentId, Integer type) {
        redisTemplate.opsForValue().increment("like:count:" + type + ":" + articleOrCommentId);
    }

    @Override
    public void decrementLikeCountInRedis(Integer articleOrCommentId, Integer type) {
        redisTemplate.opsForValue().decrement("like:count:" + type + ":" + articleOrCommentId);
    }

    //定时任务,每s同步一次点赞记录到数据库
    @Scheduled(cron = "0/1 * * * * ?")
    public void syncLikeCountToDb(){
        //同步文章的的点赞数到数据库
        Objects.requireNonNull(redisTemplate.keys("like:count:0:*")).forEach(key -> {
            String[] split = key.split(":");
            Integer articleOrCommentId = Integer.valueOf(split[3]);
            Integer likesCount = (Integer) redisTemplate.opsForValue().get(key);
            articleMapper.updateLikesCount(articleOrCommentId, likesCount);
            log.info("sync like count to db: articleId={}, likesCount={}", articleOrCommentId, likesCount);
            //删除记录
            redisTemplate.delete(key);

        });

        //同步评论的的点赞数到数据库
        Objects.requireNonNull(redisTemplate.keys("like:count:1:*")).forEach(key -> {
            String[] split = key.split(":");
            Integer commentId = Integer.valueOf(split[3]);
            Integer likesCount = (Integer) redisTemplate.opsForValue().get(key);
            commentMapper.updateLikesCount(commentId, likesCount);
            //删除记录
            redisTemplate.delete(key);
        });


        //同步点赞记录到数据库
        Objects.requireNonNull(redisTemplate.keys("like:0:*")).forEach(key -> {
            String[] split = key.split(":");
            Integer articleOrCommentId = Integer.valueOf(split[2]);
            redisTemplate.opsForSet().members(key).forEach(username -> {
                addLikeInDatabase((String) username, articleOrCommentId, 0);
                //删除记录
                redisTemplate.opsForSet().remove(key, username);
                log.info("sync like record to db: articleOrCommentId={}, username={}", articleOrCommentId, username);
            });
        });

        //同步点赞记录到数据库
        Objects.requireNonNull(redisTemplate.keys("like:1:*")).forEach(key -> {
            String[] split = key.split(":");
            Integer articleOrCommentId = Integer.valueOf(split[2]);
            redisTemplate.opsForSet().members(key).forEach(username -> {
                addLikeInDatabase((String) username, articleOrCommentId, 1);
                //删除记录
                redisTemplate.opsForSet().remove(key, username);
                log.info("sync like record to db: articleOrCommentId={}, username={}", articleOrCommentId, username);
            });
        });


        //从数据库中查找需要删除的点赞记录
        Objects.requireNonNull(redisTemplate.keys("unlike:0:*")).forEach(key -> {
            String[] split = key.split(":");
            Integer articleOrCommentId = Integer.valueOf(split[2]);
            redisTemplate.opsForSet().members(key).forEach(username -> {
                removeLikeInDatabase((String) username, articleOrCommentId, 0);
                //删除记录
                redisTemplate.opsForSet().remove(key, username);
                log.info("sync unlike record to db: articleOrCommentId={}, username={}", articleOrCommentId, username);
            });
        });

        //从数据库中查找需要删除的点赞记录
        Objects.requireNonNull(redisTemplate.keys("unlike:1:*")).forEach(key -> {
            String[] split = key.split(":");
            Integer articleOrCommentId = Integer.valueOf(split[2]);
            redisTemplate.opsForSet().members(key).forEach(username -> {
                removeLikeInDatabase((String) username, articleOrCommentId, 1);
                //删除记录
                redisTemplate.opsForSet().remove(key, username);
                log.info("sync unlike record to db: articleOrCommentId={}, username={}", articleOrCommentId, username);
            });
        });
    }
}
