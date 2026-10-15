package com.rookie.jwt.handler;

import com.rookie.common.enums.ResponseCodeEnum;
import com.rookie.common.utils.Response;
import com.rookie.jwt.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author peishunwu
 * @ClassName RestAuthenticationEntryPoint
 * @description: 处理当用户未登录时，访问受保护的资源的情况
 * @date 2024年01月26日
 * @version: 1.0
 */
@Slf4j
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.warn("用户未登录访问受保护的资源: ", authException);
        if (authException instanceof InsufficientAuthenticationException) {
            // 用户未登录
            ResultUtil.fail(response, HttpStatus.UNAUTHORIZED.value(), Response.fail(ResponseCodeEnum.UNAUTHORIZED));
        }
        ResultUtil.fail(response, HttpStatus.UNAUTHORIZED.value(), Response.fail(authException.getMessage()));
    }
}
