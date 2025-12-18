package com.red.education.module.resource.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.common.result.Result;
import com.red.education.module.resource.dto.AuditResourceDTO;
import com.red.education.module.resource.dto.ResourceDTO;
import com.red.education.module.resource.service.ResourceService;
import com.red.education.module.resource.vo.ResourceDetailVO;
import com.red.education.module.resource.vo.ResourceVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 资源管理Controller（管理员）
 */
@Api(tags = "资源管理")
@RestController
@RequestMapping("/admin/resource")
@Validated
public class AdminResourceController {

    @Autowired
    private ResourceService resourceService;

    @ApiOperation("获取资源列表")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    public Result<Page<ResourceVO>> listResources(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        Page<ResourceVO> page = resourceService.listResources(current, size, categoryId, keyword, status);
        return Result.success(page);
    }

    @ApiOperation("管理员上传资源")
    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    public Result<Void> uploadResource(Authentication authentication, @Valid @RequestBody ResourceDTO resourceDTO) {
        Long adminId = (Long) authentication.getPrincipal();
        resourceService.uploadResourceByAdmin(adminId, resourceDTO);
        return Result.<Void>success("上传成功");
    }

    @ApiOperation("获取资源详情（管理员）")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    public Result<ResourceDetailVO> getResourceDetail(@PathVariable Long id) {
        ResourceDetailVO vo = resourceService.getAdminResourceDetail(id);
        return Result.success(vo);
    }

    @ApiOperation("更新资源")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    public Result<Void> updateResource(@PathVariable Long id, @Valid @RequestBody ResourceDTO resourceDTO) {
        resourceService.updateResource(id, resourceDTO);
        return Result.<Void>success("更新成功");
    }

    @ApiOperation("删除资源")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    public Result<Void> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return Result.<Void>success("删除成功");
    }

    @ApiOperation("获取待审核资源列表")
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    public Result<Page<ResourceVO>> getPendingResources(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<ResourceVO> page = resourceService.getPendingResources(current, size);
        return Result.success(page);
    }

    @ApiOperation("审核资源")
    @PostMapping("/audit/{resourceId}")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    public Result<Void> auditResource(
            Authentication authentication,
            @PathVariable Long resourceId,
            @Valid @RequestBody AuditResourceDTO auditResourceDTO) {
        Long adminId = (Long) authentication.getPrincipal();
        resourceService.auditResource(adminId, resourceId, auditResourceDTO);
        return Result.<Void>success("审核成功");
    }
}
