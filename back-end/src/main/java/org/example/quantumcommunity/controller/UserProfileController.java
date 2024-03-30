package org.example.quantumcommunity.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.quantumcommunity.mapper.UserMapper;
import org.example.quantumcommunity.model.User;
import org.example.quantumcommunity.model.UserProfile;
import org.example.quantumcommunity.service.file.FileService;
import org.example.quantumcommunity.util.other.TimeUtil;
import org.example.quantumcommunity.util.security.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.example.quantumcommunity.service.userprofile.UserProfileService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author xiaol
 */
@RestController
@Slf4j
@RequestMapping("/user-profile")
public class UserProfileController {
    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FileService fileService;

    @PostMapping("/get-user-profile")
    public Result getUserProfile(@RequestBody Map<String,Object> request){
        try{
            String username = (String) request.get("username");
            Map<String,Object> result = new HashMap<>();
            result.put("userProfile", userProfileService.getUserProfileByUsername(username));
            User user=userMapper.getUserByUsername(username);
            String email=user.getEmail();
            result.put("email",email);
            String phone=user.getPhone();
            result.put("phone",phone);
            return new Result(Result.OK, "获取用户信息成功", result);
        }catch (Exception e){
            return new Result(Result.ERROR, e.getMessage(), null);
        }
    }

    @PostMapping("/update-profile")
    public Result updateUserProfile(@RequestBody Map<String, Object> request) {
        try{
            String nickname = (String) request.get("nickname");
            String gender = (String) request.get("gender");
            Date birthday = new Date((long) request.get("birthday"));
            String signature = (String) request.get("signature");
            String wechat = (String) request.get("wechat");
            String github = (String) request.get("github");
            String location = (String) request.get("location");
            String username = (String) request.get("username");
            UserProfile userProfile=new UserProfile();
            userProfile.setUsername(username);
            userProfile.setNickname(nickname);
            userProfile.setGender(gender);

            //birthday:String to Date
            TimeUtil timeUtil = new TimeUtil();
            java.sql.Date timeStamp = timeUtil.simpleDateToSqlDate(birthday);
            userProfile.setBirthday(timeStamp);
            userProfile.setSignature(signature);
            userProfile.setWechat(wechat);
            userProfile.setGithub(github);
            userProfile.setLocation(location);
            userProfileService.updateUserProfile(userProfile);

            return new Result(Result.OK, "更新用户信息成功", null);
        }catch (Exception e){
            return new Result(Result.ERROR, e.getMessage(), null);
        }
    }

    @PostMapping("/upload-avatar")
    public Result uploadAvatar(@RequestBody MultipartFile file){
        try{
            String suffix= Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
            if(!".jpg".equals(suffix)&&!".png".equals(suffix)&&!".jpeg".equals(suffix)){
                return new Result(Result.ERROR, "上传头像失败，文件格式不正确", null);
            }
            //object name: username_avatar
            String objectName = file.getOriginalFilename();
            if(fileService.isFileExist(objectName)){
                fileService.deleteFile(objectName);
            }
            fileService.uploadFile(objectName, file.getInputStream());
            return new Result(Result.OK, "上传头像成功", null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
