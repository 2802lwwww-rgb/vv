package com.red.education.module.exam.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 试卷DTO
 */
@Data
public class ExamDTO {

    /**
     * 试卷名称
     */
    @NotBlank(message = "试卷名称不能为空")
    private String name;

    /**
     * 试卷说明
     */
    private String description;

    /**
     * 类型：1-随机组卷, 2-固定试卷
     */
    @NotNull(message = "试卷类型不能为空")
    private Integer type;

    /**
     * 考试时长（分钟）
     */
    @NotNull(message = "考试时长不能为空")
    private Integer duration;

    /**
     * 总分
     */
    @NotNull(message = "总分不能为空")
    private Integer totalScore;

    /**
     * 及格分数
     */
    @NotNull(message = "及格分数不能为空")
    private Integer passScore;

    /**
     * 题目数量（固定试卷可选，用于初始化）
     */
    private Integer questionCount;

    /**
     * 题目ID列表（固定试卷必填）
     */
    private List<Long> questionIds;

    /**
     * 随机组卷配置（随机试卷必填）
     */
    private RandomConfig randomConfig;

    /**
     * 随机组卷配置
     */
    @Data
    public static class RandomConfig {
        /**
         * 题目分类（可选）
         */
        private String category;

        /**
         * 题目难度（可选，1-简单 2-中等 3-困难）
         */
        private Integer difficulty;

        /**
         * 题目类型（可选，SINGLE_CHOICE-单选 JUDGE-判断）
         */
        private String questionType;

        /**
         * 抽取题目数量
         */
        @NotNull(message = "题目数量不能为空")
        private Integer questionCount;
    }
}
