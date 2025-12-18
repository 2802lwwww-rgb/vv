package com.red.education.module.community.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 发表评论DTO
 */
@Data
public class CommentDTO {

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    private String content;

    /**
     * 父评论ID（0或null表示顶级评论）
     */
    private Long parentId;

    /**
     * 回复目标用户ID
     */
    /**
     * 回复目标用户ID
     */
    private Long replyToUserId;

    /**
     * 帖子ID
     */
    private Long postId;
}
