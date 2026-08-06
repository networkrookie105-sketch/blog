package com.rookie.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.rookie.common.constant.CacheConstants;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 本地缓存配置（Caffeine + Spring Cache 抽象）。
 * <p>
 * 缓存分类/标签列表、博客设置等"读多写极少"的热点数据，命中后直接走内存，
 * 显著降低数据库压力；对应的写操作通过 {@code @CacheEvict} 主动失效。
 *
 * @author kiro
 * @ClassName CacheConfig
 * @date 2026年08月05日
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                CacheConstants.CATEGORY_LIST,
                CacheConstants.TAG_LIST,
                CacheConstants.BLOG_SETTINGS
        );
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 写入后 30 分钟过期，作为兜底，避免脏数据长期驻留
                .expireAfterWrite(30, TimeUnit.MINUTES)
                // 单个缓存最大条目数
                .maximumSize(1000)
                // 记录命中率等统计信息，便于后续观测
                .recordStats());
        // 未预先声明的缓存名不自动创建，避免拼写错误导致的"隐形"缓存
        cacheManager.setAllowNullValues(false);
        return cacheManager;
    }
}
