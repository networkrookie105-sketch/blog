package com.rookie.web.controller;

import com.rookie.common.aspect.ApiOperationLog;
import com.rookie.common.utils.Response;
import com.rookie.web.model.vo.category.FindCategoryArticlePageListReqVO;
import com.rookie.web.service.CategoryService;
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
 * @ClassName CategoryController
 * @description: TODO
 * @date 2024年09月04日
 * @version: 1.0
 */
@RestController
@RequestMapping("/category")
@Tag(name = "分类")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping("/list")
    @Operation(summary = "前台查询分类列表")
    @ApiOperationLog(description = "前台查询分类列表")
    public Response findCategoryList() {
        return categoryService.findCategoryList();
    }


    @PostMapping("/article/list")
    @Operation(summary = "前台获取分类下文章分页数据")
    @ApiOperationLog(description = "前台获取分类下文章分页数据")
    public Response findCategoryArticlePageList(@RequestBody @Validated FindCategoryArticlePageListReqVO findCategoryArticlePageListReqVO) {
        return categoryService.findCategoryArticlePageList(findCategoryArticlePageListReqVO);
    }
}
