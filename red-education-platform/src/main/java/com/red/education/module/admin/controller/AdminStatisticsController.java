package com.red.education.module.admin.controller;

import com.red.education.common.result.Result;
import com.red.education.module.admin.service.AdminStatisticsService;
import com.red.education.module.admin.vo.DashboardStatsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台统计Controller
 */
@Api(tags = "后台统计")
@RestController
@RequestMapping("/admin/statistics")
public class AdminStatisticsController {

    @Autowired
    private AdminStatisticsService statisticsService;

    @ApiOperation("获取仪表盘统计数据")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    @GetMapping("/dashboard")
    public Result<DashboardStatsVO> getDashboardStats() {
        DashboardStatsVO stats = statisticsService.getDashboardStats();
        return Result.success(stats);
    }
}
