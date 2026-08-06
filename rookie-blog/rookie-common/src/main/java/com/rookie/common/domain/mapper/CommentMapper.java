package com.rookie.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rookie.common.domain.dos.CommentDO;

import java.util.List;
import java.util.Objects;

/**
 * @author kiro
 * @ClassName CommentMapper
 * @date 2026年08月05日
 */
public interface CommentMapper extends BaseMapper<CommentDO> {

    /**
     * 查询某文章下「已通过」的评论（按时间升序，便于楼层展示）
     * @param articleId 文章 ID
     * @return 评论列表
     */
    default List<CommentDO> selectPublishedByArticleId(Long articleId) {
        return selectList(Wrappers.<CommentDO>lambdaQuery()
                .eq(CommentDO::getArticleId, articleId)
                .eq(CommentDO::getStatus, 1)
                .orderByAsc(CommentDO::getCreateTime));
    }

    /**
     * 后台分页查询评论（可按状态、文章、昵称/内容关键字过滤）
     * @param current 当前页
     * @param size 每页数量
     * @param status 状态（可空）
     * @param articleId 文章 ID（可空）
     * @param keyword 昵称或内容关键字（可空）
     * @return 分页结果
     */
    default Page<CommentDO> selectPageList(Long current, Long size, Integer status, Long articleId, String keyword) {
        Page<CommentDO> page = new Page<>(current, size);
        LambdaQueryWrapper<CommentDO> wrapper = Wrappers.<CommentDO>lambdaQuery()
                .eq(Objects.nonNull(status), CommentDO::getStatus, status)
                .eq(Objects.nonNull(articleId), CommentDO::getArticleId, articleId)
                .and(StringUtils.isNotBlank(keyword), w ->
                        w.like(CommentDO::getContent, keyword).or().like(CommentDO::getNickname, keyword))
                .orderByDesc(CommentDO::getCreateTime);
        return selectPage(page, wrapper);
    }

    /**
     * 统计某状态的评论总数
     * @param status 状态（null 表示统计全部）
     * @return 数量
     */
    default long countByStatus(Integer status) {
        return selectCount(Wrappers.<CommentDO>lambdaQuery()
                .eq(Objects.nonNull(status), CommentDO::getStatus, status));
    }
}
