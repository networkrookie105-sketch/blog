package com.rookie.admin.controller;

import com.rookie.admin.model.vo.blogsettings.UpdateBlogSettingsReqVO;
import com.rookie.admin.service.AdminBlogSettingsService;
import com.rookie.common.aspect.ApiOperationLog;
import com.rookie.common.utils.Response;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author peishunwu
 * @ClassName AdminBlogSettingsController
 * @description: TODO
 * @date 2024年05月13日
 * @version: 1.0
 */
@RestController
@RequestMapping("/admin/blog/settings")
@Tag(name = "Admin 设置模块")
public class AdminBlogSettingsController {

    @Autowired
    private AdminBlogSettingsService adminBlogSettingsService;

    @PostMapping("/update")
    @Operation(summary = "更新博客基础信息")
    @ApiOperationLog(description = "更新博客基础信息")
    public Response updateBlogSettings(@RequestBody @Validated UpdateBlogSettingsReqVO updateBlogSettingsReqVO) {
        return adminBlogSettingsService.updateBlogSettings(updateBlogSettingsReqVO);
    }

    @PostMapping("/detail")
    @Operation(summary = "查询博客基础信息")
    @ApiOperationLog(description = "查询博客基础信息")
    public Response findBlogSettingsDetail() {
        return adminBlogSettingsService.findBlogSettingsDetail();
    }
}
