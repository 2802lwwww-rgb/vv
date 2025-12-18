package com.red.education.module.course.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学习历史VO（包含课程信息）
 */
@Data
public class StudyHistoryVO {
    private Long id;
    private Long userId;
    private Long courseId;

    // 课程信息
    private String courseTitle;
    private String courseCover;
    private Integer totalChapters;
    private Integer completedChapters;

    // 学习信息
    private Integer studyTime; // 学习时长（秒）
    private Integer totalDuration; // 总学习时长（分钟）
    private Integer progress;
    private Integer isCompleted;
    private Integer lastPosition;
    private LocalDateTime lastStudyTime; // 最后学习时间
    private LocalDateTime createTime;
}
