package com.red.education.module.exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.module.exam.dto.ExamDTO;
import com.red.education.module.exam.dto.SubmitExamDTO;
import com.red.education.module.exam.entity.Exam;
import com.red.education.module.exam.entity.ExamScore;
import com.red.education.module.exam.vo.ExamDetailVO;
import com.red.education.module.exam.vo.ExamLeaderboardVO;
import java.util.List;

/**
 * 考试Service接口
 */
public interface ExamService {

    /**
     * 获取可用试卷列表
     */
    Page<Exam> listAvailableExams(Integer current, Integer size);

    /**
     * 获取可用试卷列表（支持关键词搜索）
     */
    Page<Exam> listAvailableExams(Integer current, Integer size, String keyword);

    /**
     * 获取所有试卷列表（管理员）
     */
    Page<Exam> listAdminExams(Integer current, Integer size);

    /**
     * 获取试卷详情（用于考试）
     */
    ExamDetailVO getExamDetail(Long examId);

    /**
     * 提交试卷并自动评分
     */
    ExamScore submitExam(Long userId, SubmitExamDTO submitExamDTO);

    /**
     * 获取我的成绩列表
     */
    Page<ExamScore> getMyScores(Long userId, Long examId, Integer current, Integer size);

    /**
     * 获取成绩详情
     */
    ExamScore getScoreDetail(Long userId, Long scoreId);

    /**
     * 创建试卷
     */
    void createExam(ExamDTO examDTO);

    /**
     * 更新试卷
     */
    void updateExam(Long id, ExamDTO examDTO);

    /**
     * 删除试卷
     */
    void deleteExam(Long id);

    /**
     * 更新试卷状态
     */
    void updateExamStatus(Long id, Integer status);

    /**
     * 获取试卷排行榜
     */
    List<ExamLeaderboardVO> getExamLeaderboard(Long examId, Integer topN);

    /**
     * 获取试卷关联的题目ID列表
     */
    List<Long> getExamQuestionIds(Long examId);

    /**
     * 设置试卷题目
     */
    void setExamQuestions(Long examId, List<Long> questionIds);
}
