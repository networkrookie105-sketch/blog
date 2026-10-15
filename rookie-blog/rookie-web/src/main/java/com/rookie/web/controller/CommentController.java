package com.rookie.web.controller;

import com.rookie.common.aspect.ApiOperationLog;
import com.rookie.common.utils.Response;
import com.rookie.web.model.vo.comment.FindCommentListReqVO;
import com.rookie.web.model.vo.comment.PublishCommentReqVO;
import com.rookie.web.service.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kiro
 * @ClassName CommentController
 * @date 2026年08月05日
 */
@RestController
@RequestMapping("/comment")
@Tag(name = "评论")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/publish")
    @Operation(summary = "发表评论")
    @ApiOperationLog(description = "发表评论")
    public Response publishComment(@RequestBody @Validated PublishCommentReqVO reqVO) {
        return commentService.publishComment(reqVO);
    }

    @PostMapping("/list")
    @Operation(summary = "查询文章评论列表")
    @ApiOperationLog(description = "查询文章评论列表")
    public Response findCommentList(@RequestBody @Validated FindCommentListReqVO reqVO) {
        return commentService.findCommentList(reqVO);
    }
}
