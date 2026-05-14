package com.jinsi.housedemo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinsi.housedemo.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.Map;

/**
 * Web MVC 配置 —— 注册 JWT 登录拦截器
 * <p>
 * 拦截所有 /api/** 请求（除白名单），从 Authorization 头中取出 Token，
 * 解析出 userId 和 userType 放入 request.setAttribute，后续 Controller 通过
 * {@code @RequestAttribute} 即可获取当前登录用户信息。
 * </p>
 *
 * <pre>
 * 白名单（无需登录即可访问）：
 *   /api/user/login        —— 登录
 *   /api/user/register     —— 注册
 *   /api/house/list        —— 房源列表（公开浏览）
 *   /api/house/page        —— 房源分页（公开浏览）
 *   /api/house/detail/**   —— 房源详情
 *   /api/region/list       —— 区域列表（城市选择）
 *   /api/dict-data/batch   —— 字典数据（前端渲染用）
 * </pre>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtInterceptor())
                .addPathPatterns("/api/**")               // 拦截所有 /api/ 下的请求
                .excludePathPatterns(                     // === 白名单：以下路径不需要登录 ===
                        "/api/user/login",                // 登录
                        "/api/user/register",             // 注册
                        "/api/house/list",                // 房源列表（公开浏览）
                        "/api/house/page",                // 房源分页
                        "/api/house/detail/**",           // 房源详情（未登录也能看，但敏感字段为 null）
                        "/api/region/list",               // 区域列表（首页城市选择）
                        "/api/dict-data/batch"            // 字典数据批量获取
                );
    }

    // ==================== 内部类：JWT 拦截器 ====================

    /**
     * 每次请求到达 Controller 之前执行：
     * 1. 从请求头 Authorization 中提取 Bearer Token
     * 2. 解析 Token 得到 userId 和 userType
     * 3. 放入 request 属性，供后续 @RequestAttribute 使用
     * 4. 未登录时返回 401 JSON（不再抛异常）
     */
    static class JwtInterceptor implements HandlerInterceptor {

        /** Jackson 对象映射器（用于输出 JSON） */
        private static final ObjectMapper MAPPER = new ObjectMapper();

        @Override
        public boolean preHandle(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Object handler) throws Exception {
            // 1. 读取请求头 Authorization: Bearer <token>
            String header = request.getHeader("Authorization");

            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);          // 去掉 "Bearer " 前缀（7 个字符）
                Integer userId = JwtUtil.getUserId(token);
                Integer userType = JwtUtil.getUserType(token);

                if (userId != null && userType != null) {
                    // Token 有效，将用户信息放入 request 属性
                    request.setAttribute("userId", userId);
                    request.setAttribute("userType", userType);
                    return true; // 放行，继续执行 Controller
                }
            }

            // 2. Token 缺失或无效 → 返回 401 JSON（而非抛异常）
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.setContentType("application/json;charset=UTF-8");

            Map<String, Object> body = new HashMap<>();
            body.put("errorCode", 401);
            body.put("msg", "请先登录");
            body.put("result", null);

            response.getWriter().write(MAPPER.writeValueAsString(body));
            return false; // 拦截，不再执行 Controller
        }
    }
}
