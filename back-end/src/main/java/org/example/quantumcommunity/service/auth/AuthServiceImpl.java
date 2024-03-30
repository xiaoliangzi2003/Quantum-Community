package org.example.quantumcommunity.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.example.quantumcommunity.exception.GlobalException;
import org.example.quantumcommunity.mapper.UserMapper;
import org.example.quantumcommunity.mapper.UserProfileMapper;
import org.example.quantumcommunity.model.User;
import org.example.quantumcommunity.service.rules.RulesService;
import org.example.quantumcommunity.util.security.AuthUtil;
import org.example.quantumcommunity.util.security.JwtUtil;
import org.example.quantumcommunity.util.security.PasswordEncryptor;
import org.example.quantumcommunity.util.security.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    UserProfileMapper userProfileMapper;

    @Autowired
    RulesService rulesService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private AuthUtil authUtil;
    private static final int INTERVAL = 5;
    private static final int MAX_FAILED_ATTEMPTS = 5;

    private RedisAtomicInteger getRedisCounter(String key) {
        RedisAtomicInteger counter =
                new RedisAtomicInteger(key, Objects.requireNonNull(stringRedisTemplate.getConnectionFactory()));
        if (counter.get() == 0) {
            counter.expireAt(new Date(System.currentTimeMillis() + INTERVAL * 60 * 1000));
        }
        return counter;
    }

    @Override
    public Map<String, Object> loginByAccountAndPassword(Map<String,Object> authRequest) {
        Map<String,Object> result=new HashMap<>();

        //获取登录的账户
        String account= (String) authRequest.get("account");
        String username=authUtil.authRequestGetUsername(account);
        String password= (String) authRequest.get("password");

        //获取redis中的登录失败次数
        String key = "loginFailed:" + username;
        RedisAtomicInteger failedAttempts = getRedisCounter(key);

        //如果失败次数超过5次，锁定5min
        if (failedAttempts.get() >= MAX_FAILED_ATTEMPTS) {
            throw new GlobalException("登录失败次数超过5次，锁定5min", Result.TOO_MANY_REQUEST);
        }

        //获取redis中的验证码
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String captcha = operations.get("captcha");
        String code= (String) authRequest.get("captcha");
        if (!code.equals(captcha)) {
            throw new GlobalException("验证码错误", Result.UNAUTHORIZED);
        }

        //用户被封禁
        if ("ban".equals(userMapper.getStatus(username))) {
            failedAttempts.incrementAndGet();
            throw new GlobalException("用户已被封禁", Result.FORBIDDEN);
        }

        //获取数据库中的密码
        User user = new User();
        rulesService.checkIsRightPassword(username, password);

        //登录成功,生成token
        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("token", JwtUtil.generateToken(user.getUsername(), password));
        log.info(response.get("username") + "登录成功");

        //设置用户当前的状态为在线
        userMapper.updateStatus(user.getUsername(), "online");

        //更新用户的登录时间
        long loginTimeMillis = System.currentTimeMillis();
        Timestamp loginTimeStamp = new Timestamp(loginTimeMillis);
        userMapper.updateLoginTime(user.getUsername(), loginTimeStamp);
        return response;
    }


    @Override
    public Map<String, Object> registerByEmail(Map<String,Object> authRequest) {
        try {

            String username = (String) authRequest.get("username");
            String password = (String) authRequest.get("password");
            String email = (String) authRequest.get("email");
            String code = (String) authRequest.get("code");
            log.info("当前验证码："+code);

            //从redis中获取验证码
            String correctCode=stringRedisTemplate.opsForValue().get(email);
            log.info("正确验证码："+correctCode);
            if(!code.equals(correctCode)){
                throw new GlobalException("验证码错误", Result.UNAUTHORIZED);
            }

            String registerType="email";
            rulesService.usernameRule(username);
            rulesService.passwordRule(password);
            rulesService.emailRule(email);
            rulesService.checkExistRule(username, email,null,registerType);

            //注册部分的逻辑
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);

            //数据库中的密码应该加密存储
            String encryptedPassword = PasswordEncryptor.encrypt(password);
            user.setPassword(encryptedPassword);


            userMapper.insertUser(user);
            //注册后直接生成token
            Map<String, Object> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("token", JwtUtil.generateToken(user.getUsername(), user.getPassword()));

            userProfileMapper.insertUsername(username);

            log.info(response.get("username") + "注册成功");
            return response;

        } catch (GlobalException e) {
            throw new GlobalException(e.getMessage(), e.getStatusCode());
        }

    }

    @Override
    public void logout(String username) {
        //设置用户当前的状态为离线
        userMapper.updateStatus(username, "offline");
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        try{
            //检查旧密码是否正确
            rulesService.checkIsRightPassword(username, oldPassword);
            rulesService.passwordRule(newPassword);
            //如果新密码和旧密码相同
            if (oldPassword.equals(newPassword)) {
                throw new GlobalException("新密码不能和旧密码相同",Result.BAD_REQUEST);
            }
            //更新密码
            userMapper.updatePassword(username, oldPassword, newPassword);
        }catch (GlobalException e){
            throw new GlobalException(e.getMessage(), e.getStatusCode());
        }
    }



    @Override
    public void deleteUser(String username,String password) {
        try{
            //检查密码是否正确
            rulesService.checkIsRightPassword(username, password);
            //获取用户ID
            int id = userMapper.getUserByUsername(username).getId();
            //删除用户
            userMapper.deleteUser(id);
        }catch (GlobalException e){
            throw new GlobalException(e.getMessage(), e.getStatusCode());
        }
    }

    @Override
    public Map<String, Object> loginByEmailCode(Map<String,Object> authRequest) {

        String email = (String) authRequest.get("account");
        String code = (String) authRequest.get("code");

        //如果邮箱不存在
        if (userMapper.getUserByEmail(email) == null) {
            throw new GlobalException("邮箱不存在", Result.NOT_FOUND);
        }

        //从redis中获取验证码并进行比对
        String correctCode = stringRedisTemplate.opsForValue().get(email);
        if (!code.equals(correctCode)) {
            throw new GlobalException("验证码错误", Result.UNAUTHORIZED);
        }

        //登录成功，生成token
        String username = authUtil.authRequestGetUsername(email);
        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("token", JwtUtil.generateToken(username, code));

        //设置用户当前的状态为在线
        userMapper.updateStatus(username, "online");

        //更新用户的登录时间
        long loginTimeMillis = System.currentTimeMillis();
        Timestamp loginTimeStamp = new Timestamp(loginTimeMillis);
        userMapper.updateLoginTime(username, loginTimeStamp);

        return response;
    }

    @Override
    public Map<String, Object> loginByPhoneCode(Map<String,Object> authRequest) {
        return null;
    }

    @Override
    public Map<String, Object> loginByWechatCode(Map<String,Object> authRequest) {
        return null;
    }

    @Override
    public Map<String, Object> loginByGithub(Map<String,Object> authRequest) {
        return null;
    }

    @Override
    public Map<String, Object> registerByPhone(Map<String,Object> authRequest) {
        return null;
    }

    @Override
    public void resetPasswordByEmail(String username,String password) {
        try{
            rulesService.passwordRule(password);
            //更新密码
            userMapper.updatePassword(username,password,password);
        }catch (GlobalException e){
            throw new GlobalException(e.getMessage(), e.getStatusCode());
        }
    }

    @Override
    public void resetPasswordByPhone(String username,String password) {
        try{
            rulesService.passwordRule(password);
            //更新密码
            userMapper.updatePassword(username,password,password);
        }catch (GlobalException e){
            throw new GlobalException(e.getMessage(), e.getStatusCode());
        }
    }

    @Override
    public void resetPasswordByWechat(String username,String password) {
        try{
            rulesService.passwordRule(password);
            //更新密码
            userMapper.updatePassword(username,password,password);
        }catch (GlobalException e){
            throw new GlobalException(e.getMessage(), e.getStatusCode());
        }
    }

    @Override
    public void bindEmail(String username, String password, String email,String code) {
        //如果绑定的邮箱已经被其他用户绑定
        if(!Objects.equals(userMapper.getUserByEmail(email).getUsername(), username)){
            throw new GlobalException("邮箱已被其他用户绑定", Result.BAD_REQUEST);
        }
        if(email.equals(userMapper.getUserByUsername(username).getEmail())){
            throw new GlobalException("邮箱已被绑定", Result.BAD_REQUEST);
        }
        rulesService.checkIsRightPassword(username, password);
        //校验验证码
        String correctCode = stringRedisTemplate.opsForValue().get(email);
        if (!code.equals(correctCode)) {
            throw new GlobalException("验证码错误", Result.UNAUTHORIZED);
        }
        //更新邮箱
        userMapper.updateEmail(username, password, email);
    }

    @Override
    public void bindPhone(String username, String password, String phone,String code) {
        //如果绑定的手机号已经被其他用户绑定
        if(!Objects.equals(userMapper.getUserByPhone(phone).getUsername(), username)){
            throw new GlobalException("手机号已被其他用户绑定", Result.BAD_REQUEST);
        }
        if(phone.equals(userMapper.getUserByUsername(username).getPhone())){
            throw new GlobalException("手机号已被绑定", Result.BAD_REQUEST);
        }
        rulesService.checkIsRightPassword(username, password);
        //校验验证码
        String correctCode = stringRedisTemplate.opsForValue().get(phone);
        if (!code.equals(correctCode)) {
            throw new GlobalException("验证码错误", Result.UNAUTHORIZED);
        }
        //更新手机号
        userMapper.updatePhone(username, password, phone);
    }

    @Override
    public void bindWechat(String username, String password, String wechatCode) {

    }

    @Override
    public void bindGithub(String username, String password, String githubCode) {

    }

    @Override
    public void unbindEmail(String username, String password, String email,String code) {
        rulesService.checkIsRightPassword(username, password);
        //校验验证码
        String correctCode = stringRedisTemplate.opsForValue().get(email);
        if (!code.equals(correctCode)) {
            throw new GlobalException("验证码错误", Result.UNAUTHORIZED);
        }
        //如果只剩下一个绑定，不允许解绑
        if (rulesService.checkAtLeastOneBinding(username)) {
            userMapper.updateEmail(username, password, null);
        }else {
            throw new GlobalException("至少绑定了手机或邮箱其中一个", Result.BAD_REQUEST);
        }
    }

    @Override
    public void unbindPhone(String username, String password, String phone,String code) {
        rulesService.checkIsRightPassword(username, password);
        //校验验证码
        String correctCode = stringRedisTemplate.opsForValue().get(phone);
        if (!code.equals(correctCode)) {
            throw new GlobalException("验证码错误", Result.UNAUTHORIZED);
        }
        //如果只剩下一个绑定，不允许解绑
        if (rulesService.checkAtLeastOneBinding(username)) {
            userMapper.updatePhone(username, password, null);
        }else {
            userMapper.updatePhone(username, password, null);
        }
    }

    @Override
    public void unbindWechat(String username, String password, String wechatCode) {

    }

    @Override
    public void unbindGithub(String username, String password, String githubCode) {

    }
}