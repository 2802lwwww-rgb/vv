package com.red.education.module.resource.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 审核资源DTO
 */
@Data
public class AuditResourceDTO {

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
