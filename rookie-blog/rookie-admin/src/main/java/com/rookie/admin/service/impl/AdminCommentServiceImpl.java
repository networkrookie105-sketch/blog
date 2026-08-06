package com.rookie.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rookie.admin.model.vo.comment.DeleteCommentReqVO;
import com.rookie.admin.model.vo.comment.FindCommentPageListReqVO;
import com.rookie.admin.model.vo.comment.FindCommentPageListRspVO;
import com.rookie.admin.model.vo.comment.UpdateCommentStatusReqVO;
import com.rookie.admin.service.AdminCommentService;
import com.rookie.common.domain.dos.CommentDO;
import com.rookie.common.domain.mapper.CommentMapper;
import com.rookie.common.enums.ResponseCodeEnum;
import com.rookie.common.exception.BizException;
import com.rookie.common.utils.PageResponse;
import com.rookie.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author kiro
 * @ClassName AdminCommentServiceImpl
 * @date 2026年08月05日
 */
@Service
@Slf4j
public class AdminCommentServiceImpl implements AdminCommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public Response findCommentPageList(FindCommentPageListReqVO reqVO) {
        Page<CommentDO> page = commentMapper.selectPageList(reqVO.getCurrent(), reqVO.getSize(),
                reqVO.getStatus(), reqVO.getArticleId(), reqVO.getKeyword());
        List<CommentDO> records = page.getRecords();
        List<FindCommentPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(records)) {
            vos = records.stream()
                    .map(c -> FindCommentPageListRspVO.builder()
                            .id(c.getId())
                            .articleId(c.getArticleId())
                            .parentId(c.getParentId())
                            .nickname(c.getNickname())
                            .email(c.getEmail())
                            .website(c.getWebsite())
                            .content(c.getContent())
                            .replyNickname(c.getReplyNickname())
                            .status(c.getStatus())
                            .createTime(c.getCreateTime())
                            .build())
                    .collect(Collectors.toList());
        }
        return PageResponse.success(page, vos);
    }

    @Override
    public Response updateCommentStatus(UpdateCommentStatusReqVO reqVO) {
        CommentDO commentDO = commentMapper.selectById(reqVO.getId());
        if (Objects.isNull(commentDO)) {
            throw new BizException(ResponseCodeEnum.COMMENT_NOT_EXISTED);
        }
        CommentDO update = CommentDO.builder()
                .id(reqVO.getId())
                .status(reqVO.getStatus())
                .build();
        commentMapper.updateById(update);
        return Response.success();
    }

    @Override
    public Response deleteComment(DeleteCommentReqVO reqVO) {
        // 删除评论同时删除其楼中楼回复
        commentMapper.deleteById(reqVO.getId());
        commentMapper.delete(com.baomidou.mybatisplus.core.toolkit.Wrappers.<CommentDO>lambdaQuery()
                .eq(CommentDO::getParentId, reqVO.getId()));
        return Response.success();
    }
}
