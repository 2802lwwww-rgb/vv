package com.red.education.module.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.red.education.module.user.dto.UserLoginDTO;
import com.red.education.module.user.dto.UserRegisterDTO;
import com.red.education.module.user.dto.UserUpdateDTO;
import com.red.education.module.user.entity.User;
import com.red.education.module.user.vo.UserInfoVO;
import com.red.education.module.user.vo.UserLoginVO;

/**
 * 用户Service接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    void register(UserRegisterDTO registerDTO);

    /**
     * 用户登录
     */
    UserLoginVO login(UserLoginDTO loginDTO);

    /**
     * 获取用户信息
     */
    UserInfoVO getUserInfo(Long userId);

    /**
     * 更新用户信息
     */
    void updateUserInfo(Long userId, UserUpdateDTO updateDTO);

    /**
     * 修改密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 发送密码重置验证码
     */
    void sendResetCode(String email);

    /**
     * 重置密码
     */
    void resetPassword(String email, String code, String newPassword);

    /**
     * 管理员获取用户列表（分页）
     */
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.red.education.module.user.vo.UserAdminVO> getUserList(
            Integer current, Integer size, String keyword);

    /**
     * 管理员修改用户角色
     */
    void updateUserRole(Long userId, String role);

    /**
     * 管理员禁用/启用用户
     */
    void updateUserStatus(Long userId, Integer status);

    /**
     * 管理员重置用户密码
     */
    void adminResetPassword(Long userId, String newPassword);
}
