package com.red.education.module.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.red.education.common.exception.BusinessException;
import com.red.education.module.community.entity.LikeRecord;
import com.red.education.module.community.entity.Post;
import com.red.education.module.community.mapper.LikeRecordMapper;
import com.red.education.module.community.mapper.PostMapper;
import com.red.education.module.community.service.LikeService;
import com.red.education.module.points.service.PointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 点赞Service实现类
 */
@Slf4j
@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private LikeRecordMapper likeRecordMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private PointService pointService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void likePost(Long userId, Long postId) {
        // 检查是否已点赞
        if (hasLikedPost(userId, postId)) {
            throw new BusinessException("您已经点赞过了");
        }

        // 检查帖子是否存在
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }

        // 创建点赞记录
        LikeRecord likeRecord = new LikeRecord();
        likeRecord.setUserId(userId);
        likeRecord.setTargetType("POST");
        likeRecord.setTargetId(postId);
        likeRecordMapper.insert(likeRecord);

        // 更新帖子点赞数
        post.setLikeCount(post.getLikeCount() + 1);
        postMapper.updateById(post);

        // 给帖子作者增加积分 (+1分)
        if (!userId.equals(post.getUserId())) { // 不能给自己点赞加分
            pointService.addPoints(post.getUserId(), 1, "LIKE", postId, "帖子被点赞");
        }

        log.info("用户{}点赞帖子{}", userId, postId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlikePost(Long userId, Long postId) {
        // 查找点赞记录
        LambdaQueryWrapper<LikeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LikeRecord::getUserId, userId);
        wrapper.eq(LikeRecord::getTargetType, "POST");
        wrapper.eq(LikeRecord::getTargetId, postId);

        LikeRecord likeRecord = likeRecordMapper.selectOne(wrapper);
        if (likeRecord == null) {
            throw new BusinessException("您还未点赞");
        }

        // 删除点赞记录
        likeRecordMapper.deleteById(likeRecord.getId());

        // 更新帖子点赞数
        Post post = postMapper.selectById(postId);
        if (post != null && post.getLikeCount() > 0) {
            post.setLikeCount(post.getLikeCount() - 1);
            postMapper.updateById(post);
        }

        log.info("用户{}取消点赞帖子{}", userId, postId);
    }

    @Override
    public boolean hasLikedPost(Long userId, Long postId) {
        LambdaQueryWrapper<LikeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LikeRecord::getUserId, userId);
        wrapper.eq(LikeRecord::getTargetType, "POST");
        wrapper.eq(LikeRecord::getTargetId, postId);

        return likeRecordMapper.selectCount(wrapper) > 0;
    }
}
