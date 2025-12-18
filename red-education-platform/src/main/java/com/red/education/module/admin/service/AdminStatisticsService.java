package com.red.education.module.admin.service;

import com.red.education.module.admin.vo.DashboardStatsVO;

/**
 * 后台统计服务接口
 */
public interface AdminStatisticsService {

    /**
     * 获取仪表盘统计数据
     *
     * @return 统计数据
     */
    DashboardStatsVO getDashboardStats();
}
