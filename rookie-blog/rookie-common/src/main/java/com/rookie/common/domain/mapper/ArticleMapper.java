package com.rookie.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rookie.common.domain.dos.ArticleDO;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * @author peishunwu
 * @ClassName ArticleMapper
 * @description: TODO
 * @date 2024年05月27日
 * @version: 1.0
 */
public interface ArticleMapper extends BaseMapper<ArticleDO> {

    /**
     * 分页查询文章列表
     * @param current
     * @param size
     * @param title
     * @param startDate
     * @param endDate
     * @return
     */
    default Page<ArticleDO> selectPageList(Long current, Long size, String title, LocalDate startDate, LocalDate endDate) {
        // 分页对象(查询第几页、每页多少数据)
        Page<ArticleDO> page = new Page<>(current, size);
        // 查询条件（后台管理：草稿 + 已发布都展示，置顶优先，其次按创建时间倒序）
        LambdaQueryWrapper<ArticleDO> queryWrapper = Wrappers.<ArticleDO>lambdaQuery()
                .like(StringUtils.isNotBlank(title), ArticleDO::getTitle, title)
                .ge(Objects.nonNull(startDate), ArticleDO::getCreateTime, startDate)
                .le(Objects.nonNull(endDate), ArticleDO::getCreateTime, endDate)
                .orderByDesc(ArticleDO::getIsTop)
                .orderByDesc(ArticleDO::getCreateTime) ;
        return selectPage(page, queryWrapper);
    }

    /**
     * 前台分页查询「已发布」文章列表（置顶优先）
     * @param current 当前页
     * @param size 每页数量
     * @return 分页结果
     */
    default Page<ArticleDO> selectPublishedPageList(Long current, Long size) {
        Page<ArticleDO> page = new Page<>(current, size);
        LambdaQueryWrapper<ArticleDO> queryWrapper = Wrappers.<ArticleDO>lambdaQuery()
                .eq(ArticleDO::getStatus, 1) // 仅已发布
                .orderByDesc(ArticleDO::getIsTop)
                .orderByDesc(ArticleDO::getCreateTime);
        return selectPage(page, queryWrapper);
    }

    /** 归档按时间排列，不能受文章置顶顺序影响。 */
    default Page<ArticleDO> selectArchivePageList(Long current, Long size) {
        return selectPage(new Page<>(current, size), Wrappers.<ArticleDO>lambdaQuery()
                .eq(ArticleDO::getStatus, 1).orderByDesc(ArticleDO::getCreateTime).orderByDesc(ArticleDO::getId));
    }

    /**
     * 根据文章 ID 列表查询文章列表
     * @param current
     * @param size
     * @param articleIds
     * @return
     */
    default Page<ArticleDO> selectPageListByArticleIds(Long current, Long size, List<Long> articleIds) {
        Page<ArticleDO> page = new Page<>(current, size);
        //构建查询条件，根据文章id批量查询「已发布」文章，置顶优先、创建时间倒序
        LambdaQueryWrapper<ArticleDO> queryWrapper = Wrappers.<ArticleDO>lambdaQuery()
                .in(ArticleDO::getId, articleIds)
                .eq(ArticleDO::getStatus, 1)
                .orderByDesc(ArticleDO::getIsTop)
                .orderByDesc(ArticleDO::getCreateTime);
        return selectPage(page, queryWrapper);
    }

    /**
     * 查询上一篇文章
     * @param articleId
     * @return
     */
    default ArticleDO selectPreArticle(Long articleId) {
        ArticleDO current = selectById(articleId);
        if (current == null || current.getCreateTime() == null) return null;
        return selectOne(Wrappers.<ArticleDO>lambdaQuery().eq(ArticleDO::getStatus, 1)
                .and(w -> w.gt(ArticleDO::getCreateTime, current.getCreateTime())
                        .or(tie -> tie.eq(ArticleDO::getCreateTime, current.getCreateTime()).gt(ArticleDO::getId, articleId)))
                .orderByAsc(ArticleDO::getCreateTime).orderByAsc(ArticleDO::getId).last("limit 1"));
    }

    /** 下一篇按文章日期寻找，迁移文章的 ID 顺序可能与日期不一致。 */
    default ArticleDO selectNextArticle(Long articleId) {
        ArticleDO current = selectById(articleId);
        if (current == null || current.getCreateTime() == null) return null;
        return selectOne(Wrappers.<ArticleDO>lambdaQuery().eq(ArticleDO::getStatus, 1)
                .and(w -> w.lt(ArticleDO::getCreateTime, current.getCreateTime())
                        .or(tie -> tie.eq(ArticleDO::getCreateTime, current.getCreateTime()).lt(ArticleDO::getId, articleId)))
                .orderByDesc(ArticleDO::getCreateTime).orderByDesc(ArticleDO::getId).last("limit 1"));
    }

    /**
     * 阅读量 +1（数据库层面原子自增，避免并发覆盖）
     * @param articleId 文章 ID
     * @return 受影响行数
     */
    default int increaseReadNum(Long articleId) {
        return update(null, Wrappers.<ArticleDO>lambdaUpdate()
                .setSql("read_num = read_num + 1")
                .eq(ArticleDO::getId, articleId));
    }

    /**
     * 全文搜索：在「已发布」文章中，按标题或正文命中的文章 ID 分页检索
     * @param current 当前页
     * @param size 每页数量
     * @param keyword 关键字（匹配标题）
     * @param contentMatchIds 正文命中的文章 ID 列表（可空）
     * @return 分页结果
     */
    default Page<ArticleDO> searchPublishedPageList(Long current, Long size, String keyword, List<Long> contentMatchIds) {
        Page<ArticleDO> page = new Page<>(current, size);
        LambdaQueryWrapper<ArticleDO> wrapper = Wrappers.<ArticleDO>lambdaQuery()
                .eq(ArticleDO::getStatus, 1)
                .and(w -> {
                    w.like(ArticleDO::getTitle, keyword);
                    // 正文命中的文章 ID 也纳入结果
                    if (contentMatchIds != null && !contentMatchIds.isEmpty()) {
                        w.or().in(ArticleDO::getId, contentMatchIds);
                    }
                })
                .orderByDesc(ArticleDO::getCreateTime);
        return selectPage(page, wrapper);
    }

    /**
     * 统计所有文章的总阅读量
     * @return 总阅读量（无数据返回 0）
     */
    default Long selectTotalReadNum() {
        java.util.List<java.util.Map<String, Object>> maps = selectMaps(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ArticleDO>()
                        .select("IFNULL(SUM(read_num), 0) AS total"));
        if (maps == null || maps.isEmpty() || maps.get(0).get("total") == null) {
            return 0L;
        }
        return Long.parseLong(maps.get(0).get("total").toString());
    }

    /**
     * 按天统计指定日期(含)之后发布的文章数量
     * @param startDate 起始日期
     * @return 每天的 {date, count} 列表
     */
    default java.util.List<java.util.Map<String, Object>> selectPublishCountByDay(LocalDate startDate) {
        return selectMaps(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ArticleDO>()
                .select("DATE(create_time) AS date", "COUNT(*) AS count")
                .ge("create_time", startDate)
                .groupBy("DATE(create_time)"));
    }
}
