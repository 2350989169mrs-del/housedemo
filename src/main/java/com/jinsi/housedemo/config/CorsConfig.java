package com.jinsi.housedemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig  implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                // 允许跨域的路径（/**表示所有路径）
                .addMapping("/**")
                // 允许的前端源（生产环境建议指定具体域名，如"http://www.xxx.com"）
                .allowedOrigins("*")
                // 允许的请求方法（GET/POST/PUT/DELETE等）
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                // 允许的请求头（*表示所有）
                .allowedHeaders("*")
                // 预检请求缓存时间（秒）
                .maxAge(3600);
    }
}
