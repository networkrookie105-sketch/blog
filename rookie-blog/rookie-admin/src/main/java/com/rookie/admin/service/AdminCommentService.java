package com.rookie.admin.service;

import com.rookie.admin.model.vo.comment.DeleteCommentReqVO;
import com.rookie.admin.model.vo.comment.FindCommentPageListReqVO;
import com.rookie.admin.model.vo.comment.UpdateCommentStatusReqVO;
import com.rookie.common.utils.Response;

/**
 * @author kiro
 * @ClassName AdminCommentService
 * @date 2026年08月05日
 */
public interface AdminCommentService {

    Response findCommentPageList(FindCommentPageListReqVO reqVO);

    Response updateCommentStatus(UpdateCommentStatusReqVO reqVO);

    Response deleteComment(DeleteCommentReqVO reqVO);
}
