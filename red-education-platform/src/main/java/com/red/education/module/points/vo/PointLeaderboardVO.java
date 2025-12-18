package com.red.education.module.points.vo;

import lombok.Data;

/**
 * 积分排行榜VO
 */
@Data
public class PointLeaderboardVO {

    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private Integer points;
    private Integer rank;
}
