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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 题目管理Controller（管理员）
 */
@Api(tags = "题目管理")
@RestController
@RequestMapping("/admin/questions")
@Validated
public class AdminQuestionController {

    @Autowired
    private QuestionService questionService;

    @ApiOperation("查询题目列表")
    @GetMapping
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

    @ApiOperation("创建题目")
    @PostMapping
    @PreAuthorize("hasRole('SYS_ADMIN')")
    public Result<Void> createQuestion(@Valid @RequestBody QuestionDTO questionDTO) {
        questionService.addQuestion(questionDTO);
        return Result.<Void>success("创建成功");
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

    @ApiOperation("批量导入题目")
    @PostMapping("/import")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    public Result<String> importQuestions(@RequestParam("file") MultipartFile file) {
        try {
            int count = questionService.batchImport(file);
            return Result.success("成功导入 " + count + " 道题目");
        } catch (Exception e) {
            return Result.fail("导入失败：" + e.getMessage());
        }
    }

    @ApiOperation("下载导入模板")
    @GetMapping("/template")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    public void downloadTemplate(HttpServletResponse response) throws Exception {
        questionService.downloadTemplate(response);
    }
}
