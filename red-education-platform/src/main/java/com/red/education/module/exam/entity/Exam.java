package com.red.education.module.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 试卷实体类
 */
@Data
@TableName("exam")
public class Exam {

    /**
     * 试卷ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 试卷名称
     */
    private String name;

    /**
     * 试卷说明
     */
    private String description;

    /**
     * 类型：1-随机组卷, 2-固定试卷
     */
    private Integer type;

    /**
     * 考试时长（分钟）
     */
    private Integer duration;

    /**
     * 总分
     */
    private Integer totalScore;

    /**
     * 及格分数
     */
    private Integer passScore;

    /**
     * 题目数量
     */
    private Integer questionCount;

    /**
     * 状态：0-禁用, 1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
