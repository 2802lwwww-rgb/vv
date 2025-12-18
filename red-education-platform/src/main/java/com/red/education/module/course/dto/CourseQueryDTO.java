package com.red.education.module.course.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 课程查询DTO
 */
@Data
public class CourseQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 课程标题
     */
    private String title;

    /**
     * 课程分类
     */
    private String category;

    /**
     * 状态：0-下架, 1-上架
     */
    private Integer status;

    /**
     * 是否推荐：0-否, 1-是
     */
    private Integer isRecommend;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 排序字段：createTime-创建时间, studyCount-学习人数
     */
    private String orderBy;
}
