package com.red.education.module.points.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.red.education.module.points.entity.PointRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 积分记录Mapper
 */
@Mapper
public interface PointRecordMapper extends BaseMapper<PointRecord> {
}
