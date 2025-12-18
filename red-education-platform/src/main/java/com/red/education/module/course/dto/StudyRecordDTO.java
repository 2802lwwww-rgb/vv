package com.red.education.module.course.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 学习记录DTO
 */
@Data
public class StudyRecordDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    /**
     * 学习时长（分钟）
     */
    private Integer studyTime;

    /**
     * 学习进度（百分比0-100）
     */
    private Integer progress;

    /**
     * 最后学习位置（如视频秒数）
     */
    private Integer lastPosition;

    /**
     * 本次学习时长（秒）- 前端传入
     */
    private Integer duration;
}
