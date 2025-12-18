package com.red.education.module.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.common.result.Result;
import com.red.education.module.community.dto.PostDTO;
import com.red.education.module.community.service.PostService;
import com.red.education.module.community.vo.PostDetailVO;
import com.red.education.module.community.vo.PostVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 帖子Controller
 */
@Api(tags = "互动交流")
@RestController
@RequestMapping("/community/posts")
@Validated
public class PostController {

    @Autowired
    private PostService postService;

    @ApiOperation("发布帖子")
    @PostMapping
    public Result<Void> publishPost(Authentication authentication, @Valid @RequestBody PostDTO postDTO) {
        Long userId = (Long) authentication.getPrincipal();
        postService.publishPost(userId, postDTO);
        return Result.<Void>success("发布成功，等待审核");
    }

    @ApiOperation("帖子列表")
    @GetMapping
    public Result<Page<PostVO>> listPosts(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "time") String orderBy) {
        Page<PostVO> page = postService.listPosts(current, size, topic, keyword, orderBy);
        return Result.success(page);
    }

    @ApiOperation("搜索帖子")
    @GetMapping("/search")
    public Result<Page<PostVO>> searchPosts(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam String keyword) {
        Page<PostVO> page = postService.searchPosts(current, size, keyword);
        return Result.success(page);
    }

    @ApiOperation("帖子详情")
    @GetMapping("/{id}")
    public Result<PostDetailVO> getPostDetail(Authentication authentication, @PathVariable Long id) {
        // 获取当前登录用户ID（未登录则为null）
        Long viewerId = null;
        if (authentication != null) {
            viewerId = (Long) authentication.getPrincipal();
        }
        PostDetailVO detail = postService.getPostDetail(id, viewerId);
        // 增加浏览量
        postService.addViewCount(id);
        return Result.success(detail);
    }

    @ApiOperation("删除帖子")
    @DeleteMapping("/{id}")
    public Result<Void> deletePost(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        postService.deletePost(userId, id);
        return Result.<Void>success("删除成功");
    }

    @ApiOperation("获取热门话题")
    @GetMapping("/hot-topics")
    public Result<java.util.List<com.red.education.module.community.vo.TopicVO>> getHotTopics(
            @RequestParam(defaultValue = "10") Integer topN) {
        java.util.List<com.red.education.module.community.vo.TopicVO> topics = postService.getHotTopics(topN);
        return Result.success(topics);
    }

    @ApiOperation("获取我的帖子")
    @GetMapping("/my")
    public Result<Page<PostVO>> getMyPosts(
            Authentication authentication,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) authentication.getPrincipal();
        Page<PostVO> page = postService.getMyPosts(userId, current, size);
        return Result.success(page);
    }
}
