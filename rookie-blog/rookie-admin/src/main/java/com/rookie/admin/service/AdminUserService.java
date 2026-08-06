package com.rookie.admin.service;

import com.rookie.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import com.rookie.common.utils.Response;

/**
 * @author peishunwu
 * @ClassName AdminUserService
 * @description: TODO
 * @date 2024年04月10日
 * @version: 1.0
 */
public interface AdminUserService {
    /**
     * 更新密码
     * @param updateAdminUserPasswordReqVO
     * @return
     */
    Response updatePassword(UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO);

    Response findUserInfo();
}
