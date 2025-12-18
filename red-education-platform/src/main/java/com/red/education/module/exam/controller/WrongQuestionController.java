package com.red.education.module.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.common.result.Result;
import com.red.education.module.exam.service.WrongQuestionService;
import com.red.education.module.exam.vo.WrongQuestionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 错题本Controller
 */
@Api(tags = "错题本")
@RestController
@RequestMapping("/exam/wrong")
@Validated
public class WrongQuestionController {

    @Autowired
    private WrongQuestionService wrongQuestionService;

    @ApiOperation("我的错题列表")
    @GetMapping("/list")
    public Result<Page<WrongQuestionVO>> getMyWrongQuestions(
            Authentication authentication,
            @RequestParam(required = false) Integer isMastered,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) authentication.getPrincipal();
        Page<WrongQuestionVO> page = wrongQuestionService.getMyWrongQuestions(userId, isMastered, current, size);
        return Result.success(page);
    }

    @ApiOperation("标记已掌握")
    @PutMapping("/{id}/master")
    public Result<Void> markAsMastered(
            Authentication authentication,
            @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        wrongQuestionService.markAsMastered(userId, id);
        return Result.<Void>success("已标记为掌握");
    }

    @ApiOperation("删除错题记录")
    @DeleteMapping("/{id}")
    public Result<Void> deleteWrongQuestion(
            Authentication authentication,
            @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        wrongQuestionService.deleteWrongQuestion(userId, id);
        return Result.<Void>success("删除成功");
    }

    @ApiOperation("重做错题")
    @PostMapping("/redo")
    public Result<Boolean> redoQuestion(
            Authentication authentication,
            @RequestBody @javax.validation.Valid com.red.education.module.exam.dto.RedoQuestionDTO redoQuestionDTO) {
        Long userId = (Long) authentication.getPrincipal();
        boolean isCorrect = wrongQuestionService.redoQuestion(userId, redoQuestionDTO);
        return Result.success(isCorrect);
    }
}
