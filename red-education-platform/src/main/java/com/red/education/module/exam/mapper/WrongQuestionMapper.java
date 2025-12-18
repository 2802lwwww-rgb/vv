package com.red.education.module.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.red.education.module.exam.entity.WrongQuestion;
import org.apache.ibatis.annotations.Mapper;

/**
 * 错题本Mapper接口
 */
@Mapper
public interface WrongQuestionMapper extends BaseMapper<WrongQuestion> {
}
