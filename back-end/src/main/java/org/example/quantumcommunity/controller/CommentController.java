package org.example.quantumcommunity.controller;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.example.quantumcommunity.service.comment.CommentService;
import org.example.quantumcommunity.util.security.Result;
import org.example.quantumcommunity.service.article.ArticleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaol
 */
@RestController
@Slf4j
@RequestMapping("/comment")
public class CommentController {
//    private final RateLimiter rateLimiter = RateLimiter.create(3000, 1, TimeUnit.SECONDS);


    @Autowired
    private CommentService commentService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    @Qualifier("taskExecutor")
    private TaskExecutor taskExecutor;

    @PostMapping("/add-comment")
    public Result publishComment(@RequestBody Map<String, Object> comment){
        try{
            // 限流：尝试获取许可，超时时间为10秒
//            rateLimiter.acquire(10);
            //异步处理：将评论存入消息队列
            try {
                //异步处理：将评论存入消息队列
                taskExecutor.execute(() -> commentService.sendCommentToQueue(comment));
                return new Result(Result.OK, "评论已提交，正在处理中", null);
            } catch (Exception e) {
                return new Result(Result.ERROR, e.getMessage(), null);
            }
        } catch (Exception e) {
            // 如果线程在等待许可时被中断，返回错误
            return new Result(Result.ERROR, "系统繁忙，请稍后再试", null);
        }
    }


    @PostMapping("/get-comment-list")
    public Result getCommentListByPage(@RequestBody Map<String, Object> request){
        try{
            int pageNum = (int) request.get("pageNum");
            int pageSize = (int) request.get("pageSize");
            String decodeTitle = URLDecoder.decode((String) request.get("title"), StandardCharsets.UTF_8);
            String author = (String) request.get("author");
            String username = (String) request.get("username");
            int articleId = articleService.getArticleByTitleAndAuthor(decodeTitle, author).getId();
            return new Result(Result.OK, "获取评论列表成功",
                    commentService.getCommentMapByPage(pageNum, pageSize, articleId,username));
        }catch (Exception e){
            return new Result(Result.ERROR, e.getMessage(), null);
        }
    }

    @PostMapping("/load-more-sub-comments")
    public Result loadMoreSubComments(@RequestBody Map<String, Object> request){
        try{
            int pageNum = (int) request.get("pageNum");
            int pageSize = (int) request.get("pageSize");
            int commentId = (int) request.get("commentId");
            int articleId = (int) request.get("articleId");
            return new Result(Result.OK, "获取二级评论成功",
                    commentService.getSubCommentListByPage(pageNum, pageSize,commentId,articleId));
        }catch (Exception e){
            return new Result(Result.ERROR, e.getMessage(), null);
        }
    }

    @DeleteMapping("/delete-comment")
    public Result deleteComment(@RequestBody Map<String, Object> request){
        try{
            int commentId = (int) request.get("commentId");
            commentService.deleteComment(commentId);
            return new Result(Result.OK, "删除评论成功", null);
        }catch (Exception e){
            return new Result(Result.ERROR, e.getMessage(), null);
        }
    }

    @PostMapping("/like-comment")
    public Result likeComment(@RequestBody Map<String, Object> request){
        try{
            //commentator是点赞的用户，commentId是被点赞的评论
            int commentId = (int) request.get("commentId");
            String commentator = (String) request.get("commentator");
            int flag = (int) request.get("flag");
            commentService.likeComment(commentId, commentator,flag);
            return new Result(Result.OK, "点赞成功", null);
        }catch (Exception e){
            return new Result(Result.ERROR, e.getMessage(), null);
        }
    }

    @PostMapping("/unlike-comment")
    public Result unlikeComment(@RequestBody Map<String, Object> request){
        try{
            //commentator是取消点赞的用户，commentId是被取消点赞的评论
            int commentId = (int) request.get("commentId");
            String commentator = (String) request.get("commentator");
            commentService.unlikeComment(commentId, commentator);
            return new Result(Result.OK, "取消点赞成功", null);
        }catch (Exception e){
            return new Result(Result.ERROR, e.getMessage(), null);
        }
    }
}
