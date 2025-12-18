package com.red.education.module.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 管理后台统计数据VO
 */
@Data
@ApiModel("后台统计数据")
public class DashboardStatsVO {

    @ApiModelProperty("总用户数")
    private Long totalUsers;

    @ApiModelProperty("总资源数")
    private Long totalResources;

    @ApiModelProperty("总课程数")
    private Long totalCourses;

    @ApiModelProperty("总帖子数")
    private Long totalPosts;

    @ApiModelProperty("总试卷数")
    private Long totalExams;

    @ApiModelProperty("待审核帖子数")
    private Long pendingPosts;

    @ApiModelProperty("待审核资源数")
    private Long pendingResources;

    @ApiModelProperty("在线用户数")
    private Integer onlineUsers;

    @ApiModelProperty("今日访问量")
    private Integer todayVisits;

    @ApiModelProperty("用户增长数据（过去6个月）")
    private int[] userGrowthData;

    @ApiModelProperty("用户增长率（环比）")
    private Double userGrowthRate;

    @ApiModelProperty("资源增长率（环比）")
    private Double resourceGrowthRate;

    @ApiModelProperty("课程增长率（环比）")
    private Double courseGrowthRate;

    @ApiModelProperty("帖子增长率（环比）")
    private Double postGrowthRate;

    @ApiModelProperty("学习活跃度数据（近30天）")
    private List<StudyActivityVO> studyActivityData;

    @ApiModelProperty("考试通过率统计")
    private ExamPassRateVO examPassRate;

    @ApiModelProperty("热门资源/课程列表")
    private List<PopularItemVO> popularItems;
}
