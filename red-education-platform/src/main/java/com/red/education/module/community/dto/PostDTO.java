package com.red.education.module.community.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 发布/编辑帖子DTO
 */
@Data
public class PostDTO {

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 内容
     */
    @NotBlank(message = "内容不能为空")
    private String content;

    /**
     * 图片URL（多张用逗号分隔）
     */
    private String images;

    /**
     * 话题标签
     */
    private String topic;
}
