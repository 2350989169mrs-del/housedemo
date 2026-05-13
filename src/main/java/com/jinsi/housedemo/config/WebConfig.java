package com.jinsi.housedemo.config;

import com.jinsi.housedemo.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtInterceptor())
                .addPathPatterns("/api/**")              // 拦截所有 /api/ 请求
                .excludePathPatterns(                    // 这些路径不拦截
                        "/api/user/login",
                        "/api/user/register"
                );
    }

    // 内部类：JWT 拦截器
    static class JwtInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Object handler) {
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                Integer userId = JwtUtil.getUserId(token);
                Integer userType = JwtUtil.getUserType(token);
                request.setAttribute("userId", userId);
                request.setAttribute("userType", userType);
                return true; // 放行
            }
            throw new RuntimeException("请先登录");
        }
    }
}
