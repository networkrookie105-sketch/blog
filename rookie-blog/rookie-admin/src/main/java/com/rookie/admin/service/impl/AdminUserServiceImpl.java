package com.rookie.admin.service.impl;

import com.rookie.admin.model.vo.user.FindUserInfoRspVO;
import com.rookie.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import com.rookie.admin.service.AdminUserService;
import com.rookie.common.domain.mapper.UserMapper;
import com.rookie.common.enums.ResponseCodeEnum;
import com.rookie.common.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author peishunwu
 * @ClassName AdminUserServiceImpl
 * @description: TODO
 * @date 2024年04月10日
 * @version: 1.0
 */
@Service
public class AdminUserServiceImpl implements AdminUserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public Response updatePassword(UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO) {
        // 获取用户名和密码
        String username = updateAdminUserPasswordReqVO.getUsername();
        String password = updateAdminUserPasswordReqVO.getPassword();
        // 对密码进行加密
        String encodePassword = passwordEncoder.encode(password);
        // 更新密码
        int count = userMapper.updatePasswordByUsername(username, encodePassword);
        return count == 1 ? Response.success() : Response.fail(ResponseCodeEnum.USERNAME_NOT_FOUND);
    }

    @Override
    public Response findUserInfo() {
        //获取存储在ThreadLocal中的用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //获取用户名
        String username = authentication.getName();
        return Response.success(FindUserInfoRspVO.builder().username(username).build());
    }
}
