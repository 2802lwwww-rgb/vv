package com.red.education.module.exam.vo;

import com.red.education.module.exam.entity.Question;
import lombok.Data;

import java.util.List;

/**
 * 试卷详情VO（用于考试）
 */
@Data
public class ExamDetailVO {

    private Long id;
    private String name;
    private String description;
    private Integer duration; // 考试时长（分钟）
    private Integer totalScore;
    private Integer passScore;
    private Integer questionCount;

    /**
     * 题目列表（不包含正确答案和解析）
     */
    private List<QuestionVO> questions;

    @Data
    public static class QuestionVO {
        private Long id;
        private String type;
        private String content;
        private String optionA;
        private String optionB;
        private String optionC;
        private String optionD;
        private Integer score;

        public static QuestionVO fromQuestion(Question question) {
            QuestionVO vo = new QuestionVO();
            vo.setId(question.getId());
            vo.setType(question.getType());
            vo.setContent(question.getContent());
            vo.setOptionA(question.getOptionA());
            vo.setOptionB(question.getOptionB());
            vo.setOptionC(question.getOptionC());
            vo.setOptionD(question.getOptionD());
            vo.setScore(question.getScore());
            return vo;
        }
    }
}
