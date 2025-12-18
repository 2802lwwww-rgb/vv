package com.red.education.module.community.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论VO
 */
@Data
public class CommentVO {

    private Long id;
    private Long postId;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String content;
    private Long parentId;
    private Long replyToUserId;
    private String replyToUsername;
    private Integer likeCount;
    private LocalDateTime createTime;
}
