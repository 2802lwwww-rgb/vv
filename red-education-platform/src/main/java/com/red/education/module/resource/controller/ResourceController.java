package com.red.education.module.resource.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.common.result.Result;
import com.red.education.module.resource.dto.ResourceDTO;
import com.red.education.module.resource.entity.ResourceCategory;
import com.red.education.module.resource.service.ResourceService;
import com.red.education.module.resource.vo.ResourceDetailVO;
import com.red.education.module.resource.vo.ResourceVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 资源Controller
 */
@Api(tags = "红色资源")
@RestController
@RequestMapping("/resource")
@Validated
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @ApiOperation("上传资源")
    @PostMapping
    public Result<Void> uploadResource(Authentication authentication, @Valid @RequestBody ResourceDTO resourceDTO) {
        Long userId = (Long) authentication.getPrincipal();
        resourceService.uploadResource(userId, resourceDTO);
        return Result.<Void>success("上传成功，等待审核");
    }

    @ApiOperation("资源列表")
    @GetMapping("/list")
    public Result<Page<ResourceVO>> listResources(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "createTime") String orderBy) {
        // 公开接口只返回已审核通过的资源(status=1)
        Page<ResourceVO> page = resourceService.listResources(current, size, categoryId, keyword, 1, orderBy);
        return Result.success(page);
    }

    @ApiOperation("资源详情")
    @GetMapping("/{id}")
    public Result<ResourceDetailVO> getResourceDetail(@PathVariable Long id) {
        ResourceDetailVO detail = resourceService.getResourceDetail(id);
        // 增加浏览量
        resourceService.addViewCount(id);
        return Result.success(detail);
    }

    @ApiOperation("获取所有分类")
    @GetMapping("/categories")
    public Result<List<ResourceCategory>> getAllCategories() {
        List<ResourceCategory> categories = resourceService.getAllCategories();
        return Result.success(categories);
    }

    @ApiOperation("获取我的资源")
    @GetMapping("/my")
    public Result<Page<ResourceVO>> getMyResources(
            Authentication authentication,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) authentication.getPrincipal();
        Page<ResourceVO> page = resourceService.getMyResources(userId, current, size);
        return Result.success(page);
    }

    @ApiOperation("下载资源")
    @PostMapping("/{id}/download")
    public Result<String> downloadResource(@PathVariable Long id) {
        ResourceDetailVO resource = resourceService.getResourceDetail(id);
        // 增加下载量
        resourceService.addDownloadCount(id);
        return Result.success("下载成功", resource.getFileUrl());
    }
}
