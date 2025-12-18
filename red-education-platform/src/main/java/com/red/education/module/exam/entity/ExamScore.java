package com.red.education.module.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 考试成绩实体类
 */
@Data
@TableName("exam_score")
public class ExamScore {

    /**
     * 成绩ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 试卷ID
     */
    private Long examId;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 总分
     */
    private Integer totalScore;

    /**
     * 是否及格：0-不及格, 1-及格
     */
    private Integer pass;

    /**
     * 正确题数
     */
    private Integer correctCount;

    /**
     * 错误题数
     */
    private Integer wrongCount;

    /**
     * 用时（秒）
     */
    private Integer duration;

    /**
     * 答案JSON
     */
    private String answers;

    /**
     * 提交时间
     */
    private LocalDateTime createTime;
}
