package com.red.education.module.exam.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.common.exception.BusinessException;
import com.red.education.module.exam.dto.ExamDTO;
import com.red.education.module.exam.dto.SubmitExamDTO;
import com.red.education.module.exam.entity.*;
import com.red.education.module.exam.mapper.*;
import com.red.education.module.exam.service.ExamService;
import com.red.education.module.exam.vo.ExamDetailVO;
import com.red.education.module.points.service.PointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 考试Service实现类
 */
@Slf4j
@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private ExamQuestionMapper examQuestionMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private ExamScoreMapper examScoreMapper;

    @Autowired
    private WrongQuestionMapper wrongQuestionMapper;

    @Autowired
    private PointService pointService;

    @Override
    public Page<Exam> listAvailableExams(Integer current, Integer size) {
        return listAvailableExams(current, size, null);
    }

    @Override
    public Page<Exam> listAvailableExams(Integer current, Integer size, String keyword) {
        Page<Exam> page = new Page<>(current, size);
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Exam::getStatus, 1); // 只查询启用的试卷

        // 关键词搜索 - 搜索名称和描述
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Exam::getName, keyword)
                    .or().like(Exam::getDescription, keyword));
        }

        wrapper.orderByDesc(Exam::getCreateTime);
        return examMapper.selectPage(page, wrapper);
    }

    @Override
    public Page<Exam> listAdminExams(Integer current, Integer size) {
        Page<Exam> page = new Page<>(current, size);
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        // 管理员查询所有状态的试卷，不需要 status=1 的过滤条件
        wrapper.orderByDesc(Exam::getCreateTime);
        return examMapper.selectPage(page, wrapper);
    }

    @Override
    public ExamDetailVO getExamDetail(Long examId) {
        // 获取试卷信息
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("试卷不存在");
        }
        if (exam.getStatus() == 0) {
            throw new BusinessException("试卷已禁用");
        }

        // 获取试卷题目
        LambdaQueryWrapper<ExamQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestion::getExamId, examId);
        wrapper.orderByAsc(ExamQuestion::getSort);
        List<ExamQuestion> examQuestions = examQuestionMapper.selectList(wrapper);

        List<Long> questionIds = examQuestions.stream()
                .map(ExamQuestion::getQuestionId)
                .collect(Collectors.toList());

        List<Question> questions = questionMapper.selectBatchIds(questionIds);

        // 组装VO（不包含正确答案）
        ExamDetailVO vo = new ExamDetailVO();
        vo.setId(exam.getId());
        vo.setName(exam.getName());
        vo.setDescription(exam.getDescription());
        vo.setDuration(exam.getDuration());
        vo.setTotalScore(exam.getTotalScore());
        vo.setPassScore(exam.getPassScore());
        vo.setQuestionCount(exam.getQuestionCount());
        vo.setQuestions(questions.stream()
                .map(ExamDetailVO.QuestionVO::fromQuestion)
                .collect(Collectors.toList()));

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamScore submitExam(Long userId, SubmitExamDTO submitExamDTO) {
        Long examId = submitExamDTO.getExamId();
        Map<Long, String> userAnswers = submitExamDTO.getAnswers();

        // 获取试卷信息
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("试卷不存在");
        }

        // 获取试卷所有题目
        LambdaQueryWrapper<ExamQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestion::getExamId, examId);
        List<ExamQuestion> examQuestions = examQuestionMapper.selectList(wrapper);

        List<Long> questionIds = examQuestions.stream()
                .map(ExamQuestion::getQuestionId)
                .collect(Collectors.toList());
        List<Question> questions = questionMapper.selectBatchIds(questionIds);

        // 自动批改
        int totalScore = 0;
        int correctCount = 0;
        int wrongCount = 0;
        List<WrongQuestion> wrongQuestions = new ArrayList<>();

        for (Question question : questions) {
            String userAnswer = userAnswers.get(question.getId());
            String correctAnswer = question.getCorrectAnswer();

            if (correctAnswer.equals(userAnswer)) {
                // 答对
                totalScore += question.getScore();
                correctCount++;
            } else {
                // 答错，收集到错题本
                wrongCount++;
                WrongQuestion wrongQuestion = new WrongQuestion();
                wrongQuestion.setUserId(userId);
                wrongQuestion.setQuestionId(question.getId());
                wrongQuestion.setUserAnswer(userAnswer);
                wrongQuestion.setIsMastered(0);
                wrongQuestions.add(wrongQuestion);
            }
        }

        // 获取用户在本次提交前的历史最高分，用于计算积分奖励
        Integer bestScore = getBestScore(userId, examId);

        // 保存成绩
        ExamScore examScore = new ExamScore();
        examScore.setUserId(userId);
        examScore.setExamId(examId);
        examScore.setScore(totalScore);
        examScore.setTotalScore(exam.getTotalScore());
        examScore.setPass(totalScore >= exam.getPassScore() ? 1 : 0);
        examScore.setCorrectCount(correctCount);
        examScore.setWrongCount(wrongCount);
        examScore.setDuration(submitExamDTO.getDuration());
        examScore.setAnswers(JSONUtil.toJsonStr(userAnswers));
        examScoreMapper.insert(examScore);

        // 保存错题
        if (!wrongQuestions.isEmpty()) {
            for (WrongQuestion wq : wrongQuestions) {
                wq.setExamScoreId(examScore.getId());
                wrongQuestionMapper.insert(wq);
            }
        }

        // 积分奖励
        awardPoints(userId, totalScore, exam.getTotalScore(), examId, bestScore);

        log.info("用户{}提交试卷{}，得分：{}/{}", userId, examId, totalScore, exam.getTotalScore());
        return examScore;
    }

    /**
     * 积分奖励逻辑 - 只有超过历史最好成绩才给积分差值
     * 
     * @param bestScore 历史最高分，null表示第一次考试
     */
    private void awardPoints(Long userId, int score, int totalScore, Long examId, Integer bestScore) {
        // 计算本次成绩对应的积分
        int currentPoints = calculatePoints(score, totalScore);

        if (currentPoints == 0) {
            return;
        }

        // 检查用户是否已经获得过该试卷的积分（兼容处理：之前因bug未获得积分的记录）
        // 传"EXAM%"表示匹配所有考试相关的积分类型，避免与其他业务（如帖子）的ID冲突
        boolean hasObtained = pointService.hasObtainedPoints(userId, "EXAM%", examId);

        // 如果之前没有获得过积分（无论是第一次考，还是之前考过但没给分），直接发放当前积分
        if (!hasObtained) {
            String type = getPointType(score, totalScore);
            String description = getPointDescription(score, totalScore);
            pointService.addPoints(userId, currentPoints, type, examId, description);
            log.info("用户{}考试{}获得积分：{} (首次或补发)", userId, examId, currentPoints);
            return;
        }

        // 如果之前已经获得过积分，则通过比较历史最高分来计算差值
        if (bestScore == null) {
            // 理论上如果 hasObtained 为 true，bestScore 不应为 null，除非数据不一致
            return;
        }

        // 计算历史最好成绩对应的积分
        int bestPoints = calculatePoints(bestScore, totalScore);

        // 只有本次成绩对应的积分大于历史最好成绩的积分，才发放差值
        int pointsDiff = currentPoints - bestPoints;
        if (pointsDiff > 0) {
            String type = getPointType(score, totalScore);
            String description = String.format("考试成绩提升奖励（%d分→%d分）", bestScore, score);
            pointService.addPoints(userId, pointsDiff, type, examId, description);
            log.info("用户{}考试{}成绩提升，获得积分差值：{} ({}分→{}分)",
                    userId, examId, pointsDiff, bestScore, score);
        } else if (pointsDiff == 0) {
            log.info("用户{}考试{}成绩未提升到下一档位，不发放积分", userId, examId);
        } else {
            log.info("用户{}考试{}成绩未超过历史最好成绩，不发放积分", userId, examId);
        }
    }

    /**
     * 根据成绩计算应获得的积分
     */
    private int calculatePoints(int score, int totalScore) {
        if (score == totalScore) {
            return 50; // 满分
        } else if (score >= totalScore * 0.9) {
            return 30; // 优秀
        } else if (score >= totalScore * 0.6) {
            return 15; // 及格
        }
        return 0; // 不及格
    }

    /**
     * 获取积分类型
     */
    private String getPointType(int score, int totalScore) {
        if (score == totalScore) {
            return "EXAM_PERFECT";
        } else if (score >= totalScore * 0.9) {
            return "EXAM_EXCELLENT";
        } else if (score >= totalScore * 0.6) {
            return "EXAM_PASS";
        }
        return "EXAM";
    }

    /**
     * 获取积分描述
     */
    private String getPointDescription(int score, int totalScore) {
        if (score == totalScore) {
            return "考试满分奖励";
        } else if (score >= totalScore * 0.9) {
            return "考试优秀奖励";
        } else if (score >= totalScore * 0.6) {
            return "考试及格奖励";
        }
        return "考试奖励";
    }

    /**
     * 获取用户在指定考试的历史最高分
     */
    private Integer getBestScore(Long userId, Long examId) {
        LambdaQueryWrapper<ExamScore> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamScore::getUserId, userId);
        wrapper.eq(ExamScore::getExamId, examId);
        wrapper.orderByDesc(ExamScore::getScore);
        wrapper.last("LIMIT 1");

        ExamScore bestExamScore = examScoreMapper.selectOne(wrapper);
        return bestExamScore != null ? bestExamScore.getScore() : null;
    }

    @Override
    public Page<ExamScore> getMyScores(Long userId, Long examId, Integer current, Integer size) {
        Page<ExamScore> page = new Page<>(current, size);
        LambdaQueryWrapper<ExamScore> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamScore::getUserId, userId);
        // 如果指定了examId，则只查询该考试的成绩
        if (examId != null) {
            wrapper.eq(ExamScore::getExamId, examId);
        }
        wrapper.orderByDesc(ExamScore::getCreateTime);
        return examScoreMapper.selectPage(page, wrapper);
    }

    @Override
    public ExamScore getScoreDetail(Long userId, Long scoreId) {
        ExamScore examScore = examScoreMapper.selectById(scoreId);
        if (examScore == null || !examScore.getUserId().equals(userId)) {
            throw new BusinessException("成绩不存在");
        }
        return examScore;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createExam(ExamDTO examDTO) {
        // 1. 保存试卷基本信息
        Exam exam = new Exam();
        BeanUtil.copyProperties(examDTO, exam);
        exam.setStatus(1); // 默认启用

        List<Long> questionIds = new ArrayList<>();

        if (exam.getType() == 2) {
            // 固定试卷：可以先创建，之后再通过"题目"按钮添加题目
            if (examDTO.getQuestionIds() != null && !examDTO.getQuestionIds().isEmpty()) {
                questionIds = examDTO.getQuestionIds();
                exam.setQuestionCount(questionIds.size());
            } else {
                // 如果没有提供题目ID，使用前端传入的题目数量或默认值
                exam.setQuestionCount(examDTO.getQuestionCount() != null ? examDTO.getQuestionCount() : 0);
            }

        } else if (exam.getType() == 1) {
            // 随机组卷：根据配置随机抽取题目
            if (examDTO.getRandomConfig() == null) {
                throw new BusinessException("随机试卷必须提供随机配置");
            }
            questionIds = selectRandomQuestions(examDTO.getRandomConfig());
            exam.setQuestionCount(questionIds.size());

        } else {
            throw new BusinessException("不支持的试卷类型");
        }

        examMapper.insert(exam);

        // 2. 保存题目关联（如果有题目）
        if (!questionIds.isEmpty()) {
            saveExamQuestions(exam.getId(), questionIds);
            updateExamStats(exam.getId(), questionIds);
        } else {
            // 如果是固定试卷初始无题目，分数置0
            if (exam.getType() == 2) {
                updateExamStats(exam.getId(), new ArrayList<>());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateExam(Long id, ExamDTO examDTO) {
        Exam exam = examMapper.selectById(id);
        if (exam == null) {
            throw new BusinessException("试卷不存在");
        }

        // 1. 更新基本信息
        BeanUtil.copyProperties(examDTO, exam);
        exam.setId(id);

        // 如果是随机试卷，直接更新
        if (exam.getType() != 2) {
            examMapper.updateById(exam);
            return;
        }

        // 如果是固定试卷
        if (examDTO.getQuestionIds() != null) {
            // 更新了题目
            exam.setQuestionCount(examDTO.getQuestionIds().size());
            examMapper.updateById(exam); // 先保存基本信息

            // 重置题目关联
            LambdaQueryWrapper<ExamQuestion> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ExamQuestion::getExamId, id);
            examQuestionMapper.delete(wrapper);

            saveExamQuestions(id, examDTO.getQuestionIds());
            updateExamStats(id, examDTO.getQuestionIds());
        } else {
            // 没更新题目，但也需要保存基本信息，并且重新计算分数以防前端传来的分数不准确
            examMapper.updateById(exam);
            updateExamStats(id, getExamQuestionIds(id));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteExam(Long id) {
        // 删除试卷
        examMapper.deleteById(id);
        // 删除关联题目
        LambdaQueryWrapper<ExamQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestion::getExamId, id);
        examQuestionMapper.delete(wrapper);
    }

    @Override
    public void updateExamStatus(Long id, Integer status) {
        Exam exam = new Exam();
        exam.setId(id);
        exam.setStatus(status);
        examMapper.updateById(exam);
    }

    /**
     * 根据随机配置选择题目
     */
    private List<Long> selectRandomQuestions(ExamDTO.RandomConfig config) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();

        // 按分类筛选
        if (config.getCategory() != null && !config.getCategory().isEmpty()) {
            wrapper.eq(Question::getCategory, config.getCategory());
        }

        // 按难度筛选
        if (config.getDifficulty() != null) {
            wrapper.eq(Question::getDifficulty, config.getDifficulty());
        }

        // 按题型筛选
        if (config.getQuestionType() != null && !config.getQuestionType().isEmpty()) {
            wrapper.eq(Question::getType, config.getQuestionType());
        }

        // 随机排序并限制数量
        wrapper.last("ORDER BY RAND() LIMIT " + config.getQuestionCount());

        List<Question> questions = questionMapper.selectList(wrapper);

        if (questions.isEmpty()) {
            throw new BusinessException("没有符合条件的题目，请调整筛选条件");
        }

        if (questions.size() < config.getQuestionCount()) {
            throw new BusinessException(
                    String.format("符合条件的题目不足，需要%d道，实际只有%d道",
                            config.getQuestionCount(), questions.size()));
        }

        return questions.stream()
                .map(Question::getId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getExamQuestionIds(Long examId) {
        LambdaQueryWrapper<ExamQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestion::getExamId, examId);
        wrapper.orderByAsc(ExamQuestion::getSort);
        List<ExamQuestion> examQuestions = examQuestionMapper.selectList(wrapper);
        return examQuestions.stream().map(ExamQuestion::getQuestionId).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void setExamQuestions(Long examId, List<Long> questionIds) {
        // 先删除原有关联
        LambdaQueryWrapper<ExamQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestion::getExamId, examId);
        examQuestionMapper.delete(wrapper);

        // 再插入新关联
        saveExamQuestions(examId, questionIds);

        // 更新统计信息
        updateExamStats(examId, questionIds);
    }

    private void saveExamQuestions(Long examId, List<Long> questionIds) {
        for (int i = 0; i < questionIds.size(); i++) {
            ExamQuestion examQuestion = new ExamQuestion();
            examQuestion.setExamId(examId);
            examQuestion.setQuestionId(questionIds.get(i));
            examQuestion.setSort(i + 1);
            examQuestionMapper.insert(examQuestion);
        }
    }

    /**
     * 更新试卷统计信息（题目数、总分、及格分）
     */
    private void updateExamStats(Long examId, List<Long> questionIds) {
        Exam exam = examMapper.selectById(examId);
        if (exam != null) {
            exam.setQuestionCount(questionIds.size());

            if (questionIds.isEmpty()) {
                exam.setTotalScore(0);
                exam.setPassScore(0);
            } else {
                List<Question> questions = questionMapper.selectBatchIds(questionIds);
                int totalScore = questions.stream().mapToInt(Question::getScore).sum();
                exam.setTotalScore(totalScore);
                // 及格分默认为总分的60%
                exam.setPassScore((int) Math.ceil(totalScore * 0.6));
            }
            examMapper.updateById(exam);
        }
    }

    @Autowired
    private com.red.education.module.user.mapper.UserMapper userMapper;

    @Override
    public List<com.red.education.module.exam.vo.ExamLeaderboardVO> getExamLeaderboard(Long examId, Integer topN) {
        // 查询前N名成绩
        LambdaQueryWrapper<ExamScore> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamScore::getExamId, examId);
        wrapper.orderByDesc(ExamScore::getScore);
        wrapper.orderByAsc(ExamScore::getDuration);
        wrapper.last("LIMIT " + topN);

        List<ExamScore> scores = examScoreMapper.selectList(wrapper);

        if (scores.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        // 批量查询用户信息
        List<Long> userIds = scores.stream().map(ExamScore::getUserId).collect(Collectors.toList());
        List<com.red.education.module.user.entity.User> users = userMapper.selectBatchIds(userIds);
        Map<Long, com.red.education.module.user.entity.User> userMap = users.stream()
                .collect(Collectors.toMap(com.red.education.module.user.entity.User::getId, u -> u));

        // 组装VO
        List<com.red.education.module.exam.vo.ExamLeaderboardVO> result = new java.util.ArrayList<>();
        for (int i = 0; i < scores.size(); i++) {
            ExamScore score = scores.get(i);
            com.red.education.module.user.entity.User user = userMap.get(score.getUserId());

            com.red.education.module.exam.vo.ExamLeaderboardVO vo = new com.red.education.module.exam.vo.ExamLeaderboardVO();
            vo.setUserId(score.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setNickname(user.getNickname());
                vo.setAvatar(user.getAvatar());
            } else {
                vo.setUsername("未知用户");
            }
            vo.setScore(score.getScore());
            vo.setDuration(score.getDuration());
            vo.setSubmitTime(score.getCreateTime());
            vo.setRank(i + 1);
            result.add(vo);
        }

        return result;
    }
}
