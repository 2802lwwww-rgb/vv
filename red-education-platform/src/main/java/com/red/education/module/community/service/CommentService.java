package com.red.education.module.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.module.community.dto.CommentDTO;
import com.red.education.module.community.vo.CommentVO;

/**
 * 评论Service接口
 */
public interface CommentService {

    /**
     * 发表评论
     * 
     * @param userId     用户ID
     * @param postId     帖子ID
     * @param commentDTO 评论DTO
     */
    void addComment(Long userId, Long postId, CommentDTO commentDTO);

    /**
     * 获取帖子的评论列表
     * 
     * @param postId  帖子ID
     * @param current 当前页
     * @param size    每页大小
     * @return 评论列表
     */
    Page<CommentVO> getComments(Long postId, Integer current, Integer size);

    /**
     * 删除评论
     * 
     * @param userId    用户ID
     * @param commentId 评论ID
     */
    void deleteComment(Long userId, Long commentId);
}
