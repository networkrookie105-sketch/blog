package com.rookie.admin.controller;

import com.rookie.admin.model.vo.category.AddCategoryReqVO;
import com.rookie.admin.model.vo.category.DeleteCategoryReqVO;
import com.rookie.admin.model.vo.category.FindCategoryPageListReqVO;
import com.rookie.admin.service.AdminCategoryService;
import com.rookie.common.aspect.ApiOperationLog;
import com.rookie.common.utils.PageResponse;
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
 * @ClassName AdminCategoryController
 * @description: TODO
 * @date 2024年04月23日
 * @version: 1.0
 */
@RestController
@RequestMapping("/admin")
@Tag(name = "Admin 分类模块")
public class AdminCategoryController {
    @Autowired
    private AdminCategoryService adminCategoryService;
    @PostMapping("/category/add")
    @Operation(summary = "添加分类")
    @ApiOperationLog(description = "添加分类")
    public Response addCategory(@RequestBody @Validated AddCategoryReqVO addCategoryReqVO) {
        return adminCategoryService.addCategory(addCategoryReqVO);
    }
    @PostMapping("/category/list")
    @Operation(summary = "分类分页数据获取")
    @ApiOperationLog(description = "分类分页数据获取")
    public PageResponse findCategoryPageList(@RequestBody FindCategoryPageListReqVO findCategoryPageListReqVO) {
        return adminCategoryService.findCategoryPageList(findCategoryPageListReqVO);
    }
    @PostMapping("/category/delete")
    @Operation(summary = "删除分类")
    @ApiOperationLog(description = "删除分类")
    public Response deleteCategory(@RequestBody @Validated DeleteCategoryReqVO deleteCategoryReqVO) {
        return adminCategoryService.deleteCategory(deleteCategoryReqVO);
    }
    @PostMapping("/category/select/list")
    @Operation(summary = "分类 Select 下拉列表数据获取")
    @ApiOperationLog(description = "分类 Select 下拉列表数据获取")
    public Response findCategorySeclectList() {
        return adminCategoryService.findCategorySeclectList();
    }
}
