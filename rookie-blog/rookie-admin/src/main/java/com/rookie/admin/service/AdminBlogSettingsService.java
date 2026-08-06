package com.rookie.admin.service;

import com.rookie.admin.model.vo.blogsettings.UpdateBlogSettingsReqVO;
import com.rookie.common.utils.Response;

/**
 * @author peishunwu
 * @ClassName AdminBlogSettingsService
 * @description: TODO
 * @date 2024年05月13日
 * @version: 1.0
 */
public interface AdminBlogSettingsService {
    /**
     * 更新博客基础信息
     * @param updateBlogSettingsReqVO
     * @return
     */
    Response updateBlogSettings(UpdateBlogSettingsReqVO updateBlogSettingsReqVO);


    /**
     * 查询博客基础信息
     * @return
     */
    Response findBlogSettingsDetail();
}
