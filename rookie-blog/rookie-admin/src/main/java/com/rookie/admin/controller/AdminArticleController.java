package com.rookie.admin.controller;

import com.rookie.admin.model.vo.article.*;
import com.rookie.admin.service.AdminArticleService;
import com.rookie.common.aspect.ApiOperationLog;
import com.rookie.common.utils.Response;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author peishunwu
 * @ClassName AdminArticleController
 * @description: TODO
 * @date 2024年05月30日
 * @version: 1.0
 */
@RestController
@RequestMapping("/admin/article")
@Tag(name = "Admin 文章模块")
public class AdminArticleController {

    @Autowired
    private AdminArticleService adminArticleService;

    @PostMapping("/publish")
    @Operation(summary = "发布文章")
    @ApiOperationLog(description = "文章发布")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response publishArticle(@RequestBody @Validated PublishArticleReqVO publishArticleReqVO) {
        return adminArticleService.publishArticle(publishArticleReqVO);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除文章")
    @ApiOperationLog(description = "删除文章")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response deleteArticle(@RequestBody DeleteArticleReqVO deleteArticleReqVO) {
        return adminArticleService.deleteArticle(deleteArticleReqVO);
    }

    @PostMapping("/list")
    @Operation(summary = "查询文章分页数据")
    @ApiOperationLog(description = "查询文章分页数据")
    public Response findArticlePageList(@RequestBody @Validated FindArticlePageListReqVO findArticlePageListReqVO) {
        return adminArticleService.findArticlePageList(findArticlePageListReqVO);
    }

    @PostMapping("/detail")
    @Operation(summary = "查询文章详情")
    @ApiOperationLog(description = "查询文章详情")
    public Response findArticleDetail(@RequestBody @Validated FindArticleDetailReqVO findArticleDetailReqVO) {
        return adminArticleService.findArticleDetail(findArticleDetailReqVO);
    }

    @PostMapping("/update")
    @Operation(summary = "更新文章")
    @ApiOperationLog(description = "更新文章")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response updateArticle(@RequestBody UpdateArticleReqVO updateArticleReqVO) {
        return adminArticleService.updateArticle(updateArticleReqVO);
    }
}
