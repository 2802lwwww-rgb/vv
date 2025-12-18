package com.red.education.module.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.common.result.Result;
import com.red.education.module.exam.dto.SubmitExamDTO;
import com.red.education.module.exam.entity.Exam;
import com.red.education.module.exam.entity.ExamScore;
import com.red.education.module.exam.service.ExamService;
import com.red.education.module.exam.vo.ExamDetailVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 在线考试Controller（用户）
 */
@Api(tags = "在线考试")
@RestController
@RequestMapping("/exam")
@Validated
public class ExamController {

    @Autowired
    private ExamService examService;

    @Autowired
    private com.red.education.module.exam.mapper.ExamScoreMapper examScoreMapper;

    @ApiOperation("获取可用试卷列表")
    @GetMapping("/list")
    public Result<Page<com.red.education.module.exam.vo.ExamVO>> listAvailableExams(
            Authentication authentication,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {

        Page<Exam> examPage = examService.listAvailableExams(current, size, keyword);

        // 转换为VO并添加用户参与状态
        Page<com.red.education.module.exam.vo.ExamVO> voPage = new Page<>();
        voPage.setCurrent(examPage.getCurrent());
        voPage.setSize(examPage.getSize());
        voPage.setTotal(examPage.getTotal());
        voPage.setPages(examPage.getPages());

        java.util.List<com.red.education.module.exam.vo.ExamVO> voList = new java.util.ArrayList<>();

        Long userId = null;
        if (authentication != null && authentication.getPrincipal() != null) {
            try {
                userId = (Long) authentication.getPrincipal();
            } catch (Exception e) {
                // 忽略，未登录用户
            }
        }

        for (Exam exam : examPage.getRecords()) {
            com.red.education.module.exam.vo.ExamVO vo = com.red.education.module.exam.vo.ExamVO.fromExam(exam);

            // 查询用户是否参与过该考试及最高分
            if (userId != null) {
                com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.red.education.module.exam.entity.ExamScore> wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
                wrapper.eq(com.red.education.module.exam.entity.ExamScore::getUserId, userId);
                wrapper.eq(com.red.education.module.exam.entity.ExamScore::getExamId, exam.getId());
                wrapper.orderByDesc(com.red.education.module.exam.entity.ExamScore::getScore);
                wrapper.last("LIMIT 1");

                com.red.education.module.exam.entity.ExamScore score = examScoreMapper.selectOne(wrapper);
                if (score != null) {
                    vo.setHasJoined(true);
                    vo.setBestScore(score.getScore());
                }
            }

            voList.add(vo);
        }

        voPage.setRecords(voList);
        return Result.success(voPage);
    }

    @ApiOperation("获取试卷详情（开始考试）")
    @GetMapping("/{examId}")
    public Result<ExamDetailVO> getExamDetail(@PathVariable Long examId) {
        ExamDetailVO examDetail = examService.getExamDetail(examId);
        return Result.success(examDetail);
    }

    @ApiOperation("提交试卷")
    @PostMapping("/submit")
    public Result<ExamScore> submitExam(
            Authentication authentication,
            @Valid @RequestBody SubmitExamDTO submitExamDTO) {
        Long userId = (Long) authentication.getPrincipal();
        ExamScore examScore = examService.submitExam(userId, submitExamDTO);
        return Result.success(examScore);
    }

    @ApiOperation("我的成绩列表")
    @GetMapping("/score/my")
    public Result<Page<ExamScore>> getMyScores(
            Authentication authentication,
            @RequestParam(required = false) Long examId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) authentication.getPrincipal();
        Page<ExamScore> page = examService.getMyScores(userId, examId, current, size);
        return Result.success(page);
    }

    @ApiOperation("成绩详情")
    @GetMapping("/score/{scoreId}")
    public Result<ExamScore> getScoreDetail(
            Authentication authentication,
            @PathVariable Long scoreId) {
        Long userId = (Long) authentication.getPrincipal();
        ExamScore examScore = examService.getScoreDetail(userId, scoreId);
        return Result.success(examScore);
    }

    @ApiOperation("获取试卷排行榜")
    @GetMapping("/{examId}/leaderboard")
    public Result<java.util.List<com.red.education.module.exam.vo.ExamLeaderboardVO>> getExamLeaderboard(
            @PathVariable Long examId,
            @RequestParam(defaultValue = "10") Integer topN) {
        java.util.List<com.red.education.module.exam.vo.ExamLeaderboardVO> leaderboard = examService
                .getExamLeaderboard(examId, topN);
        return Result.success(leaderboard);
    }
}
