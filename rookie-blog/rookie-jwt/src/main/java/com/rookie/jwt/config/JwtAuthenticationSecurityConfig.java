package com.rookie.jwt.config;

import com.rookie.jwt.filter.JwtAuthenticationFilter;
import com.rookie.jwt.handler.RestAuthenticationFailureHandler;
import com.rookie.jwt.handler.RestAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author peishunwu
 * @ClassName JwtAuthenticationSecurityConfig
 * @description: JWT 认证相关的配置
 * @date 2023年12月26日
 * @version: 1.0
 */
@Configuration
public class JwtAuthenticationSecurityConfig extends AbstractHttpConfigurer<JwtAuthenticationSecurityConfig, HttpSecurity> {
    @Autowired
    private RestAuthenticationSuccessHandler restAuthenticationSuccessHandler;
    @Autowired
    private RestAuthenticationFailureHandler restAuthenticationFailureHandler;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public void init(HttpSecurity httpSecurity) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        httpSecurity.authenticationProvider(provider);
    }

    @Override
    public void configure(HttpSecurity httpSecurity) {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter();
        filter.setAuthenticationManager(httpSecurity.getSharedObject(AuthenticationManager.class));
        filter.setAuthenticationSuccessHandler(restAuthenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(restAuthenticationFailureHandler);
        httpSecurity.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
