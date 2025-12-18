package com.red.education.module.user.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.red.education.module.user.entity.LoginLog;
import com.red.education.module.user.mapper.LoginLogMapper;
import com.red.education.module.user.service.LoginLogService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录日志Service实现类
 */
@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {

    @Override
    public void recordLoginLog(Long userId, Integer status, String message, HttpServletRequest request) {
        LoginLog loginLog = new LoginLog();
        loginLog.setUserId(userId);
        loginLog.setStatus(status);
        loginLog.setMessage(message);

        // 获取IP地址
        String ip = getIpAddress(request);
        loginLog.setLoginIp(ip);

        // 解析User-Agent
        String userAgentStr = request.getHeader("User-Agent");
        if (StrUtil.isNotBlank(userAgentStr)) {
            UserAgent userAgent = UserAgentUtil.parse(userAgentStr);
            loginLog.setBrowser(userAgent.getBrowser().getName() + " " + userAgent.getVersion());
            loginLog.setOs(userAgent.getOs().getName());
        }

        // 这里可以根据IP获取地理位置，暂时省略
        loginLog.setLoginLocation("未知");

        this.save(loginLog);
    }

    /**
     * 获取客户端IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
