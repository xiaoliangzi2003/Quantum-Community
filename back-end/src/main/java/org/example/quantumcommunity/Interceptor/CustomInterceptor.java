package org.example.quantumcommunity.Interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

/**
 * @author xiaol
 * @description: 自定义拦截器
 */
@Slf4j
public class CustomInterceptor implements HandlerInterceptor {
    /**
     * @param: request 请求
     * @param: response 响应
     * @param: handler 处理器
     * @return boolean:是否放行
     * @description: 拦截请求
     * */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        获取请求头中的cookie中的token
//        if(request.getHeader("cookie")==null||!request.getHeader("cookie").contains("token=")){
//            response.sendRedirect("/auth/login");
//            return false;
//        }
//        String cookie= request.getHeader("cookie");
//        String[] parts= cookie.split("token=");
//        String token = parts[1];
//        if(token!=null) {
//            try {
//                Claims claims = JwtUtils.parseToken(token);
//                Date expiration = claims.getExpiration();
//                Date now = new Date();
//                if (now.after(expiration)) {
//                    return false;
//                } else {
//                    String username = (String) claims.get("username");
//                    return username != null;
//                }
//            } catch (ExpiredJwtException | SignatureException e) {
//                response.sendRedirect("/auth/login");
//                return false;
//            }
//        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
