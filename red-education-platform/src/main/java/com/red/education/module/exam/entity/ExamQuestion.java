package com.red.education.module.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 试卷题目关联实体类
 */
@Data
@TableName("exam_question")
public class ExamQuestion {

    /**
     * 关联ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 试卷ID
     */
    private Long examId;

    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 排序
     */
    private Integer sort;
}
