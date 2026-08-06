package com.rookie.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * 全局跨域配置（前后端分离必需）。
 * <p>
 * 通过提供 {@link org.springframework.web.cors.CorsConfigurationSource} Bean，
 * 同时被 Spring MVC 与 Spring Security 过滤器链复用。
 *
 * @author kiro
 * @ClassName CorsConfig
 * @date 2026年08月05日
 */
@Configuration
public class CorsConfig {

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许携带凭证（Cookie / Authorization 头）
        configuration.setAllowCredentials(true);
        // 允许的来源，使用 allowedOriginPatterns 以兼容 allowCredentials=true 的场景（生产可收窄为具体域名）
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        // 允许的请求方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        // 允许的请求头
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        // 暴露给前端的响应头
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Disposition"));
        // 预检请求缓存时间（秒）
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
