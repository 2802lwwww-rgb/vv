package com.red.education.module.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.common.exception.BusinessException;
import com.red.education.module.exam.dto.RedoQuestionDTO;
import com.red.education.module.exam.entity.Question;
import com.red.education.module.exam.entity.WrongQuestion;
import com.red.education.module.exam.mapper.QuestionMapper;
import com.red.education.module.exam.mapper.WrongQuestionMapper;
import com.red.education.module.exam.service.WrongQuestionService;
import com.red.education.module.exam.vo.WrongQuestionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 错题本Service实现类
 */
@Slf4j
@Service
public class WrongQuestionServiceImpl implements WrongQuestionService {

    @Autowired
    private WrongQuestionMapper wrongQuestionMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public Page<WrongQuestionVO> getMyWrongQuestions(Long userId, Integer isMastered, Integer current, Integer size) {
        // 1. 查询所有错题记录
        LambdaQueryWrapper<WrongQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WrongQuestion::getUserId, userId);

        // 如果指定了掌握状态，则按状态筛选；如果不指定（null），则查询所有（包含已掌握和未掌握）
        if (isMastered != null) {
            wrapper.eq(WrongQuestion::getIsMastered, isMastered);
        }
        wrapper.orderByDesc(WrongQuestion::getCreateTime);

        List<WrongQuestion> allWrongQuestions = wrongQuestionMapper.selectList(wrapper);

        // 2. 按 questionId 分组，统计错误次数
        Map<Long, List<WrongQuestion>> groupedByQuestion = allWrongQuestions.stream()
                .collect(Collectors.groupingBy(WrongQuestion::getQuestionId));

        // 3. 获取去重后的题目列表（每道题取最新的一条记录）
        List<WrongQuestion> uniqueWrongQuestions = groupedByQuestion.values().stream()
                .map(list -> list.get(0)) // 取每组的第一条（最新的）
                .collect(Collectors.toList());

        // 4. 手动分页
        int start = (current - 1) * size;
        int end = Math.min(start + size, uniqueWrongQuestions.size());
        List<WrongQuestion> pagedWrongQuestions = start < uniqueWrongQuestions.size()
                ? uniqueWrongQuestions.subList(start, end)
                : List.of();

        // 5. 批量查询题目详情
        Set<Long> questionIds = pagedWrongQuestions.stream()
                .map(WrongQuestion::getQuestionId)
                .collect(Collectors.toSet());

        Map<Long, Question> questionMap;
        if (!questionIds.isEmpty()) {
            List<Question> questions = questionMapper.selectBatchIds(questionIds);
            questionMap = questions.stream()
                    .collect(Collectors.toMap(Question::getId, q -> q));
        } else {
            questionMap = Map.of();
        }

        // 6. 转换为VO，并设置错误次数
        List<WrongQuestionVO> voList = pagedWrongQuestions.stream()
                .map(wq -> {
                    WrongQuestionVO vo = WrongQuestionVO.from(wq, questionMap.get(wq.getQuestionId()));
                    // 设置该题目的错误次数
                    vo.setWrongCount(groupedByQuestion.get(wq.getQuestionId()).size());
                    return vo;
                })
                .collect(Collectors.toList());

        // 7. 构建分页结果
        Page<WrongQuestionVO> resultPage = new Page<>(current, size, uniqueWrongQuestions.size());
        resultPage.setRecords(voList);

        return resultPage;
    }

    @Override
    public void markAsMastered(Long userId, Long id) {
        WrongQuestion wrongQuestion = wrongQuestionMapper.selectById(id);
        if (wrongQuestion == null || !wrongQuestion.getUserId().equals(userId)) {
            throw new BusinessException("错题记录不存在");
        }

        // 标记该用户所有关于此题目的错题记录为已掌握
        LambdaQueryWrapper<WrongQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WrongQuestion::getUserId, userId);
        wrapper.eq(WrongQuestion::getQuestionId, wrongQuestion.getQuestionId());
        wrapper.eq(WrongQuestion::getIsMastered, 0);

        List<WrongQuestion> wrongQuestions = wrongQuestionMapper.selectList(wrapper);
        for (WrongQuestion wq : wrongQuestions) {
            wq.setIsMastered(1);
            wrongQuestionMapper.updateById(wq);
        }
    }

    @Override
    public void deleteWrongQuestion(Long userId, Long id) {
        WrongQuestion wrongQuestion = wrongQuestionMapper.selectById(id);
        if (wrongQuestion == null || !wrongQuestion.getUserId().equals(userId)) {
            throw new BusinessException("错题记录不存在");
        }

        // 删除该用户所有关于此题目的错题记录
        LambdaQueryWrapper<WrongQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WrongQuestion::getUserId, userId);
        wrapper.eq(WrongQuestion::getQuestionId, wrongQuestion.getQuestionId());

        wrongQuestionMapper.delete(wrapper);
    }

    @Override
    public boolean redoQuestion(Long userId, RedoQuestionDTO redoQuestionDTO) {
        // 1. 查询错题记录
        LambdaQueryWrapper<WrongQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WrongQuestion::getUserId, userId);
        wrapper.eq(WrongQuestion::getQuestionId, redoQuestionDTO.getQuestionId());
        wrapper.eq(WrongQuestion::getIsMastered, 0);
        WrongQuestion wrongQuestion = wrongQuestionMapper.selectOne(wrapper);

        if (wrongQuestion == null) {
            throw new BusinessException("该题目未在错题本中或已掌握");
        }

        // 2. 查询题目正确答案
        Question question = questionMapper.selectById(redoQuestionDTO.getQuestionId());
        if (question == null) {
            throw new BusinessException("题目不存在");
        }

        // 3. 比对答案
        boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase(redoQuestionDTO.getAnswer());

        // 4. 如果回答正确，自动标记为已掌握
        if (isCorrect) {
            wrongQuestion.setIsMastered(1);
            wrongQuestionMapper.updateById(wrongQuestion);
        }

        return isCorrect;
    }
}
