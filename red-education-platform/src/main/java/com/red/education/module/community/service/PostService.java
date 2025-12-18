package com.red.education.module.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.module.community.dto.PostDTO;
import com.red.education.module.community.vo.PostDetailVO;
import com.red.education.module.community.vo.PostVO;

/**
 * 帖子Service接口
 */
public interface PostService {

    /**
     * 发布帖子
     */
    void publishPost(Long userId, PostDTO postDTO);

    /**
     * 分页查询帖子列表
     * 
     * @param current 当前页
     * @param size    每页大小
     * @param topic   话题筛选（精确匹配，可选）
     * @param keyword 关键词搜索（标题/内容，可选）
     * @param orderBy 排序方式：time-时间 hot-热度
     */
    Page<PostVO> listPosts(Integer current, Integer size, String topic, String keyword, String orderBy);

    /**
     * 搜索帖子
     */
    Page<PostVO> searchPosts(Integer current, Integer size, String keyword);

    /**
     * 获取帖子详情
     * 
     * @param id       帖子ID
     * @param viewerId 查看者用户ID（可选，如果是作者可查看未审核的帖子）
     */
    PostDetailVO getPostDetail(Long id, Long viewerId);

    /**
     * 增加浏览量
     */
    void addViewCount(Long id);

    /**
     * 审核帖子（管理员）
     * 
     * @param adminId      管理员ID
     * @param postId       帖子ID
     * @param auditPostDTO 审核信息
     */
    void auditPost(Long adminId, Long postId, com.red.education.module.community.dto.AuditPostDTO auditPostDTO);

    /**
     * 获取待审核帖子列表（管理员）
     */
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<PostVO> getPendingPosts(Integer current, Integer size);

    /**
     * 获取热门话题
     * 
     * @param topN 前N个
     */
    java.util.List<com.red.education.module.community.vo.TopicVO> getHotTopics(Integer topN);

    /**
     * 删除帖子（用户只能删除自己的帖子）
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     */
    void deletePost(Long userId, Long postId);

    /**
     * 获取我的帖子（包含所有审核状态）
     * 
     * @param userId  用户ID
     * @param current 当前页
     * @param size    每页大小
     */
    Page<PostVO> getMyPosts(Long userId, Integer current, Integer size);

    /**
     * 管理员查看帖子详情（不检查审核状态）
     * 
     * @param id 帖子ID
     */
    PostDetailVO getPostDetailForAdmin(Long id);
}
