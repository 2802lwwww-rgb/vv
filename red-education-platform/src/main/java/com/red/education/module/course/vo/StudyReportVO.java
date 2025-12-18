package com.red.education.module.course.vo;

import lombok.Data;

import java.util.List;

/**
 * 学习报告VO
 */
@Data
public class StudyReportVO {

    /**
     * 总学习时长（分钟）
     */
    private Long totalStudyTime;

    /**
     * 已完成课程数量
     */
    private Integer completedCourses;

    /**
     * 学习天数统计
     */
    private Integer studyDays;

    /**
     * 每日学习时长（最近7天）
     * 格式：["2025-11-22", "2025-11-23", ...]
     */
    private List<String> studyDates;

    /**
     * 每日学习时长数据（最近7天，单位：分钟）
     * 格式：[30, 45, 60, ...]
     */
    private List<Integer> dailyStudyTime;

    /**
     * 课程完成率（%）
     */
    private Double courseCompletionRate;

    /**
     * 获得积分总数
     */
    private Integer totalPoints;

    /**
     * 学习中的课程数
     */
    private Integer inProgressCourses;

    /**
     * 平均每天学习时长（分钟）
     */
    private Double avgDailyStudyTime;

    /**
     * 学习勤奋度（0-5星）
     */
    private Double diligenceScore;
}
