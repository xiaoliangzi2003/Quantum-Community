package org.example.quantumcommunity.controller;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import lombok.extern.slf4j.Slf4j;
import org.example.quantumcommunity.exception.GlobalException;
import org.example.quantumcommunity.mapper.UserMapper;
import org.example.quantumcommunity.model.User;
import org.example.quantumcommunity.service.auth.AuthService;
import org.example.quantumcommunity.util.generator.CodeGenerator;
import org.example.quantumcommunity.util.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaol
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    AuthService authService;

    @Autowired
    private Producer captchaProducer;

    @Autowired
    private  Producer captchaMathProducer;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private LoginFailedUtil loginFailedUtil;

    /**
     * @param authRequest 用户、密码、验证码
     * @return Result 登录结果
     * @description: 登录功能
     * @path: /auth/login
     * @method: POST
     * */
    @PostMapping("/login")
    public Result loginByAccount(@RequestBody Map<String,Object> authRequest){
        try{
            Map<String,Object> response;
            response=authService.loginByAccountAndPassword(authRequest);
            return new Result(Result.OK,"登录成功",response);
        }catch (Exception e){
            return new Result(Result.UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @param authRequest 邮箱、密码、验证码
     * @return Result 登录结果
     * @description: 邮箱验证码登录功能
     * @path: /auth/loginByEmailCode
     * @method: POST
     * */
    @PostMapping("/login-by-email-code")
    public Result loginByEmailCode(@RequestBody Map<String,Object> authRequest){
        try{
            Map<String,Object> response;
            response=authService.loginByEmailCode(authRequest);
            return new Result(Result.OK,"登录成功",response);
        }catch (Exception e){
            return new Result(Result.UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @param authRequest 手机号、密码、验证码
     * @return Result 登录结果
     * @description: 手机验证码登录功能
     * @path: /auth/loginByPhoneCode
     * @method: POST
     * */
    @PostMapping("/login-by-phone-code")
    public Result loginByPhoneCode(@RequestBody Map<String,Object> authRequest){
        try{
            Map<String,Object> response;
            response=authService.loginByPhoneCode(authRequest);
            return new Result(Result.OK,"登录成功",response);
        }catch (Exception e){
            return new Result(Result.UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @param authRequest 授权码
     * @return Result 登录结果
     * */
    @PostMapping("/login-by-wechat-code")
    public Result loginByWechatCode(@RequestBody Map<String,Object> authRequest){
        try{
            Map<String,Object> response;
            response=authService.loginByWechatCode(authRequest);
            return new Result(Result.OK,"登录成功",response);
        }catch (Exception e){
            return new Result(Result.UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @param authRequest 授权码
     * @return Result 登录结果
     * @description: Github登录功能
     * @path: /auth/loginByGithub
     * @method: POST
     * */
    @PostMapping("/login-by-github")
    public Result loginByGithub(@RequestBody Map<String,Object> authRequest){
        try{
            Map<String,Object> response;
            response=authService.loginByGithub(authRequest);
            return new Result(Result.OK,"登录成功",response);
        }catch (Exception e){
            return new Result(Result.UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @param authRequest 用户名、密码、邮箱、验证码
     * @return Result 注册结果
     * @description: 邮箱注册功能
     * @path: /auth/registerByEmail
     * @method: POST
     * */
    @PostMapping("/register-by-email")
    public Result registerByEmail(@RequestBody Map<String,Object> authRequest){
        try{
            Map<String,Object> response;
            response=authService.registerByEmail(authRequest);
            return new Result(Result.OK,"注册成功",response);
        }catch(Exception e){
            return new Result(Result.REGISTER_FAILED,e.getMessage(),null);
        }
    }

    /**
     * @param authRequest 用户名、密码、手机号、验证码
     * @return Result 注册结果
     * @description: 手机注册功能
     * @path: /auth/registerByPhone
     * @method: POST
     * */
    @PostMapping("/register-by-phone")
    public Result registerByPhone(@RequestBody Map<String,Object> authRequest){
        try{
            Map<String,Object> response;
            response=authService.registerByPhone(authRequest);
            return new Result(Result.OK,"注册成功",response);
        }catch(Exception e){
            return new Result(Result.REGISTER_FAILED,e.getMessage(),"尝试次数："+loginFailedUtil.getFailedAttempts(authRequest.get("username").toString()));
        }
    }

    /**
     * @param request 发送到的目标邮箱、主题、验证码
     * @return Result 发送邮件结果
     * @description: 发送邮件
     * @path: /auth/sendEmail
     * @method: POST
     * */
    @PostMapping("/send-email")
    public Result sendEmail(@RequestBody Map<String,String> request){
        try{
            String to=request.get("to");
            String code= CodeGenerator.generateRandomCode();
            String subject=request.get("subject");
            EmailUtil emailService=new EmailUtil(emailSender);
            stringRedisTemplate.opsForValue().set(to,code,3, TimeUnit.MINUTES);
            emailService.sendEmail(to,subject,code);
            return new Result(Result.OK,"发送邮件成功",null);
        }catch (Exception e){
            return new Result(Result.UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @param request 发送到的目标手机号 主题 验证码
     * @return Result 发送短信结果
     * @description: 发送手机短信
     * @path: /auth/sendPhoneCode
     * @method: POST
     * */
    @PostMapping("/send-phone-code")
    public Result sendPhoneCode(@RequestBody Map<String,String> request){
        try{
            String to=request.get("to");
            String code= CodeGenerator.generateRandomCode();
            //Todo:发送手机短信的业务逻辑
            stringRedisTemplate.opsForValue().set(to,code,3, TimeUnit.MINUTES);
            return new Result(Result.OK,"发送短信成功",null);
        }catch (Exception e){
            return new Result(Result.UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @param request 用户名、邮箱、旧密码、新密码、邮箱验证码
     * @return Result 修改密码结果
     * @description: 通过邮箱修改密码
     * @path: /auth/changePassword
     * @method: PUT
     * */
    @PutMapping("/change-password-by-email")
    public Result changePasswordByEmail(@RequestBody Map<String,String> request){
        try{
            String username=request.get("username");
            String email=request.get("email");
            String oldPassword=request.get("oldPassword");
            String newPassword=request.get("newPassword");
            String code=request.get("code");

            //校验邮箱和用户名是否匹配
            User user=new User();
            user=userMapper.getUserByEmail(email);
            if(!Objects.equals(user.getUsername(),username)){
                throw new GlobalException("用户名和邮箱不匹配",Result.UNAUTHORIZED);
            }

            //校验验证码
            if(!Objects.equals(stringRedisTemplate.opsForValue().get(email), code)){
                throw new GlobalException("验证码错误",Result.UNAUTHORIZED);
            }
            authService.changePassword(username,oldPassword,newPassword);
            return new Result(Result.OK,"修改密码成功",null);
        }catch (Exception e){
            return new Result(Result.UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @param request 用户名、手机号码、密码、新密码、验证码
     * @return Result 修改密码结果
     * @description: 通过手机修改密码
     * @path: /auth/changePasswordByPhone
     * @method: PUT
     * */
    @PutMapping("/change-password-by-phone")
    public Result changePasswordByPhone(@RequestBody Map<String,String> request){
        try{
            String username=request.get("username");
            String phone=request.get("phone");
            String oldPassword=request.get("oldPassword");
            String newPassword=request.get("newPassword");
            String code=request.get("code");

            //校验邮箱和用户名是否匹配
            User user=new User();
            user=userMapper.getUserByEmail(phone);
            if(!Objects.equals(user.getUsername(),username)){
                throw new GlobalException("用户名和邮箱不匹配",Result.UNAUTHORIZED);
            }

            //校验验证码
            if(!Objects.equals(stringRedisTemplate.opsForValue().get(phone), code)){
                throw new GlobalException("验证码错误",Result.UNAUTHORIZED);
            }

            authService.changePassword(username,oldPassword,newPassword);
            return new Result(Result.OK,"修改密码成功",null);

        }catch (Exception e){
            return new Result(Result.UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @param request 用户名、微信授权码、密码、新密码、验证码
     * @return Result 修改密码结果
     * @description: 通过微信修改密码
     * @path: /auth/changePasswordByWechat
     * @method: PUT
     * */
    @PutMapping("/change-password-by-wechat")
    public Result changePasswordByWechat(@RequestBody Map<String,String> request){
        try{
            String username=request.get("username");
            String wechatCode=request.get("wechatCode");
            String oldPassword=request.get("oldPassword");
            String newPassword=request.get("newPassword");
            String code=request.get("code");

            //校验验证码
            if(!Objects.equals(stringRedisTemplate.opsForValue().get(wechatCode), code)){
                throw new GlobalException("验证码错误",Result.UNAUTHORIZED);
            }

            authService.changePassword(username,oldPassword,newPassword);
            return new Result(Result.OK,"修改密码成功",null);
        }catch (Exception e){
            return new Result(Result.UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @param request 用户名、密码、邮箱、验证码
     * @return Result 找回密码结果
     * @description: 通过邮箱找回密码
     * */
    @PutMapping("/reset-password-by-email")
    public Result resetPasswordByEmail(@RequestBody Map<String,String> request){
        try{
            String username=request.get("username");
            String password=request.get("password");
            String email=request.get("email");
            String code=request.get("code");

            //校验邮箱和用户名是否匹配
            User user=new User();
            user=userMapper.getUserByEmail(email);
            if(!Objects.equals(user.getUsername(),username)){
                throw new GlobalException("用户名和邮箱不匹配",Result.UNAUTHORIZED);
            }

            //校验验证码
            if(!Objects.equals(stringRedisTemplate.opsForValue().get(email), code)){
                throw new GlobalException("验证码错误",Result.UNAUTHORIZED);
            }

            authService.resetPasswordByEmail(username,password);
            return new Result(Result.OK,"找回密码成功",null);
        }catch (Exception e){
            return new Result(Result.UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @param request 用户名、密码、手机号、验证码
     * @return Result 找回密码结果
     * @description: 通过手机找回密码
     * @path: /auth/resetPasswordByPhone
     * @method: PUT
     * */
    @PutMapping("/reset-password-by-phone")
    public Result resetPasswordByPhone(@RequestBody Map<String,String> request){
        try{
            String username=request.get("username");
            String password=request.get("password");
            String phone=request.get("phone");
            String code=request.get("code");

            //校验手机和用户名是否匹配
            User user=new User();
            user=userMapper.getUserByPhone(phone);
            if(!Objects.equals(user.getUsername(),username)){
                throw new GlobalException("用户名和手机号不匹配",Result.UNAUTHORIZED);
            }

            //校验验证码
            if(!Objects.equals(stringRedisTemplate.opsForValue().get(phone), code)){
                throw new GlobalException("验证码错误",Result.UNAUTHORIZED);
            }

            authService.resetPasswordByPhone(username,password);
            return new Result(Result.OK,"找回密码成功",null);
        }catch (Exception e){
            return new Result(Result.UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @param request 用户名、密码、微信授权码、新密码
     * @return Result 找回密码结果
     * @description: 通过微信找回密码
     * @path: /auth/resetPasswordByWechat
     * */
    @PutMapping("/reset-password-by-wechat")
    public Result resetPasswordByWechat(@RequestBody Map<String,String> request){
        try{
            String username=request.get("username");
            String password=request.get("password");
            String wechatCode=request.get("wechatCode");
            String code=request.get("code");

            //校验验证码
            if(!Objects.equals(stringRedisTemplate.opsForValue().get(wechatCode), code)){
                throw new GlobalException("验证码错误",Result.UNAUTHORIZED);
            }

            authService.resetPasswordByWechat(username,password);
            return new Result(Result.OK,"找回密码成功",null);
        }catch (Exception e){
            return new Result(Result.UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @param username 用户名
     * @return Result 登出结果
     * @description: 登出
     * @path: /auth/logout
     * @method: POST
     * */
    @PostMapping("/logout")
    public Result logout(@RequestBody String username){
        authService.logout(username);
        return new Result(Result.OK,"登出成功",null);
    }

    /**
     * @param request 用户名、密码
     * @return Result 删除账号结果
     * @description: 删除账号
     * @path: /auth/deleteUser
     * @method: DELETE
     * */
    @DeleteMapping("/delete-user")
    public Result deleteUser(@RequestBody Map<String,String> request){
        try{
            String username=request.get("username");
            String password=request.get("password");
            authService.deleteUser(username,password);
            return new Result(Result.OK,"删除账号成功",null);
        }catch (Exception e){
            return new Result(Result.UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @param request 用户名、密码、邮箱、验证码
     * @return Result 绑定邮箱结果
     * @description: 绑定或修改绑定的邮箱
     * @path: /auth/bindEmail
     * @method: POST
     * */
    @PostMapping("/bind-email")
    public Result bindEmail(@RequestBody Map<String,String> request){
        try{
            String username=request.get("username");
            String password=request.get("password");
            String email=request.get("email");
            String code=request.get("code");

            authService.bindEmail(username,password,email,code);
            return new Result(Result.OK,"绑定邮箱成功",null);
        }catch (Exception e){
            return new Result(Result.UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @param request 用户名、密码、手机号、验证码
     * @return Result 绑定手机号结果
     * @description: 绑定或修改绑定的手机号
     * @path: /auth/bindPhone
     * @method: POST
     * */
    @PostMapping("/bind-phone")
    public Result bindPhone(@RequestBody Map<String,String> request){
        try{
            String username=request.get("username");
            String password=request.get("password");
            String phone=request.get("phone");
            String code=request.get("code");
            authService.bindPhone(username,password,phone,code);
            return new Result(Result.OK,"绑定手机号成功",null);
        }catch (Exception e){
            return new Result(Result.UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @param request 用户名、密码、微信授权码
     * @return Result 绑定微信结果
     * @description: 绑定或修改绑定的微信
     * @path: /auth/bindWechat
     * @method: POST
     * */
    @PostMapping("/bind-wechat")
    public Result bindWechat(@RequestBody Map<String,String> request){
        try{
            authService.bindWechat(request.get("username"),request.get("password"),request.get("wechatCode"));
            return new Result(Result.OK,"绑定微信成功",null);
        }catch (Exception e){
            return new Result(Result.UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @param request 用户名、密码、Github授权码
     * @return Result 绑定Github结果
     * @description: 绑定或修改绑定的Github
     * @path: /auth/bindGithub
     * @method: POST
     * */
    @PostMapping("/bind-github")
    public Result bindGithub(@RequestBody Map<String,String> request){
        try{
            authService.bindGithub(request.get("username"),request.get("password"),request.get("githubCode"));
            return new Result(Result.OK,"绑定Github成功",null);
        }catch (Exception e){
            return new Result(Result.UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @return byte[] 验证码图片
     * @description: 获取验证码图片
     * @path: /auth/captcha
     * @method: GET
     * */
    @GetMapping("/captcha")
    @ResponseBody
    public byte[] captcha() throws IOException {

        // 随机选择验证码类型
        DefaultKaptcha producer = (DefaultKaptcha) (new Random().nextBoolean() ? captchaProducer : captchaMathProducer);

        // 生成验证码
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);

        // 将验证码存储在 Redis 中，并设置过期时间为 1 分钟
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();

        ops.set("captcha", text, 1, TimeUnit.MINUTES);

        // 将验证码图片转换为 Base64 编码的字符串
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);

        return outputStream.toByteArray();
    }

    /**
     * @param request token
     * @return Result 验证token结果
     * @description: 验证token
     * @path: /auth/validateToken
     * @method: POST
     * */
    @PostMapping("/validate-token")
    public Result validateToken(@RequestBody Map<String,String> request){
        try{
            String token=request.get("token");
            String username= JwtUtil.parseToken(token).get("username").toString();
            return new Result(Result.OK,"验证成功",username);
        }catch (Exception e){
            return new Result(Result.UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @param request 用户名、密码、邮箱、验证码
     * @return Result 解绑邮箱结果
     * @description: 解绑邮箱
     * @path: /auth/unbindEmail
     * @method: POST
     * */
    @PostMapping("/unbind-email")
    public Result unbindEmail(@RequestBody Map<String,String> request){
        try{
            String username=request.get("username");
            String password=request.get("password");
            String email=request.get("email");
            String code=request.get("code");

            authService.unbindEmail(username,password,email,code);
            return new Result(Result.OK,"解绑邮箱成功",null);
        }catch (Exception e){
            return new Result(Result.UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @param request 用户名、密码、手机号、验证码
     * @return Result 解绑手机号结果
     * @description: 解绑手机号
     * @path: /auth/unbindPhone
     * @method: POST
     * */
    @PostMapping("/unbind-phone")
    public Result unbindPhone(@RequestBody Map<String,String> request){
        try{
            String username=request.get("username");
            String password=request.get("password");
            String phone=request.get("phone");
            String code=request.get("code");
            authService.unbindPhone(username,password,phone,code);
            return new Result(Result.OK,"解绑手机号成功",null);
        }catch (Exception e){
            return new Result(Result.UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @param request 用户名、密码、微信授权码
     * @return Result 解绑微信结果
     * @description: 解绑微信
     * @path: /auth/unbindWechat
     * @method: POST
     * */
    @PostMapping("/unbind-wechat")
    public Result unbindWechat(@RequestBody Map<String,String> request){
        try{
            String username=request.get("username");
            String password=request.get("password");
            String wechatCode=request.get("wechatCode");
            authService.unbindWechat(username,password,wechatCode);
            return new Result(Result.OK,"解绑微信成功",null);
        }catch (Exception e){
            return new Result(Result.UNAUTHORIZED,e.getMessage(),null);
        }
    }

    /**
     * @param request 用户名、密码、Github授权码
     * @return Result 解绑Github结果
     * @description: 解绑Github
     * @path: /auth/unbindGithub
     * @method: POST
     * */
    @PostMapping("/unbind-github")
    public Result unbindGithub(@RequestBody Map<String,String> request){
        try{
            String username=request.get("username");
            String password=request.get("password");
            String githubCode=request.get("githubCode");

            authService.unbindGithub(username,password,githubCode);
            return new Result(Result.OK,"解绑Github成功",null);
        }catch (Exception e){
            return new Result(Result.UNAUTHORIZED,e.getMessage(),null);
        }
    }
}
