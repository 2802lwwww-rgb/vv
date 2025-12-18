package com.red.education.module.resource.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 资源DTO
 */
@Data
public class ResourceDTO {

    /**
     * 资源标题
     */
    @NotBlank(message = "资源标题不能为空")
    private String title;

    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    /**
     * 资源描述
     */
    private String description;

    /**
     * 详细内容
     */
    private String content;

    /**
     * 封面图URL
     */
    private String coverImage;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 标签（逗号分隔）
     */
    private String tags;
}
