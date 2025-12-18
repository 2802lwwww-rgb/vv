package com.red.education.module.exam.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 提交试卷DTO
 */
@Data
public class SubmitExamDTO {

    /**
     * 试卷ID
     */
    @NotNull(message = "试卷ID不能为空")
    private Long examId;

    /**
     * 答案 Map<题目ID, 用户答案>
     */
    @NotNull(message = "答案不能为空")
    private Map<Long, String> answers;

    /**
     * 用时（分钟）
     */
    private Integer duration;
}
