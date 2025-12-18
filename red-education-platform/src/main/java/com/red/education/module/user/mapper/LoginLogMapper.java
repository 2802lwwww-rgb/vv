package com.red.education.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.red.education.module.user.entity.LoginLog;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {

    /**
     * 删除指定时间之前的登录日志
     */
    @Delete("DELETE FROM login_log WHERE login_time < #{beforeTime}")
    int deleteOlderThan(@Param("beforeTime") LocalDateTime beforeTime);
}
