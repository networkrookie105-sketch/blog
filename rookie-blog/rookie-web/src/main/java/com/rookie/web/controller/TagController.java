package com.rookie.web.controller;

import com.rookie.common.aspect.ApiOperationLog;
import com.rookie.common.utils.Response;
import com.rookie.web.model.vo.tag.FindTagArticlePageListReqVO;
import com.rookie.web.service.TagService;
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
 * @ClassName TagController
 * @description: TODO
 * @date 2024年09月04日
 * @version: 1.0
 */
@RestController
@RequestMapping("/tag")
@Tag(name = "标签")
public class TagController {

    @Autowired
    private TagService tagService;

    @PostMapping("/list")
    @Operation(summary = "前台查询标签列表")
    @ApiOperationLog(description = "前台查询标签列表")
    public Response findTagList() {
        return tagService.findTagList();
    }

    @PostMapping("/article/list")
    @Operation(summary = "前台获取标签下文章列表")
    @ApiOperationLog(description = "前台获取标签下文章列表")
    public Response findTagPageList(@RequestBody @Validated FindTagArticlePageListReqVO findTagArticlePageListReqVO) {
        return tagService.findTagPageList(findTagArticlePageListReqVO);
    }
}
