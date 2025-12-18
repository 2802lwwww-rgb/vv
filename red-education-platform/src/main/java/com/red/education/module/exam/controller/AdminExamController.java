package com.red.education.module.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.common.result.Result;
import com.red.education.module.exam.dto.ExamDTO;
import com.red.education.module.exam.entity.Exam;
import com.red.education.module.exam.service.ExamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 试卷管理Controller（管理员）
 */
@Api(tags = "试卷管理")
@RestController
@RequestMapping("/admin/exams")
@Validated
public class AdminExamController {

    @Autowired
    private ExamService examService;

    @ApiOperation("查询试卷列表")
    @GetMapping
    @PreAuthorize("hasRole('SYS_ADMIN')")
    public Result<Page<Exam>> listExams(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        // 使用 listAdminExams 查询所有试卷，包括禁用的
        Page<Exam> page = examService.listAdminExams(current, size);
        return Result.success(page);
    }

    @ApiOperation("创建试卷")
    @PostMapping
    @PreAuthorize("hasRole('SYS_ADMIN')")
    public Result<Void> createExam(@Valid @RequestBody ExamDTO examDTO) {
        examService.createExam(examDTO);
        return Result.<Void>success("创建成功");
    }

    @ApiOperation("更新试卷")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    public Result<Void> updateExam(@PathVariable Long id, @Valid @RequestBody ExamDTO examDTO) {
        examService.updateExam(id, examDTO);
        return Result.<Void>success("更新成功");
    }

    @ApiOperation("删除试卷")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    public Result<Void> deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return Result.<Void>success("删除成功");
    }

    @ApiOperation("更新试卷状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    public Result<Void> updateExamStatus(@PathVariable Long id, @RequestParam Integer status) {
        examService.updateExamStatus(id, status);
        return Result.<Void>success("状态更新成功");
    }

    @ApiOperation("获取试卷题目列表")
    @GetMapping("/{id}/questions")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    public Result<List<Long>> getExamQuestions(@PathVariable Long id) {
        List<Long> questionIds = examService.getExamQuestionIds(id);
        return Result.success(questionIds);
    }

    @ApiOperation("设置试卷题目")
    @PutMapping("/{id}/questions")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    public Result<Void> setExamQuestions(@PathVariable Long id, @RequestBody List<Long> questionIds) {
        examService.setExamQuestions(id, questionIds);
        return Result.<Void>success("题目设置成功");
    }
}
