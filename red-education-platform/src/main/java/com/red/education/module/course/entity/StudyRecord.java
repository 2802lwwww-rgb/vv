package com.red.education.module.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 学习记录实体类
 */
@Data
@TableName("study_record")
public class StudyRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long courseId;

    private Integer studyTime;

    private Integer progress;

    private Integer isCompleted;

    private LocalDateTime completeTime;

    private Integer lastPosition;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
