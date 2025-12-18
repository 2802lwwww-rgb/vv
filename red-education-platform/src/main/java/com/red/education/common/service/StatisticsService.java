package com.red.education.common.service;

import com.red.education.common.vo.*;

/**
 * 统计服务接口
 */
public interface StatisticsService {

    /**
     * 获取总览数据
     */
    OverviewVO getOverview();

    /**
     * 获取用户统计
     */
    UserStatisticsVO getUserStatistics();

    /**
     * 获取资源统计
     */
    ResourceStatisticsVO getResourceStatistics();

    /**
     * 获取学习统计
     */
    StudyStatisticsVO getStudyStatistics();

    /**
     * 获取考试统计
     */
    ExamStatisticsVO getExamStatistics();

    /**
     * 获取社区统计
     */
    CommunityStatisticsVO getCommunityStatistics();
}
