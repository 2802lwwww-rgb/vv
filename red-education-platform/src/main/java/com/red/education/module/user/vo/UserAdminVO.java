package com.red.education.module.user.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户管理VO（管理端使用）
 */
@Data
public class UserAdminVO {

    private Long id;

    private String username;

    private String nickname;

    private String email;

    private String phone;

    private String avatar;

    private String role;

    private String roleName;

    private Integer points;

    private Integer status;

    private String statusName;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
