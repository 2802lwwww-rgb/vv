package com.red.education.module.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.red.education.module.exam.entity.ExamScore;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考试成绩Mapper接口
 */
@Mapper
public interface ExamScoreMapper extends BaseMapper<ExamScore> {
}
