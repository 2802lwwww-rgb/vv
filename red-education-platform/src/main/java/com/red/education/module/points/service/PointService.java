package com.red.education.module.points.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.module.points.entity.PointRecord;
import com.red.education.module.points.vo.PointLeaderboardVO;
import java.util.List;

/**
 * 积分服务接口
 */
public interface PointService {

    /**
     * 增加积分
     *
     * @param userId      用户ID
     * @param points      积分数量（正数为增加，负数为减少）
     * @param type        积分类型
     * @param relatedId   关联ID（课程ID、试卷ID等）
     * @param description 描述
     */
    void addPoints(Long userId, Integer points, String type, Long relatedId, String description);

    /**
     * 获取积分排行榜
     * 
     * @param topN 前N名
     */
    List<PointLeaderboardVO> getLeaderboard(Integer topN);

    /**
     * 获取用户积分记录
     * 
     * @param userId  用户ID
     * @param current 当前页
     * @param size    每页大小
     * @param type    积分类型（可选）
     */
    Page<PointRecord> getPointRecords(Long userId, Integer current, Integer size, String type);

    /**
     * 检查是否已获得过关联积分
     *
     * @param userId    用户ID
     * @param type      积分类型
     * @param relatedId 关联ID
     * @return true 已获得，false 未获得
     */
    boolean hasObtainedPoints(Long userId, String type, Long relatedId);
}
