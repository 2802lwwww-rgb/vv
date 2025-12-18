package com.red.education.module.points.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.module.points.entity.PointRecord;
import com.red.education.module.points.mapper.PointRecordMapper;
import com.red.education.module.points.service.PointService;
import com.red.education.module.points.vo.PointLeaderboardVO;
import com.red.education.module.user.entity.User;
import com.red.education.module.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 积分服务实现类
 */
@Slf4j
@Service
public class PointServiceImpl implements PointService {

    @Autowired
    private PointRecordMapper pointRecordMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPoints(Long userId, Integer points, String type, Long relatedId, String description) {
        // 1. 记录积分变动
        PointRecord record = new PointRecord();
        record.setUserId(userId);
        record.setPoints(points);
        record.setType(type);
        record.setRelatedId(relatedId);
        record.setDescription(description);
        record.setCreateTime(LocalDateTime.now());
        pointRecordMapper.insert(record);

        // 2. 更新用户总积分
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setPoints(user.getPoints() + points);
            userMapper.updateById(user);
        }
    }

    @Override
    public List<PointLeaderboardVO> getLeaderboard(Integer topN) {
        // 查询积分最高的前N名用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(User::getPoints);
        wrapper.last("LIMIT " + topN);

        List<User> users = userMapper.selectList(wrapper);

        // 转换为VO
        return users.stream().map(user -> {
            PointLeaderboardVO vo = new PointLeaderboardVO();
            vo.setUserId(user.getId());
            vo.setUsername(user.getUsername());
            vo.setNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
            vo.setAvatar(user.getAvatar());
            vo.setPoints(user.getPoints());
            // 排名在前端处理，或者这里循环设置
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public Page<PointRecord> getPointRecords(Long userId, Integer current, Integer size, String type) {
        Page<PointRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<PointRecord> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(PointRecord::getUserId, userId);

        // 如果指定了类型，则按类型过滤
        if (StrUtil.isNotBlank(type)) {
            wrapper.eq(PointRecord::getType, type);
        }

        wrapper.orderByDesc(PointRecord::getCreateTime);

        return pointRecordMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean hasObtainedPoints(Long userId, String type, Long relatedId) {
        LambdaQueryWrapper<PointRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointRecord::getUserId, userId);
        if (StrUtil.isNotBlank(type)) {
            if (type.endsWith("%")) {
                wrapper.likeRight(PointRecord::getType, type.substring(0, type.length() - 1));
            } else {
                wrapper.eq(PointRecord::getType, type);
            }
        }
        wrapper.eq(PointRecord::getRelatedId, relatedId);
        wrapper.last("LIMIT 1");
        return pointRecordMapper.selectCount(wrapper) > 0;
    }
}
