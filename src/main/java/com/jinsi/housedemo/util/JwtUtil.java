package com.jinsi.housedemo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类 —— 负责 Token 的生成与解析
 * <p>
 * 密钥从 application.yml 的 jwt.secret 读取，
 * 服务重启后 Token 仍然有效（只要密钥不变）。
 * </p>
 *
 * <pre>
 * 使用方式（静态调用）：
 *   String token = JwtUtil.generateToken(userId, userType);
 *   Integer userId = JwtUtil.getUserId(token);
 * </pre>
 */
@Component // 交给 Spring 管理，以便读取 @Value 配置
public class JwtUtil {

    /** JWT 签名密钥（从配置文件注入，不再是每次重启随机生成） */
    private static SecretKey KEY;

    /** Token 过期时间：7 天（毫秒） */
    private static final long EXPIRE = 7L * 24 * 60 * 60 * 1000;

    // ==================== 配置注入 ====================

    /**
     * Spring 启动时自动调用，将 yml 中的 jwt.secret 转为 HMAC-SHA256 密钥。
     * <p>
     * 注意：密钥长度必须 ≥ 256 bits（32 字节），否则 HS256 会报错。
     * 建议使用 64 位以上的十六进制字符串。
     * </p>
     *
     * @param secret 来自 application.yml 的 jwt.secret 值
     */
    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        // 将配置的字符串密钥转为 HMAC-SHA256 需要的 Key 对象
        KEY = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // ==================== Token 生成 ====================

    /**
     * 生成 JWT Token，将 userId 和 userType 写入载荷（Payload）
     *
     * @param userId   用户 ID
     * @param userType 用户类型：1=普通用户 2=会员 3=经纪人 4=管理员 5=超级管理员
     * @return JWT 字符串（前端存 localStorage，每次请求带在 Authorization 头）
     */
    public static String generateToken(Integer userId, Integer userType) {
        return Jwts.builder()
                .claim("userId", userId)       // 自定义载荷：用户 ID
                .claim("userType", userType)   // 自定义载荷：用户类型
                .setIssuedAt(new Date())       // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE)) // 过期时间
                .signWith(KEY)                 // 签名
                .compact();                    // 生成字符串
    }

    // ==================== Token 解析 ====================

    /**
     * 从 Token 中取出用户 ID
     *
     * @param token JWT 字符串
     * @return 用户 ID，解析失败返回 null
     */
    public static Integer getUserId(String token) {
        try {
            return getClaims(token).get("userId", Integer.class);
        } catch (Exception e) {
            return null; // Token 无效或过期时返回 null
        }
    }

    /**
     * 从 Token 中取出用户类型
     *
     * @param token JWT 字符串
     * @return 用户类型（1~5），解析失败返回 null
     */
    public static Integer getUserType(String token) {
        try {
            return getClaims(token).get("userType", Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    // ==================== 内部工具方法 ====================

    /**
     * 解析 Token 的通用方法（私有，仅供内部调用）
     *
     * @param token JWT 字符串
     * @return Claims 对象（包含所有载荷数据）
     */
    private static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
