package com.red.education.common.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 系统配置DTO
 */
@Data
public class SystemConfigDTO {

    /**
     * 配置键
     */
    @NotBlank(message = "配置键不能为空")
    private String configKey;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 描述
     */
    private String description;
}
