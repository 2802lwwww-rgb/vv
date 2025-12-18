package com.red.education.module.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日学习日志实体类
 */
@Data
@TableName("daily_study_log")
public class DailyStudyLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private LocalDate studyDate;

    private Integer studyMinutes;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
