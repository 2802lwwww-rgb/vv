package com.red.education.module.exam.vo;

import com.red.education.module.exam.entity.Question;
import com.red.education.module.exam.entity.WrongQuestion;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 错题本VO
 */
@Data
public class WrongQuestionVO {

    private Long id;
    private Long questionId;
    private String userAnswer;
    private Integer isMastered;
    private LocalDateTime createTime;

    // 题目详情
    private String type;
    private String content;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private String explanation;
    private Integer difficulty;
    private Integer score;
    private Integer wrongCount; // 错误次数

    public static WrongQuestionVO from(WrongQuestion wrongQuestion, Question question) {
        WrongQuestionVO vo = new WrongQuestionVO();
        vo.setId(wrongQuestion.getId());
        vo.setQuestionId(wrongQuestion.getQuestionId());
        vo.setUserAnswer(wrongQuestion.getUserAnswer());
        vo.setIsMastered(wrongQuestion.getIsMastered());
        vo.setCreateTime(wrongQuestion.getCreateTime());

        if (question != null) {
            vo.setType(question.getType());
            vo.setContent(question.getContent());
            vo.setOptionA(question.getOptionA());
            vo.setOptionB(question.getOptionB());
            vo.setOptionC(question.getOptionC());
            vo.setOptionD(question.getOptionD());
            vo.setCorrectAnswer(question.getCorrectAnswer());
            vo.setExplanation(question.getExplanation());
            vo.setDifficulty(question.getDifficulty());
            vo.setScore(question.getScore());
        }

        return vo;
    }
}
