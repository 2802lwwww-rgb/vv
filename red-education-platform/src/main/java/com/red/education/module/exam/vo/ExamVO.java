package com.red.education.module.exam.vo;

import com.red.education.module.exam.entity.Exam;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 考试列表VO（包含用户参与状态）
 */
@Data
public class ExamVO {

    private Long id;

    private String name;

    private String description;

    private Integer duration;

    private Integer totalScore;

    private Integer passScore;

    private Integer questionCount;

    private Integer status;

    private Integer type;

    private LocalDateTime createTime;

    /**
     * 用户是否已参加过该考试
     */
    private Boolean hasJoined;

    /**
     * 用户最高得分（如有）
     */
    private Integer bestScore;

    public static ExamVO fromExam(Exam exam) {
        ExamVO vo = new ExamVO();
        vo.setId(exam.getId());
        vo.setName(exam.getName());
        vo.setDescription(exam.getDescription());
        vo.setDuration(exam.getDuration());
        vo.setTotalScore(exam.getTotalScore());
        vo.setPassScore(exam.getPassScore());
        vo.setQuestionCount(exam.getQuestionCount());
        vo.setStatus(exam.getStatus());
        vo.setType(exam.getType());
        vo.setCreateTime(exam.getCreateTime());
        vo.setHasJoined(false);
        return vo;
    }
}
