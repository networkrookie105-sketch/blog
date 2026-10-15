package com.rookie.web.controller;

import com.rookie.common.aspect.ApiOperationLog;
import com.rookie.common.utils.Response;
import com.rookie.web.service.BlogSettingsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author peishunwu
 * @ClassName BlogSettingsController
 * @description: TODO
 * @date 2024年09月05日
 * @version: 1.0
 */
@RestController
@RequestMapping("/blog/settings")
@Tag(name = "博客设置")
public class BlogSettingsController {

    @Autowired
    private BlogSettingsService blogSettingsService;

    @GetMapping("/detail")
    @Operation(summary = "查询博客设置详情")
    @ApiOperationLog(description = "查询博客设置详情")
    public Response findBlogSettingsDetail() {
        return blogSettingsService.findBlogSettingsDetail();
    }
}
