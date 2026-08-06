package com.rookie.admin.service;

import com.rookie.common.utils.Response;

/**
 * @author kiro
 * @ClassName AdminDashboardService
 * @date 2026年08月05日
 */
public interface AdminDashboardService {

    /**
     * 获取后台数据看板统计信息
     */
    Response findStatistics();
}
