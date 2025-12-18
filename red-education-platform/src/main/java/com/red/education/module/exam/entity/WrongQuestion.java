package com.red.education.module.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 错题本实体类
 */
@Data
@TableName("wrong_question")
public class WrongQuestion {

    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 用户答案
     */
    private String userAnswer;

    /**
     * 成绩记录ID
     */
    private Long examScoreId;

    /**
     * 是否已掌握：0-未掌握, 1-已掌握
     */
    private Integer isMastered;

    /**
     * 添加时间
     */
    private LocalDateTime createTime;
}
