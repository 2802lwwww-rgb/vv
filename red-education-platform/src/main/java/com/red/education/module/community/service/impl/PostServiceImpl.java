package com.red.education.module.community.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.common.exception.BusinessException;
import com.red.education.module.community.dto.AuditPostDTO;
import com.red.education.module.community.dto.PostDTO;
import com.red.education.module.community.entity.Comment;
import com.red.education.module.community.entity.Post;
import com.red.education.module.community.mapper.CommentMapper;
import com.red.education.module.community.mapper.PostMapper;
import com.red.education.module.community.service.PostService;
import com.red.education.module.community.vo.PostDetailVO;
import com.red.education.module.community.vo.PostVO;
import com.red.education.module.community.vo.TopicVO;
import com.red.education.module.points.service.PointService;
import com.red.education.module.user.entity.User;
import com.red.education.module.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 帖子Service实现类
 */
@Slf4j
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PointService pointService;

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public void publishPost(Long userId, PostDTO postDTO) {
        Post post = new Post();
        BeanUtils.copyProperties(postDTO, post);
        post.setUserId(userId);
        post.setStatus(0); // 待审核
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setViewCount(0);
        post.setIsTop(0);

        postMapper.insert(post);
        log.info("用户{}发布帖子成功，帖子ID={}", userId, post.getId());
    }

    @Override
    public Page<PostVO> listPosts(Integer current, Integer size, String topic, String keyword, String orderBy) {
        Page<Post> page = new Page<>(current, size);
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(Post::getStatus, 1);

        // 话题精确筛选
        if (StrUtil.isNotBlank(topic)) {
            wrapper.eq(Post::getTopic, topic);
        }

        // 关键词模糊搜索（标题或内容）
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Post::getTitle, keyword)
                    .or().like(Post::getContent, keyword));
        }

        log.info("帖子列表查询 - orderBy参数: {}", orderBy);

        if ("hot".equals(orderBy)) {
            wrapper.orderByDesc(Post::getIsTop);
            wrapper.orderByDesc(Post::getLikeCount);
            wrapper.orderByDesc(Post::getCommentCount);
            wrapper.orderByDesc(Post::getViewCount);
        } else if ("likeCount".equals(orderBy)) {
            wrapper.orderByDesc(Post::getIsTop);
            wrapper.orderByDesc(Post::getLikeCount);
        } else if ("commentCount".equals(orderBy)) {
            wrapper.orderByDesc(Post::getIsTop);
            wrapper.orderByDesc(Post::getCommentCount);
        } else {
            // createTime, time 或其他值都按时间排序
            wrapper.orderByDesc(Post::getIsTop);
            wrapper.orderByDesc(Post::getCreateTime);
        }

        Page<Post> postPage = postMapper.selectPage(page, wrapper);
        return convertToPostVOPage(postPage);
    }

    @Override
    public Page<PostVO> searchPosts(Integer current, Integer size, String keyword) {
        Page<Post> page = new Page<>(current, size);
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(Post::getStatus, 1);

        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Post::getTitle, keyword).or().like(Post::getContent, keyword));
        }

        wrapper.orderByDesc(Post::getCreateTime);

        Page<Post> postPage = postMapper.selectPage(page, wrapper);
        return convertToPostVOPage(postPage);
    }

    @Override
    public PostDetailVO getPostDetail(Long id, Long viewerId) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }

        // 只有已通过的帖子可以公开查看，但帖子作者可以查看自己的任何帖子
        boolean isAuthor = viewerId != null && viewerId.equals(post.getUserId());
        if (post.getStatus() != 1 && !isAuthor) {
            throw new BusinessException("帖子未通过审核");
        }

        PostDetailVO vo = new PostDetailVO();
        BeanUtils.copyProperties(post, vo);

        vo.setFullContent(post.getContent());

        if (StrUtil.isNotBlank(post.getImages())) {
            vo.setImageList(Arrays.asList(post.getImages().split(",")));
        }

        User user = userMapper.selectById(post.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
        }

        vo.setContent(truncateContent(post.getContent()));

        // 自动修复评论数不一致的问题
        Long realCommentCount = commentMapper.selectCount(new LambdaQueryWrapper<Comment>().eq(Comment::getPostId, id));
        if (realCommentCount != null) {
            int count = realCommentCount.intValue();
            if (post.getCommentCount() == null || post.getCommentCount() != count) {
                log.warn("帖子{}评论数不一致，自动修复：数据库={}, 实际={}", id, post.getCommentCount(), count);
                post.setCommentCount(count);
                postMapper.updateById(post);
                vo.setCommentCount(count);
            }
        }

        return vo;
    }

    @Override
    public void addViewCount(Long id) {
        Post post = postMapper.selectById(id);
        if (post != null) {
            post.setViewCount(post.getViewCount() + 1);
            postMapper.updateById(post);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditPost(Long adminId, Long postId, AuditPostDTO auditPostDTO) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }

        if (post.getStatus() != 0) {
            throw new BusinessException("该帖子已审核");
        }

        post.setStatus(auditPostDTO.getStatus());
        post.setAuditAdminId(adminId);
        post.setAuditTime(LocalDateTime.now());
        post.setAuditComment(auditPostDTO.getAuditComment());
        postMapper.updateById(post);

        if (auditPostDTO.getStatus() == 1) {
            pointService.addPoints(post.getUserId(), 5, "POST_APPROVED", postId, "帖子审核通过");
            log.info("帖子{}审核通过，用户{}获得5积分", postId, post.getUserId());
        }

        log.info("管理员{}审核帖子{}，结果={}", adminId, postId, auditPostDTO.getStatus());
    }

    @Override
    public Page<PostVO> getPendingPosts(Integer current, Integer size) {
        Page<Post> page = new Page<>(current, size);
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(Post::getStatus, 0);
        wrapper.orderByDesc(Post::getCreateTime);

        Page<Post> postPage = postMapper.selectPage(page, wrapper);
        // 待审核帖子返回完整内容，不截断
        return convertToPostVOPage(postPage, false);
    }

    @Override
    public List<TopicVO> getHotTopics(Integer topN) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 1);
        wrapper.isNotNull(Post::getTopic);
        wrapper.ne(Post::getTopic, "");

        List<Post> posts = postMapper.selectList(wrapper);

        Map<String, TopicVO> topicMap = new HashMap<>();

        for (Post post : posts) {
            String topic = post.getTopic();
            TopicVO topicVO = topicMap.computeIfAbsent(topic, k -> {
                TopicVO vo = new TopicVO();
                vo.setTopic(topic);
                vo.setPostCount(0L);
                vo.setTotalViews(0L);
                vo.setTotalLikes(0L);
                return vo;
            });

            topicVO.setPostCount(topicVO.getPostCount() + 1);
            topicVO.setTotalViews(topicVO.getTotalViews() + post.getViewCount());
            topicVO.setTotalLikes(topicVO.getTotalLikes() + post.getLikeCount());
        }

        return topicMap.values().stream()
                .sorted((a, b) -> {
                    long heatA = a.getPostCount() * 10 + a.getTotalViews() + a.getTotalLikes() * 5;
                    long heatB = b.getPostCount() * 10 + b.getTotalViews() + b.getTotalLikes() * 5;
                    return Long.compare(heatB, heatA);
                })
                .limit(topN)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePost(Long userId, Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }

        // 只能删除自己的帖子
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException("无权删除他人帖子");
        }

        postMapper.deleteById(postId);
        log.info("用户{}删除帖子{}", userId, postId);
    }

    @Override
    public Page<PostVO> getMyPosts(Long userId, Integer current, Integer size) {
        Page<Post> page = new Page<>(current, size);
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();

        // 查询当前用户的所有帖子（包含所有审核状态）
        wrapper.eq(Post::getUserId, userId);
        wrapper.orderByDesc(Post::getCreateTime);

        Page<Post> postPage = postMapper.selectPage(page, wrapper);
        return convertToPostVOPage(postPage);
    }

    @Override
    public PostDetailVO getPostDetailForAdmin(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }

        // 管理员可以查看任何状态的帖子，不检查状态
        PostDetailVO vo = new PostDetailVO();
        BeanUtils.copyProperties(post, vo);

        vo.setFullContent(post.getContent());

        if (StrUtil.isNotBlank(post.getImages())) {
            vo.setImageList(Arrays.asList(post.getImages().split(",")));
        }

        User user = userMapper.selectById(post.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
        }

        vo.setContent(post.getContent()); // 完整内容
        return vo;
    }

    private Page<PostVO> convertToPostVOPage(Page<Post> postPage) {
        return convertToPostVOPage(postPage, true);
    }

    private Page<PostVO> convertToPostVOPage(Page<Post> postPage, boolean truncate) {
        List<Post> posts = postPage.getRecords();

        List<Long> userIds = posts.stream()
                .map(Post::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, User> userMap;
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));
        } else {
            userMap = Map.of();
        }

        List<PostVO> voList = posts.stream().map(post -> {
            PostVO vo = new PostVO();
            BeanUtils.copyProperties(post, vo);

            // 根据参数决定是否截断内容
            vo.setContent(truncate ? truncateContent(post.getContent()) : post.getContent());

            User user = userMap.get(post.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setNickname(user.getNickname());
                vo.setAvatar(user.getAvatar());
            }

            return vo;
        }).collect(Collectors.toList());

        Page<PostVO> voPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        voPage.setRecords(voList);

        return voPage;
    }

    private String truncateContent(String content) {
        if (content == null) {
            return "";
        }
        if (content.length() <= 200) {
            return content;
        }
        return content.substring(0, 200) + "...";
    }
}
