package com.red.education.module.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.common.result.Result;
import com.red.education.module.community.dto.CommentDTO;
import com.red.education.module.community.service.CommentService;
import com.red.education.module.community.service.LikeService;
import com.red.education.module.community.vo.CommentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 互动Controller（点赞、评论）
 */
@Api(tags = "互动功能")
@RestController
@RequestMapping("/community")
@Validated
public class InteractionController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private CommentService commentService;

    @ApiOperation("点赞帖子")
    @PostMapping("/posts/{postId}/like")
    public Result<Void> likePost(Authentication authentication, @PathVariable Long postId) {
        Long userId = (Long) authentication.getPrincipal();
        likeService.likePost(userId, postId);
        return Result.<Void>success("点赞成功");
    }

    @ApiOperation("取消点赞帖子")
    @PostMapping("/posts/{postId}/unlike")
    public Result<Void> unlikePost(Authentication authentication, @PathVariable Long postId) {
        Long userId = (Long) authentication.getPrincipal();
        likeService.unlikePost(userId, postId);
        return Result.<Void>success("取消点赞成功");
    }

    @ApiOperation("检查是否已点赞")
    @GetMapping("/posts/{postId}/like/check")
    public Result<Boolean> checkLike(Authentication authentication, @PathVariable Long postId) {
        Long userId = (Long) authentication.getPrincipal();
        boolean liked = likeService.hasLikedPost(userId, postId);
        return Result.success(liked);
    }

    @ApiOperation("发表评论")
    @PostMapping("/comments")
    public Result<Void> addComment(
            Authentication authentication,
            @Valid @RequestBody CommentDTO commentDTO) {
        Long userId = (Long) authentication.getPrincipal();
        commentService.addComment(userId, commentDTO.getPostId(), commentDTO);
        return Result.<Void>success("评论成功");
    }

    @ApiOperation("获取评论列表")
    @GetMapping("/posts/{postId}/comments")
    public Result<Page<CommentVO>> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size) {
        Page<CommentVO> page = commentService.getComments(postId, current, size);
        return Result.success(page);
    }

    @ApiOperation("删除评论")
    @DeleteMapping("/comments/{commentId}")
    public Result<Void> deleteComment(Authentication authentication, @PathVariable Long commentId) {
        Long userId = (Long) authentication.getPrincipal();
        commentService.deleteComment(userId, commentId);
        return Result.<Void>success("删除成功");
    }
}
