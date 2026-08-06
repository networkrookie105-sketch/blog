package com.rookie.jwt.filter;

import com.rookie.jwt.utils.JwtTokenHelper;
import com.rookie.jwt.utils.TokenBlacklist;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author dreame
 * @ClassName TokenAuthenticationFilter
 * @description: 定义了一个 Spring Security 过滤器 TokenAuthenticationFilter，它继承了 OncePerRequestFilter，
 * 确保每个请求只被过滤一次。在重写的 doFilterInternal() 方法中来定义过滤器处理逻辑，
 * 首先，从请求头中获取 key 为 Authorization 的值，判断是否以 Bearer 开头，
 * 若是，截取出 Token, 对其进行解析，并对可能抛出的异常做出不同的返参。
 * 最后，我们获取了用户详情信息，将用户信息存入 authentication，方便后续进行校验，
 * 同时将 authentication 存入 ThreadLocal 中，方便后面方便的获取用户信息。
 * @date 2024年01月19日
 * @version: 1.0
 */
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private TokenBlacklist tokenBlacklist;

    @Value("${jwt.tokenPrefix}")
    private String tokenPrefix;

    @Value("${jwt.tokenHeaderKey}")
    private String tokenHeaderKey;

    private static final List<String> docUrls = Arrays.asList("/login","/doc.html", "/webjars","/favicon.ico","/v2/api-docs","/swagger-resources");
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 检查请求的 URL，如果是 knife4j 的接口文档的 URL，那么跳过 token 校验
        boolean startWith = docUrls.stream().anyMatch(request.getRequestURI()::startsWith);
        if (startWith){
            filterChain.doFilter(request, response);
        }else {
            // 从请求头中获取 key 为 Authorization 的值
            String header = request.getHeader(tokenHeaderKey);
            // 如果请求头中的 Authorization 值为空或者不是以 Bearer 开头，则直接放行
            if (header != null) {
                if (header.startsWith(tokenPrefix)){
                    // 如果请求头中有 token，则进行解析
                    String token = header.substring(7);
                    log.info("token: {}", token);
                    if (StringUtils.isNotBlank(token)) {
                        try {
                            // 校验 token 是否有效
                            jwtTokenHelper.validateToken(token);
                        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
                            // 抛出异常，统一让 AuthenticationEntryPoint 处理响应参数
                            authenticationEntryPoint.commence(request, response, new AuthenticationServiceException("Token 不可用"));
                            return;
                        } catch (ExpiredJwtException e) {
                            authenticationEntryPoint.commence(request, response, new AuthenticationServiceException("Token 已失效"));
                            return;
                        }
                        // 校验 token 是否已被登出（黑名单）
                        if (tokenBlacklist.contains(token)) {
                            authenticationEntryPoint.commence(request, response, new AuthenticationServiceException("Token 已失效，请重新登录"));
                            return;
                        }
                        // 通过 token 获取用户名
                        String tokenUsername = jwtTokenHelper.getUsernameByToken(token);
                        if (StringUtils.isNotBlank(tokenUsername) && SecurityContextHolder.getContext().getAuthentication() == null) {
                            // 通过用户名加载用户信息
                            UserDetails userDetails = userDetailsService.loadUserByUsername(tokenUsername);
                            // 校验 token 是否有效，重新构造 UsernamePasswordAuthenticationToken 对象，设置到 Spring 安全上下文中
                            //将用户信息存入 authentication，方便后续校验
                            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            // 将 authentication 存入 ThreadLocal，方便后续获取用户信息
                            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        }
                    }
                }
            }
            //继续执行写一个过滤器
            filterChain.doFilter(request, response);
        }
    }
}
