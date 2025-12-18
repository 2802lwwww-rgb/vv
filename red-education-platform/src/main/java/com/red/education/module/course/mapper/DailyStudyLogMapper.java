package com.red.education.module.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.red.education.module.course.entity.DailyStudyLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 每日学习日志Mapper
 */
@Mapper
public interface DailyStudyLogMapper extends BaseMapper<DailyStudyLog> {

    /**
     * 获取用户指定日期范围内的每日学习记录
     */
    @Select("SELECT * FROM daily_study_log WHERE user_id = #{userId} AND study_date >= #{startDate} AND study_date <= #{endDate} ORDER BY study_date ASC")
    List<DailyStudyLog> findByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * 获取指定日期范围内的每日学习统计（全平台汇总）
     */
    @Select("SELECT study_date, SUM(study_minutes) as total_minutes " +
            "FROM daily_study_log " +
            "WHERE study_date >= #{startDate} AND study_date <= #{endDate} " +
            "GROUP BY study_date ORDER BY study_date ASC")
    List<Map<String, Object>> getDailyStudyStats(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
