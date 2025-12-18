package com.red.education.common.controller;

import com.red.education.common.result.Result;
import com.red.education.common.service.StatisticsService;
import com.red.education.common.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据统计Controller
 */
@Api(tags = "数据统计")
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @ApiOperation("总览数据")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    @GetMapping("/overview")
    public Result<OverviewVO> getOverview() {
        OverviewVO overview = statisticsService.getOverview();
        return Result.success(overview);
    }

    @ApiOperation("公开统计数据 - 首页展示")
    @GetMapping("/public")
    public Result<OverviewVO> getPublicStatistics() {
        OverviewVO overview = statisticsService.getOverview();
        return Result.success(overview);
    }

    @ApiOperation("用户统计")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    @GetMapping("/user")
    public Result<UserStatisticsVO> getUserStatistics() {
        UserStatisticsVO statistics = statisticsService.getUserStatistics();
        return Result.success(statistics);
    }

    @ApiOperation("资源统计")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    @GetMapping("/resource")
    public Result<ResourceStatisticsVO> getResourceStatistics() {
        ResourceStatisticsVO statistics = statisticsService.getResourceStatistics();
        return Result.success(statistics);
    }

    @ApiOperation("学习统计")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    @GetMapping("/study")
    public Result<StudyStatisticsVO> getStudyStatistics() {
        StudyStatisticsVO statistics = statisticsService.getStudyStatistics();
        return Result.success(statistics);
    }

    @ApiOperation("考试统计")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    @GetMapping("/exam")
    public Result<ExamStatisticsVO> getExamStatistics() {
        ExamStatisticsVO statistics = statisticsService.getExamStatistics();
        return Result.success(statistics);
    }

    @ApiOperation("社区统计")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    @GetMapping("/community")
    public Result<CommunityStatisticsVO> getCommunityStatistics() {
        CommunityStatisticsVO statistics = statisticsService.getCommunityStatistics();
        return Result.success(statistics);
    }
}
