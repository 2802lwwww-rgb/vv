package com.red.education.module.course.controller;

import com.red.education.common.result.Result;
import com.red.education.module.course.dto.StudyRecordDTO;
import com.red.education.module.course.entity.StudyRecord;
import com.red.education.module.course.service.StudyRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 学习记录Controller
 */
@Api(tags = "学习记录管理")
@RestController
@RequestMapping("/study")
@Validated
public class StudyRecordController {

    @Autowired
    private StudyRecordService studyRecordService;

    @ApiOperation("更新学习进度")
    @PostMapping("/progress")
    public Result<Void> updateProgress(Authentication authentication,
            @Valid @RequestBody StudyRecordDTO recordDTO) {
        Long userId = (Long) authentication.getPrincipal();
        studyRecordService.updateProgress(userId, recordDTO);
        return Result.success("进度更新成功");
    }

    @ApiOperation("获取课程学习记录")
    @GetMapping("/record/{courseId}")
    public Result<StudyRecord> getRecord(Authentication authentication,
            @PathVariable Long courseId) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(studyRecordService.getUserStudyRecord(userId, courseId));
    }

    @ApiOperation("获取我的学习历史")
    @GetMapping("/history")
    public Result<List<com.red.education.module.course.vo.StudyHistoryVO>> getHistory(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(studyRecordService.getUserStudyHistory(userId));
    }

    @ApiOperation("获取学习报告")
    @GetMapping("/report")
    public Result<com.red.education.module.course.vo.StudyReportVO> getStudyReport(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        com.red.education.module.course.vo.StudyReportVO report = studyRecordService.getStudyReport(userId);
        return Result.success(report);
    }
}
