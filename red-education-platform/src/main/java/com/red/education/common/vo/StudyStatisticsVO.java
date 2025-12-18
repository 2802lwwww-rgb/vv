package com.red.education.common.vo;

import lombok.Data;

/**
 * 学习统计VO
 */
@Data
public class StudyStatisticsVO {

    /**
     * 课程总数
     */
    private Long totalCourses;

    /**
     * 学习人数
     */
    private Long studyUserCount;

    /**
     * 总学习时长（分钟）
     */
    private Long totalStudyTime;

    /**
     * 平均学习时长（分钟）
     */
    private Double avgStudyTime;

    /**
     * 课程完成总数
     */
    private Long completedCourses;

    /**
     * 课程完成率（%）
     */
    private Double courseCompletionRate;
}
