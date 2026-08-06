package com.rookie.web.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.rookie.common.domain.dos.ArticleDO;
import com.rookie.common.domain.dos.CommentDO;
import com.rookie.common.domain.mapper.ArticleMapper;
import com.rookie.common.domain.mapper.CommentMapper;
import com.rookie.common.enums.ResponseCodeEnum;
import com.rookie.common.exception.BizException;
import com.rookie.common.utils.Response;
import com.rookie.web.model.vo.comment.FindCommentListReqVO;
import com.rookie.web.model.vo.comment.FindCommentListRspVO;
import com.rookie.web.model.vo.comment.PublishCommentReqVO;
import com.rookie.web.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author kiro
 * @ClassName CommentServiceImpl
 * @date 2026年08月05日
 */
@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public Response publishComment(PublishCommentReqVO reqVO) {
        Long articleId = reqVO.getArticleId();
        // 校验文章存在且已发布
        ArticleDO articleDO = articleMapper.selectById(articleId);
        if (Objects.isNull(articleDO) || !Integer.valueOf(1).equals(articleDO.getStatus())) {
            log.warn("==> 评论的文章不存在或未发布, articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 若为回复，校验父评论存在
        Long parentId = reqVO.getParentId();
        if (Objects.nonNull(parentId)) {
            CommentDO parent = commentMapper.selectById(parentId);
            if (Objects.isNull(parent)) {
                log.warn("==> 回复的父评论不存在, parentId: {}", parentId);
                throw new BizException(ResponseCodeEnum.COMMENT_PARENT_NOT_EXISTED);
            }
        }

        CommentDO commentDO = CommentDO.builder()
                .articleId(articleId)
                .parentId(parentId)
                .replyNickname(reqVO.getReplyNickname())
                .nickname(reqVO.getNickname())
                .email(reqVO.getEmail())
                .website(reqVO.getWebsite())
                .content(reqVO.getContent())
                // 默认直接展示；后台可下架/拒绝。如需先审核，改为 0 即可
                .status(1)
                .createTime(LocalDateTime.now())
                .build();
        commentMapper.insert(commentDO);
        return Response.success();
    }

    @Override
    public Response findCommentList(FindCommentListReqVO reqVO) {
        Long articleId = reqVO.getArticleId();
        List<CommentDO> commentDOS = commentMapper.selectPublishedByArticleId(articleId);
        if (CollectionUtils.isEmpty(commentDOS)) {
            return Response.success(java.util.Collections.emptyList());
        }

        // 一级评论
        List<FindCommentListRspVO> topComments = commentDOS.stream()
                .filter(c -> Objects.isNull(c.getParentId()))
                .map(this::convert)
                .collect(Collectors.toList());

        // 回复按 parentId 分组
        Map<Long, List<FindCommentListRspVO>> childrenMap = commentDOS.stream()
                .filter(c -> Objects.nonNull(c.getParentId()))
                .collect(Collectors.groupingBy(CommentDO::getParentId,
                        Collectors.mapping(this::convert, Collectors.toList())));

        // 挂载楼中楼
        topComments.forEach(top -> top.setChildren(childrenMap.getOrDefault(top.getId(), java.util.Collections.emptyList())));

        return Response.success(topComments);
    }

    private FindCommentListRspVO convert(CommentDO c) {
        return FindCommentListRspVO.builder()
                .id(c.getId())
                .nickname(c.getNickname())
                .website(c.getWebsite())
                .content(c.getContent())
                .replyNickname(c.getReplyNickname())
                .createTime(c.getCreateTime())
                .build();
    }
}
