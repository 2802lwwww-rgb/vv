package com.red.education.module.community.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 审核帖子DTO
 */
@Data
public class AuditPostDTO {

    /**
     * 审核结果：1-通过, 2-驳回
     */
    @NotNull(message = "审核结果不能为空")
    private Integer status;

    /**
     * 审核意见
     */
    private String auditComment;
}
