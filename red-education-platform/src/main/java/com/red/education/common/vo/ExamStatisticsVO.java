package com.red.education.common.vo;

import lombok.Data;

/**
 * 考试统计VO
 */
@Data
public class ExamStatisticsVO {

    /**
     * 试卷总数
     */
    private Long totalExams;

    /**
     * 题目总数
     */
    private Long totalQuestions;

    /**
     * 参与考试人数
     */
    private Long participantCount;

    /**
     * 总考试次数
     */
    private Long totalExamAttempts;

    /**
     * 平均分数
     */
    private Double avgScore;

    /**
     * 及格率（%）
     */
    private Double passRate;

    /**
     * 优秀率（%，90分以上）
     */
    private Double excellentRate;
}
