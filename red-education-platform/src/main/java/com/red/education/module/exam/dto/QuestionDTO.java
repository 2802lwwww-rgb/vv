package com.red.education.module.exam.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 题目DTO
 */
@Data
public class QuestionDTO {

    /**
     * 题型：SINGLE_CHOICE-单选题, JUDGE-判断题
     */
    @NotBlank(message = "题型不能为空")
    private String type;

    /**
     * 题目内容
     */
    @NotBlank(message = "题目内容不能为空")
    private String content;

    /**
     * 选项A
     */
    private String optionA;

    /**
     * 选项B
     */
    private String optionB;

    /**
     * 选项C
     */
    private String optionC;

    /**
     * 选项D
     */
    private String optionD;

    /**
     * 正确答案
     */
    @NotBlank(message = "正确答案不能为空")
    private String correctAnswer;

    /**
     * 答案解析
     */
    private String explanation;

    /**
     * 难度：1-简单, 2-中等, 3-困难
     */
    @NotNull(message = "难度不能为空")
    private Integer difficulty;

    /**
     * 分值
     */
    @NotNull(message = "分值不能为空")
    private Integer score;

    /**
     * 知识点分类
     */
    private String category;

    /**
     * 标签
     */
    private String tags;
}
