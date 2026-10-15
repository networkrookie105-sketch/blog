package com.rookie.admin.controller;

import com.rookie.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import com.rookie.admin.service.AdminUserService;
import com.rookie.common.aspect.ApiOperationLog;
import com.rookie.common.utils.Response;
import com.rookie.jwt.utils.TokenBlacklist;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author peishunwu
 * @ClassName AdminUserController
 * @description: TODO
 * @date 2024年04月10日
 * @version: 1.0
 */
@RestController
@RequestMapping("/admin")
@Tag(name = "管理员用户模块")
public class AdminUserController {
    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private TokenBlacklist tokenBlacklist;

    @Value("${jwt.tokenHeaderKey}")
    private String tokenHeaderKey;

    @Value("${jwt.tokenPrefix}")
    private String tokenPrefix;

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    @ApiOperationLog(description = "退出登录")
    public Response logout(HttpServletRequest request) {
        String header = request.getHeader(tokenHeaderKey);
        // 将当前 token 加入黑名单，使其立即失效
        if (StringUtils.isNotBlank(header) && header.startsWith(tokenPrefix)) {
            String token = header.substring(tokenPrefix.length()).trim();
            if (StringUtils.isNotBlank(token)) {
                tokenBlacklist.add(token);
            }
        }
        return Response.success();
    }

    @PostMapping("/password/update")
    @Operation(summary = "更新管理员用户密码")
    @ApiOperationLog(description = "修改用户密码")
    public Response updatePassword(@RequestBody @Validated UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO) {
        return adminUserService.updatePassword(updateAdminUserPasswordReqVO);
    }

    @GetMapping("/user/info")
    @Operation(summary = "获取用户信息")
    @ApiOperationLog(description = "获取用户信息")
    public Response findUserInfo() {
        return adminUserService.findUserInfo();
    }

}
