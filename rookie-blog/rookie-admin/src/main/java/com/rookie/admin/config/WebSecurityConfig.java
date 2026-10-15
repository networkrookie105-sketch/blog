package com.rookie.admin.config;

import com.rookie.jwt.config.JwtAuthenticationSecurityConfig;
import com.rookie.jwt.filter.TokenAuthenticationFilter;
import com.rookie.jwt.handler.RestAccessDeniedHandler;
import com.rookie.jwt.handler.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {
    @Autowired
    private JwtAuthenticationSecurityConfig jwtAuthenticationSecurityConfig;
    @Autowired
    private RestAuthenticationEntryPoint authEntryPoint;
    @Autowired
    private RestAccessDeniedHandler deniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .with(jwtAuthenticationSecurityConfig, Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/admin/user/info", "/admin/logout", "/admin/password/update").authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll())
                .httpBasic(basic -> basic.authenticationEntryPoint(authEntryPoint))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(deniedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }

    // Token 过滤器仅在 Security 链内运行，避免 Boot 将其再次注册为 Servlet 过滤器。
    @Bean
    public FilterRegistrationBean<TokenAuthenticationFilter> tokenFilterRegistration(TokenAuthenticationFilter filter) {
        FilterRegistrationBean<TokenAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
