package com.red.education.module.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.common.result.Result;
import com.red.education.module.community.dto.AuditPostDTO;
import com.red.education.module.community.service.PostService;
import com.red.education.module.community.vo.PostVO;
import com.red.education.module.community.vo.TopicVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 帖子管理Controller（管理员）
 */
@Api(tags = "帖子管理")
@RestController
@RequestMapping("/admin/post")
@Validated
public class AdminPostController {

    @Autowired
    private PostService postService;

    @ApiOperation("获取待审核帖子列表")
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    public Result<Page<PostVO>> getPendingPosts(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<PostVO> page = postService.getPendingPosts(current, size);
        return Result.success(page);
    }

    @ApiOperation("审核帖子")
    @PostMapping("/audit/{postId}")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    public Result<Void> auditPost(
            Authentication authentication,
            @PathVariable Long postId,
            @Valid @RequestBody AuditPostDTO auditPostDTO) {
        Long adminId = (Long) authentication.getPrincipal();
        postService.auditPost(adminId, postId, auditPostDTO);
        return Result.<Void>success("审核成功");
    }

    @ApiOperation("管理员查看帖子详情")
    @GetMapping("/{postId}")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    public Result<com.red.education.module.community.vo.PostDetailVO> getPostDetail(@PathVariable Long postId) {
        // 管理员可以查看任何状态的帖子
        com.red.education.module.community.vo.PostDetailVO detail = postService.getPostDetailForAdmin(postId);
        return Result.success(detail);
    }

    @ApiOperation("获取热门话题")
    @GetMapping("/topics/hot")
    public Result<List<TopicVO>> getHotTopics(@RequestParam(defaultValue = "10") Integer topN) {
        List<TopicVO> topics = postService.getHotTopics(topN);
        return Result.success(topics);
    }
}
