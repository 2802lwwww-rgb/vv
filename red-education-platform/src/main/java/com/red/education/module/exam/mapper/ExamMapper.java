package com.red.education.module.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.red.education.module.exam.entity.Exam;
import org.apache.ibatis.annotations.Mapper;

/**
 * 试卷Mapper接口
 */
@Mapper
public interface ExamMapper extends BaseMapper<Exam> {
}
