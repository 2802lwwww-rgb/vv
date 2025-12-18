package com.red.education.common.vo;

import lombok.Data;

/**
 * 社区统计VO
 */
@Data
public class CommunityStatisticsVO {

    /**
     * 帖子总数
     */
    private Long totalPosts;

    /**
     * 评论总数
     */
    private Long totalComments;

    /**
     * 点赞总数
     */
    private Long totalLikes;

    /**
     * 待审核帖子数
     */
    private Long pendingPosts;

    /**
     * 今日新增帖子数
     */
    private Long todayNewPosts;

    /**
     * 活跃用户数
     */
    private Long activeUsers;
}
