package com.red.education.module.community.service;

/**
 * 点赞Service接口
 */
public interface LikeService {

    /**
     * 点赞帖子
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     */
    void likePost(Long userId, Long postId);

    /**
     * 取消点赞帖子
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     */
    void unlikePost(Long userId, Long postId);

    /**
     * 检查用户是否已点赞帖子
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 是否已点赞
     */
    boolean hasLikedPost(Long userId, Long postId);
}
