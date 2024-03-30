package org.example.quantumcommunity.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.quantumcommunity.service.LikeService;
import org.example.quantumcommunity.service.auth.AuthService;
import org.example.quantumcommunity.util.security.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author xiaol
 */
@RequestMapping("/like")
@Slf4j
@RestController
public class LikeController {

    @Autowired
    LikeService likeService;

    @Autowired
    AuthService authService;

    @PostMapping("/user-has-liked")
    public Result userHasLiked(@RequestBody Map<String,Object> request) {
        try {
            String message;
            String username = (String) request.get("username");
            Integer articleId = (Integer) request.get("articleId");
            //判断用户是否已经点赞：数据库、缓存
            if(likeService.hasLikedInDatabase(username, articleId, 0)
                    || likeService.hasLikedInRedis(username, articleId, 0)) {
                //已经点赞
                message="1";
            }else {
                //未点赞
                message="0";
            }
            return new Result(200, message, "success");
        }catch (Exception e) {
            log.error("userHasLiked error", e);
            return new Result(500, "error", "error");
        }
    }
}
