package com.jinsi.housedemo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRE = 7 * 24 * 60 * 60 * 1000L; // 7天

    // 生成 Token（把 userId 和 userType 包进去）
    public static String generateToken(Integer userId, Integer userType) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("userType", userType)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(KEY)
                .compact();
    }

    // 从 Token 里取出 userId
    public static Integer getUserId(String token) {
        return getClaims(token).get("userId", Integer.class);
    }

    // 从 Token 里取出 userType
    public static Integer getUserType(String token) {
        return getClaims(token).get("userType", Integer.class);
    }

    // 解析 Token 的通用方法
    private static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
