package com.rookie.admin.controller;

import com.rookie.admin.model.vo.comment.DeleteCommentReqVO;
import com.rookie.admin.model.vo.comment.FindCommentPageListReqVO;
import com.rookie.admin.model.vo.comment.UpdateCommentStatusReqVO;
import com.rookie.admin.service.AdminCommentService;
import com.rookie.common.aspect.ApiOperationLog;
import com.rookie.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kiro
 * @ClassName AdminCommentController
 * @date 2026年08月05日
 */
@RestController
@RequestMapping("/admin/comment")
@Api(tags = "Admin 评论模块")
public class AdminCommentController {

    @Autowired
    private AdminCommentService adminCommentService;

    @PostMapping("/list")
    @ApiOperation(value = "评论分页查询")
    @ApiOperationLog(description = "评论分页查询")
    public Response findCommentPageList(@RequestBody FindCommentPageListReqVO reqVO) {
        return adminCommentService.findCommentPageList(reqVO);
    }

    @PostMapping("/update/status")
    @ApiOperation(value = "评论审核")
    @ApiOperationLog(description = "评论审核")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response updateCommentStatus(@RequestBody @Validated UpdateCommentStatusReqVO reqVO) {
        return adminCommentService.updateCommentStatus(reqVO);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除评论")
    @ApiOperationLog(description = "删除评论")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response deleteComment(@RequestBody @Validated DeleteCommentReqVO reqVO) {
        return adminCommentService.deleteComment(reqVO);
    }
}
