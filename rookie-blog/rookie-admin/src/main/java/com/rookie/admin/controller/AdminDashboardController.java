package com.rookie.admin.controller;

import com.rookie.admin.service.AdminDashboardService;
import com.rookie.common.aspect.ApiOperationLog;
import com.rookie.common.utils.Response;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kiro
 * @ClassName AdminDashboardController
 * @date 2026年08月05日
 */
@RestController
@RequestMapping("/admin/dashboard")
@Tag(name = "Admin 数据看板")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService adminDashboardService;

    @GetMapping("/statistics")
    @Operation(summary = "获取数据看板统计信息")
    @ApiOperationLog(description = "获取数据看板统计信息")
    public Response findStatistics() {
        return adminDashboardService.findStatistics();
    }
}
