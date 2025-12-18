package com.red.education.module.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.common.result.Result;
import com.red.education.module.exam.dto.QuestionDTO;
import com.red.education.module.exam.entity.Question;
import com.red.education.module.exam.service.QuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 题目管理Controller（管理员）
 */
@Api(tags = "题库管理")
@RestController
@RequestMapping("/admin/question")
@Validated
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @ApiOperation("查询题目列表")
    @GetMapping("/list")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    public Result<Page<Question>> listQuestions(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer difficulty,
            @RequestParam(required = false) String category) {
        Page<Question> page = questionService.listQuestions(current, size, type, difficulty, category);
        return Result.success(page);
    }

    @ApiOperation("查询题目详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    public Result<Question> getQuestionById(@PathVariable Long id) {
        Question question = questionService.getQuestionById(id);
        return Result.success(question);
    }

    @ApiOperation("添加题目")
    @PostMapping
    @PreAuthorize("hasRole('SYS_ADMIN')")
    public Result<Void> addQuestion(@Valid @RequestBody QuestionDTO questionDTO) {
        questionService.addQuestion(questionDTO);
        return Result.<Void>success("添加成功");
    }

    @ApiOperation("更新题目")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    public Result<Void> updateQuestion(@PathVariable Long id, @Valid @RequestBody QuestionDTO questionDTO) {
        questionService.updateQuestion(id, questionDTO);
        return Result.<Void>success("更新成功");
    }

    @ApiOperation("删除题目")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    public Result<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return Result.<Void>success("删除成功");
    }
}
