package com.red.education.module.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.common.result.Result;
import com.red.education.module.user.dto.UserRoleUpdateDTO;
import com.red.education.module.user.service.UserService;
import com.red.education.module.user.vo.UserAdminVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * 用户管理Controller（管理员）
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/admin/user")
@Validated
public class AdminUserController {

    @Autowired
    private UserService userService;

    @ApiOperation("用户列表查询")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    @GetMapping("/list")
    public Result<Page<UserAdminVO>> getUserList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        Page<UserAdminVO> page = userService.getUserList(current, size, keyword);
        return Result.success(page);
    }

    @ApiOperation("修改用户角色")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    @PutMapping("/role")
    public Result<Void> updateUserRole(@Valid @RequestBody UserRoleUpdateDTO dto) {
        userService.updateUserRole(dto.getUserId(), dto.getRole());
        return Result.<Void>success("角色修改成功");
    }

    @ApiOperation("禁用/启用用户")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    @PutMapping("/{userId}/status/{status}")
    public Result<Void> updateUserStatus(
            @PathVariable Long userId,
            @PathVariable Integer status) {
        userService.updateUserStatus(userId, status);
        String message = status == 1 ? "用户已启用" : "用户已禁用";
        return Result.<Void>success(message);
    }

    @ApiOperation("重置用户密码")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    @PutMapping("/{userId}/password")
    public Result<Void> resetPassword(
            @PathVariable Long userId,
            @RequestParam @NotBlank(message = "新密码不能为空") String newPassword) {
        userService.adminResetPassword(userId, newPassword);
        return Result.<Void>success("密码重置成功");
    }
}
