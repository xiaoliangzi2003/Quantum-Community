package org.example.quantumcommunity.config;


import org.example.quantumcommunity.Interceptor.CustomInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author xiaol
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * @param: registry 视图控制器注册
     * @description: 添加视图控制器
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 登录页面
        registry.addViewController("/auth/login").setViewName("login");
        // 注册页面
        registry.addViewController("/auth/register").setViewName("register");
        // 首页
        registry.addViewController("/index").setViewName("index");
    }

    /**
     * @param: registry 拦截器注册
     * @description: 添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加拦截器
        registry.addInterceptor(new CustomInterceptor())
                // 拦截所有请求
                .addPathPatterns("/**",
                        "localhost:5173/**")
                // 放行登录和注册请求
                .excludePathPatterns(
                        "/auth/**",
                        "localhost:5173/login",
                        "localhost:5173/register",
                        "/css/**",
                        "/js/**",
                        "/image/**",
                        "templates/login.html",
                        "templates/register.html");
    }
}
