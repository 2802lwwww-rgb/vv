package com.red.education.common.vo;

import lombok.Data;

/**
 * 用户统计VO
 */
@Data
public class UserStatisticsVO {

    /**
     * 总用户数
     */
    private Long totalUsers;

    /**
     * 今日新增用户数
     */
    private Long todayNewUsers;

    /**
     * 本周新增用户数
     */
    private Long weekNewUsers;

    /**
     * 本月新增用户数
     */
    private Long monthNewUsers;

    /**
     * 活跃用户数（近7天）
     */
    private Long activeUsers7Days;

    /**
     * 活跃用户数（近30天）
     */
    private Long activeUsers30Days;
}
