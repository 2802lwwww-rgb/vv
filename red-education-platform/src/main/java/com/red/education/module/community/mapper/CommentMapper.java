package com.red.education.module.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.red.education.module.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论Mapper
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
