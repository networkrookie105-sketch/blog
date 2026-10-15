package com.rookie.jwt.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

/**
 * @author peishunwu
 * @ClassName JwtTokenHelper
 * @description: TODO
 * @date 2023年12月19日
 * @version: 1.0
 */
@Component
public class JwtTokenHelper implements InitializingBean {
    /**
     * JWT 签发者
     */
    @Value("${jwt.issuer}")
    private String issuer;
    /**
     * JWT 秘钥
     */
    private Key key;
    /**
     * JWT 解析器
     */
    private JwtParser jwtParser;
    /**
     * 解码配置文件中配置的 Base 64 编码 key 为秘钥
     */
    @Value("${jwt.secret}")
    public void setBase64Key(String base64Key) {
        key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Key));
    }

    /**
     * Token 失效时间（分钟）
     */
    @Value("${jwt.tokenExpireTime}")
    private Long tokenExpireTime;
    /**
     * 初始化 JwtParser
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 考虑到不同服务器之间可能存在时钟偏移，setAllowedClockSkewSeconds 用于设置能够容忍的最大的时钟误差
        jwtParser = Jwts.parserBuilder().requireIssuer(issuer)
                .setSigningKey(key).setAllowedClockSkewSeconds(10)
                .build();
    }

    /**
     * 生成token
     * @param username
     * @return
     */
    public String generateToken(String username){
        LocalDateTime now = LocalDateTime.now();
        // 设置token过期时间为当前时间 + 30 分钟
        LocalDateTime expireTime = now.plusMinutes(tokenExpireTime);
        return Jwts.builder().setId(java.util.UUID.randomUUID().toString()).setSubject(username)
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(expireTime.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(key).compact();
    }

    /**
     * 解析 Token
     * @param token
     * @return
     */
    public Jws<Claims> parseToken(String token) {
        try {
            return jwtParser.parseClaimsJws(token);
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new BadCredentialsException("Token 不可用", e);
        } catch (ExpiredJwtException e) {
            throw new CredentialsExpiredException("Token 失效", e);
        }
    }

    /**
     * 解析 Token 获取用户名
     * @param token
     * @return
     */
    public String getUsernameByToken(String token) {
        try {
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            String username = claims.getSubject();
            return username;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成一个 Base64 的安全秘钥
     * @return
     */
    private static String generateBase64Key() {
        // 生成安全秘钥
        Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

        // 将密钥进行 Base64 编码
        String base64Key = Base64.getEncoder().encodeToString(secretKey.getEncoded());

        return base64Key;
    }
    /**
     * 校验 Token 是否可用
     * @param token
     * @return
     */
    public void validateToken(String token) {
        jwtParser.parseClaimsJws(token);
    }
    public static void main(String[] args) {
        String key = generateBase64Key();
        System.out.println("key: " + key);
    }
}
