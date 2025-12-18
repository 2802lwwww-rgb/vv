package com.red.education.module.exam.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 考试排行榜VO
 */
@Data
public class ExamLeaderboardVO {

    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private Integer score;
    private Integer duration; // 用时（秒）
    private LocalDateTime submitTime;
    private Integer rank;
}
