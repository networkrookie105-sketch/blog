package com.rookie.admin.controller;

import com.rookie.admin.model.vo.tag.AddTagReqVO;
import com.rookie.admin.model.vo.tag.DeleteTagReqVO;
import com.rookie.admin.model.vo.tag.FindTagPageListReqVO;
import com.rookie.admin.service.AdminTagService;
import com.rookie.common.aspect.ApiOperationLog;
import com.rookie.common.utils.PageResponse;
import com.rookie.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author peishunwu
 * @ClassName AdminTagController
 * @description: TODO
 * @date 2024年05月03日
 * @version: 1.0
 */
@RestController
@RequestMapping("/admin/tag")
@Api(tags = "Admin 标签模块")
public class AdminTagController {
    @Autowired
    private AdminTagService adminTagService;
    @PostMapping("/add")
    @ApiOperation(value = "添加标签")
    @ApiOperationLog(description = "添加标签")
    public Response addTag(@RequestBody @Validated AddTagReqVO addTagReqVO) {
        return adminTagService.addTag(addTagReqVO);
    }

    @PostMapping("/list")
    @ApiOperation(value = "标签分页数据获取")
    @ApiOperationLog(description = "标签分页数据获取")
    public PageResponse findTagPageList(@RequestBody FindTagPageListReqVO findTagPageListReqVO) {
        return adminTagService.findTagPageList(findTagPageListReqVO);
    }
    @PostMapping("/delete")
    @ApiOperation(value = "标签删除")
    @ApiOperationLog(description = "标签删除")
    public Response deleteTag(@RequestBody @Validated DeleteTagReqVO deleteTagReqVO) {
        return adminTagService.deleteTag(deleteTagReqVO);
    }

    @PostMapping("/select/list")
    @ApiOperation(value = "标签 Select 下拉列表数据获取")
    @ApiOperationLog(description = "标签 Select 下拉列表数据获取")
    public Response findTagSeclectList(@RequestParam(value = "name", required = false) String name) {
        return adminTagService.findTagSeclectList(name);
    }


    @PostMapping("/select/all/list")
    @ApiOperation(value = "标签 Select 下拉列表数据获取")
    @ApiOperationLog(description = "标签 Select 下拉列表数据获取")
    public Response findTagSelectList() {
        return adminTagService.findTagSelectList();
    }
}
