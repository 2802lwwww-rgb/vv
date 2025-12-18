package com.red.education.common.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 批量更新配置DTO
 */
@Data
public class ConfigUpdateDTO {

    /**
     * 配置列表
     */
    @NotEmpty(message = "配置列表不能为空")
    @Valid
    private List<SystemConfigDTO> configs;
}
