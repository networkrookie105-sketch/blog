package com.rookie.web.service;

import com.rookie.common.utils.Response;
import com.rookie.web.model.vo.comment.FindCommentListReqVO;
import com.rookie.web.model.vo.comment.PublishCommentReqVO;

/**
 * @author kiro
 * @ClassName CommentService
 * @date 2026年08月05日
 */
public interface CommentService {

    /**
     * 发表评论
     */
    Response publishComment(PublishCommentReqVO reqVO);

    /**
     * 查询文章评论列表（楼中楼树形结构）
     */
    Response findCommentList(FindCommentListReqVO reqVO);
}
