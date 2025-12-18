package com.red.education.module.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.red.education.module.community.entity.Post;
import org.apache.ibatis.annotations.Mapper;

/**
 * 帖子Mapper
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {
}
