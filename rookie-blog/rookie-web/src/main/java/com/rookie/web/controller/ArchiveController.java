package com.rookie.web.controller;

import com.rookie.common.aspect.ApiOperationLog;
import com.rookie.common.utils.Response;
import com.rookie.web.model.vo.article.FindArchiveArticlePageListReqVO;
import com.rookie.web.service.ArchiveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author peishunwu
 * @ClassName ArchiveController
 * @description: TODO
 * @date 2025年08月11日
 * @version: 1.0
 */
@RestController
@Api(tags = "文章归档")
public class ArchiveController {
    @Autowired
    private ArchiveService archiveService;

    @PostMapping("/archive/list")
    @ApiOperation(value = "获取文章归档分页数据")
    @ApiOperationLog(description = "获取文章归档分页数据")
    public Response findArchiveArticlePageList(@RequestBody FindArchiveArticlePageListReqVO reqVO) {
        return archiveService.findArchivePageList(reqVO);
    }

}

