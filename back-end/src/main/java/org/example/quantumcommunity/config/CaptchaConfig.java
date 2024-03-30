package org.example.quantumcommunity.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Properties;

/**
 * @description: 验证码配置类
 * @author xiaol
 */
@Configuration
public class CaptchaConfig {

    /**
     * @return DefaultKaptcha
     * @description: 默认生成图形验证码
     * */
    @Bean
    //生成验证码
    public DefaultKaptcha defaultKaptcha(){
        DefaultKaptcha defaultKaptcha=new DefaultKaptcha();
        Properties properties=new Properties();
        //是否有边框
        properties.setProperty("kaptcha.border","no");

        //验证码文本字符颜色
        properties.setProperty("kaptcha.textproducer.font.color","black");

        //验证码图片宽度
        properties.setProperty("kaptcha.image.width","150");

        //验证码图片高度
        properties.setProperty("kaptcha.image.height","50");

        //验证码文本字符大小
        properties.setProperty("kaptcha.textproducer.font.size","40");

        //验证码Session key
        properties.setProperty("kaptcha.session.key","code");

        //验证码文本字符长度
        properties.setProperty("kaptcha.textproducer.char.length","4");

        //验证码文本字体样式
        properties.setProperty("kaptcha.textproducer.font.names","宋体,Arial,Courier");

        //图像样式
        properties.setProperty("kaptcha.obscurificator.impl","com.google.code.kaptcha.impl.WaterRipple");

        //干扰线颜色
        properties.setProperty("kaptcha.noise.color","black");

        //干扰实现类
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.DefaultNoise");


        Config config=new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }

    /**
     * @return DefaultKaptcha
     * @description: 生成算术验证码
     * */
    public DefaultKaptcha getKaptchaMath(){
        DefaultKaptcha defaultKaptcha=new DefaultKaptcha();

        Properties properties=new Properties();

        //是否有边框
        properties.setProperty("kaptcha.border","no");

        //验证码文本字符颜色
        properties.setProperty("kaptcha.textproducer.font.color","black");

        //验证码图片宽度
        properties.setProperty("kaptcha.image.width","150");

        //验证码图片高度
        properties.setProperty("kaptcha.image.height","50");

        //验证码文本字符大小
        properties.setProperty("kaptcha.textproducer.font.size","40");

        //验证码Session key
        properties.setProperty("kaptcha.session.key","MATH_CODE");

        //验证码文本生成器
        properties.setProperty("kaptcha.textproducer.impl","com.google.code.kaptcha.text.impl.MathExpressionProducer");

        //验证码文本字符长度
        properties.setProperty("kaptcha.textproducer.char.length","6");

        //验证码文本字体样式
        properties.setProperty("kaptcha.textproducer.font.names","宋体,Arial,Courier");

        //图像样式
        properties.setProperty("kaptcha.obscurificator.impl","com.google.code.kaptcha.impl.WaterRipple");

        //干扰线颜色
        properties.setProperty("kaptcha.noise.color","black");

        //干扰实现类
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.DefaultNoise");

        Config config=new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}
