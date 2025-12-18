package com.red.education.module.community.vo;

import lombok.Data;

/**
 * 话题统计VO
 */
@Data
public class TopicVO {

    /**
     * 话题名称
     */
    private String topic;

    /**
     * 帖子数量
     */
    private Long postCount;

    /**
     * 总浏览量
     */
    private Long totalViews;

    /**
     * 总点赞数
     */
    private Long totalLikes;
}
