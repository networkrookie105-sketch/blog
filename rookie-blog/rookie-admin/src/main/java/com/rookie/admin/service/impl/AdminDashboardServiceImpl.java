package com.rookie.admin.service.impl;

import com.rookie.admin.model.vo.dashboard.FindDashboardStatisticsRspVO;
import com.rookie.admin.service.AdminDashboardService;
import com.rookie.common.domain.mapper.ArticleMapper;
import com.rookie.common.domain.mapper.CategoryMapper;
import com.rookie.common.domain.mapper.CommentMapper;
import com.rookie.common.domain.mapper.TagMapper;
import com.rookie.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kiro
 * @ClassName AdminDashboardServiceImpl
 * @date 2026年08月05日
 */
@Service
@Slf4j
public class AdminDashboardServiceImpl implements AdminDashboardService {

    /** 趋势统计天数 */
    private static final int TREND_DAYS = 7;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private CommentMapper commentMapper;

    /** selectCount 在不同 MyBatis-Plus 版本返回 Integer/Long，统一转 Long */
    private static Long toLong(Number n) {
        return n == null ? 0L : n.longValue();
    }

    @Override
    public Response findStatistics() {
        Long articleCount = toLong(articleMapper.selectCount(null));
        Long categoryCount = toLong(categoryMapper.selectCount(null));
        Long tagCount = toLong(tagMapper.selectCount(null));
        Long commentCount = toLong(commentMapper.selectCount(null));
        Long totalReadNum = articleMapper.selectTotalReadNum();

        // 近 7 天发布趋势（补齐无数据的日期为 0）
        LocalDate startDate = LocalDate.now().minusDays(TREND_DAYS - 1L);
        List<Map<String, Object>> rawTrend = articleMapper.selectPublishCountByDay(startDate);
        Map<String, Long> dayCountMap = new HashMap<>();
        if (rawTrend != null) {
            for (Map<String, Object> row : rawTrend) {
                Object dateObj = row.get("date");
                Object countObj = row.get("count");
                if (dateObj != null && countObj != null) {
                    // DATE() 返回的可能是 java.sql.Date 或字符串，统一取前 10 位
                    String date = dateObj.toString();
                    dayCountMap.put(date.length() >= 10 ? date.substring(0, 10) : date, Long.parseLong(countObj.toString()));
                }
            }
        }

        List<String> trendDates = new ArrayList<>(TREND_DAYS);
        List<Long> trendCounts = new ArrayList<>(TREND_DAYS);
        for (int i = 0; i < TREND_DAYS; i++) {
            LocalDate day = startDate.plusDays(i);
            String key = day.format(DATE_FORMATTER);
            trendDates.add(key);
            trendCounts.add(dayCountMap.getOrDefault(key, 0L));
        }

        FindDashboardStatisticsRspVO vo = FindDashboardStatisticsRspVO.builder()
                .articleCount(articleCount)
                .categoryCount(categoryCount)
                .tagCount(tagCount)
                .commentCount(commentCount)
                .totalReadNum(totalReadNum)
                .trendDates(trendDates)
                .trendCounts(trendCounts)
                .build();
        return Response.success(vo);
    }
}
