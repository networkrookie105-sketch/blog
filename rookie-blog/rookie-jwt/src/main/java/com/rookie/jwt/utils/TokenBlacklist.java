package com.rookie.jwt.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * Token 黑名单（本地 Caffeine 实现）。
 * <p>
 * 用户登出或强制下线后，将其 Token 加入黑名单，{@code TokenAuthenticationFilter}
 * 校验时命中黑名单则拒绝访问。条目的存活时间与 Token 有效期一致，过期后自动清理，
 * 因此不会无限增长。
 *
 * @author kiro
 * @ClassName TokenBlacklist
 * @date 2026年08月05日
 */
@Component
public class TokenBlacklist {

    /**
     * Token 有效期（分钟），黑名单条目按此时长过期即可，无需保留更久
     */
    @Value("${jwt.tokenExpireTime}")
    private Long tokenExpireTime;

    private Cache<String, Boolean> cache;

    @PostConstruct
    public void init() {
        cache = Caffeine.newBuilder()
                .expireAfterWrite(tokenExpireTime, TimeUnit.MINUTES)
                .maximumSize(10_000)
                .build();
    }

    /**
     * 将 Token 加入黑名单
     * @param token 不含 Bearer 前缀的原始 token
     */
    public void add(String token) {
        cache.put(token, Boolean.TRUE);
    }

    /**
     * 判断 Token 是否已被列入黑名单
     * @param token 不含 Bearer 前缀的原始 token
     * @return true 表示已失效
     */
    public boolean contains(String token) {
        return cache.getIfPresent(token) != null;
    }
}
