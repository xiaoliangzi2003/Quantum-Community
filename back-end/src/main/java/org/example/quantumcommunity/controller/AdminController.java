package org.example.quantumcommunity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.example.quantumcommunity.mapper.UserMapper;
import org.example.quantumcommunity.model.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiaol
 * @description: 管理员控制器
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserMapper userMapper;

    /**
     * @return Map<String,Object>:userInfo 用户信息
     * @description: 获取所有用户信息
     * @path: /admin/getAllUser
     * @method: GET
     * */
    @GetMapping("/getAllUser")
    public Map<String,Object> getAllUser(){
        Map<String,Object> userInfo = new HashMap<>();
        List<User> userList = userMapper.getAllUser();
        userInfo.put("userList", userList);
        return userInfo;
    }
}
