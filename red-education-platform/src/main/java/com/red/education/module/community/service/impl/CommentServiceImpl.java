package com.red.education.module.community.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.common.exception.BusinessException;
import com.red.education.module.community.dto.CommentDTO;
import com.red.education.module.community.entity.Comment;
import com.red.education.module.community.entity.Post;
import com.red.education.module.community.mapper.CommentMapper;
import com.red.education.module.community.mapper.PostMapper;
import com.red.education.module.community.service.CommentService;
import com.red.education.module.community.vo.CommentVO;
import com.red.education.module.points.service.PointService;
import com.red.education.module.user.entity.User;
import com.red.education.module.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论Service实现类
 */
@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PointService pointService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addComment(Long userId, Long postId, CommentDTO commentDTO) {
        // 检查帖子是否存在
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }

        // 创建评论
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDTO, comment);
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setLikeCount(0);

        // 如果没有指定父评论ID，设为0
        if (comment.getParentId() == null) {
            comment.setParentId(0L);
        }

        commentMapper.insert(comment);

        // 更新帖子评论数
        post.setCommentCount(post.getCommentCount() + 1);
        postMapper.updateById(post);

        // 给评论者增加积分 (+2分)，但不能给自己的帖子评论加分
        if (!userId.equals(post.getUserId())) {
            pointService.addPoints(userId, 2, "COMMENT", postId, "发表评论");
        }

        log.info("用户{}在帖子{}发表评论", userId, postId);
    }

    @Override
    public Page<CommentVO> getComments(Long postId, Integer current, Integer size) {
        Page<Comment> page = new Page<>(current, size);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getPostId, postId);
        wrapper.orderByAsc(Comment::getCreateTime); // 按时间正序

        Page<Comment> commentPage = commentMapper.selectPage(page, wrapper);

        return convertToCommentVOPage(commentPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        // 只能删除自己的评论
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("无权删除他人评论");
        }

        // 删除评论
        commentMapper.deleteById(commentId);

        // 更新帖子评论数
        Post post = postMapper.selectById(comment.getPostId());
        if (post != null && post.getCommentCount() > 0) {
            post.setCommentCount(post.getCommentCount() - 1);
            postMapper.updateById(post);
        }

        log.info("用户{}删除评论{}", userId, commentId);
    }

    /**
     * 转换为CommentVO分页对象
     */
    private Page<CommentVO> convertToCommentVOPage(Page<Comment> commentPage) {
        List<Comment> comments = commentPage.getRecords();

        // 批量查询用户信息
        List<Long> userIds = comments.stream()
                .map(Comment::getUserId)
                .distinct()
                .collect(Collectors.toList());

        // 添加回复目标用户ID
        comments.stream()
                .filter(c -> c.getReplyToUserId() != null)
                .forEach(c -> userIds.add(c.getReplyToUserId()));

        Map<Long, User> userMap;
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds.stream().distinct().collect(Collectors.toList()));
            userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));
        } else {
            userMap = Map.of();
        }

        // 转换为VO
        List<CommentVO> voList = comments.stream().map(comment -> {
            CommentVO vo = new CommentVO();
            BeanUtils.copyProperties(comment, vo);

            // 设置用户信息
            User user = userMap.get(comment.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setNickname(user.getNickname());
                vo.setAvatar(user.getAvatar());
            }

            // 设置回复目标用户名
            if (comment.getReplyToUserId() != null) {
                User replyToUser = userMap.get(comment.getReplyToUserId());
                if (replyToUser != null) {
                    // 优先显示昵称，如果昵称为空则显示用户名
                    String replyToDisplayName = StrUtil.isNotBlank(replyToUser.getNickname())
                            ? replyToUser.getNickname()
                            : replyToUser.getUsername();
                    vo.setReplyToUsername(replyToDisplayName);
                }
            }

            return vo;
        }).collect(Collectors.toList());

        Page<CommentVO> voPage = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
        voPage.setRecords(voList);

        return voPage;
    }
}
