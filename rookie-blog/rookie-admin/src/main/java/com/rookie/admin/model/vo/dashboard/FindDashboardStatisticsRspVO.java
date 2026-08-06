package com.rookie.admin.model.vo.dashboard;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author kiro
 * @ClassName FindDashboardStatisticsRspVO
 * @date 2026年08月05日
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "后台数据看板统计 VO")
public class FindDashboardStatisticsRspVO {
    /** 文章总数 */
    private Long articleCount;
    /** 分类总数 */
    private Long categoryCount;
    /** 标签总数 */
    private Long tagCount;
    /** 评论总数 */
    private Long commentCount;
    /** 总阅读量 */
    private Long totalReadNum;
    /** 最近 N 天的日期（yyyy-MM-dd） */
    private List<String> trendDates;
    /** 最近 N 天每天发布的文章数量，与 trendDates 一一对应 */
    private List<Long> trendCounts;
}
