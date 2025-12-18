package com.red.education.common.task;

import com.red.education.module.user.mapper.LoginLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 日志清理定时任务
 * 每天凌晨2点执行，清理30天前的登录日志
 */
@Component
public class LogCleanupTask {

    private static final Logger log = LoggerFactory.getLogger(LogCleanupTask.class);

    @Autowired
    private LoginLogMapper loginLogMapper;

    /**
     * 每天凌晨2点执行
     * cron表达式: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupLoginLogs() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        int deleted = loginLogMapper.deleteOlderThan(thirtyDaysAgo);
        log.info("登录日志清理完成，删除了 {} 条30天前的记录", deleted);
    }
}
