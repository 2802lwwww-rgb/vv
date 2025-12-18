package com.red.education.module.community.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 帖子列表VO
 */
@Data
public class PostVO {

    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String title;
    private String content; // 截取前200字
    private String topic;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer isTop;
    private Integer status; // 0-待审核, 1-已通过, 2-已驳回
    private String auditComment; // 审核意见（驳回原因）
    private LocalDateTime createTime;
}
