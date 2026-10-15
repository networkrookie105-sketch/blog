package com.rookie.jwt.handler;

import com.rookie.common.enums.ResponseCodeEnum;
import com.rookie.common.utils.Response;
import com.rookie.jwt.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author peishunwu
 * @ClassName RestAccessDeniedHandler
 * @description: 处理当用户登录成功时，访问受保护的资源，但是权限不够的情况
 * @date 2024年01月26日
 * @version: 1.0
 */
@Slf4j
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.warn("登录成功访问受保护的资源，但是权限不够: ", accessDeniedException);
        // 预留，后面引入多角色时会用到
        ResultUtil.fail(response, Response.fail(ResponseCodeEnum.FORBIDDEN));
    }
}
