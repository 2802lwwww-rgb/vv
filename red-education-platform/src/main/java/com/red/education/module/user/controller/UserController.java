package com.red.education.module.user.controller;

import com.red.education.common.result.Result;
import com.red.education.module.user.dto.ChangePasswordDTO;
import com.red.education.module.user.dto.ResetPasswordDTO;
import com.red.education.module.user.dto.SendResetCodeDTO;
import com.red.education.module.user.dto.UserLoginDTO;
import com.red.education.module.user.dto.UserRegisterDTO;
import com.red.education.module.user.dto.UserUpdateDTO;
import com.red.education.module.user.service.UserService;
import com.red.education.module.user.vo.UserInfoVO;
import com.red.education.module.user.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户Controller
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.<Void>success("注册成功");
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<UserLoginVO> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        UserLoginVO loginVO = userService.login(loginDTO);
        return Result.success(loginVO);
    }

    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        UserInfoVO userInfo = userService.getUserInfo(userId);
        return Result.success(userInfo);
    }

    @ApiOperation("更新用户信息")
    @PutMapping("/info")
    public Result<Void> updateUserInfo(Authentication authentication,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        Long userId = (Long) authentication.getPrincipal();
        userService.updateUserInfo(userId, updateDTO);
        return Result.<Void>success("更新成功");
    }

    @ApiOperation("修改密码")
    @PutMapping("/password")
    public Result<Void> changePassword(Authentication authentication,
            @Valid @RequestBody ChangePasswordDTO dto) {
        Long userId = (Long) authentication.getPrincipal();
        userService.changePassword(userId, dto.getOldPassword(), dto.getNewPassword());
        return Result.<Void>success("密码修改成功");
    }

    @ApiOperation("发送密码重置验证码")
    @PostMapping("/reset-code")
    public Result<Void> sendResetCode(@Valid @RequestBody SendResetCodeDTO dto) {
        userService.sendResetCode(dto.getEmail());
        return Result.<Void>success("验证码已发送到您的邮箱");
    }

    @ApiOperation("重置密码")
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) {
        userService.resetPassword(dto.getEmail(), dto.getCode(), dto.getNewPassword());
        return Result.<Void>success("密码重置成功");
    }
}
