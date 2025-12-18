package com.red.education.module.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.red.education.module.user.entity.LoginLog;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录日志Service接口
 */
public interface LoginLogService extends IService<LoginLog> {

    /**
     * 记录登录日志
     */
    void recordLoginLog(Long userId, Integer status, String message, HttpServletRequest request);
}
