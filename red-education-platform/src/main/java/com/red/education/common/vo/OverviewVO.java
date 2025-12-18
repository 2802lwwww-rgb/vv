package com.red.education.common.vo;

import lombok.Data;

/**
 * 总览数据VO
 */
@Data
public class OverviewVO {

    /**
     * 总用户数
     */
    private Long totalUsers;

    /**
     * 总资源数
     */
    private Long totalResources;

    /**
     * 总课程数
     */
    private Long totalCourses;

    /**
     * 总试卷数
     */
    private Long totalExams;

    /**
     * 总帖子数
     */
    private Long totalPosts;

    /**
     * 总积分发放量
     */
    private Long totalPoints;
}
