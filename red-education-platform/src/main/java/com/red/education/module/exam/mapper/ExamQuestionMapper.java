package com.red.education.module.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.red.education.module.exam.entity.ExamQuestion;
import org.apache.ibatis.annotations.Mapper;

/**
 * 试卷题目关联Mapper接口
 */
@Mapper
public interface ExamQuestionMapper extends BaseMapper<ExamQuestion> {
}
