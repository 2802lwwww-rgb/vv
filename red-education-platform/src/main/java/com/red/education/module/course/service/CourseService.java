package com.red.education.module.course.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.red.education.module.course.dto.CourseDTO;
import com.red.education.module.course.dto.CourseQueryDTO;
import com.red.education.module.course.entity.Course;

/**
 * 课程服务接口
 */
public interface CourseService extends IService<Course> {

    /**
     * 分页查询课程列表
     *
     * @param page     分页参数
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<Course> pageList(Page<Course> page, CourseQueryDTO queryDTO);

    /**
     * 创建课程
     *
     * @param courseDTO 课程信息
     */
    void createCourse(CourseDTO courseDTO);

    /**
     * 更新课程
     *
     * @param courseDTO 课程信息
     */
    void updateCourse(CourseDTO courseDTO);

    /**
     * 更新课程状态（上架/下架）
     *
     * @param id     课程ID
     * @param status 状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 增加浏览次数
     *
     * @param id 课程ID
     */
    void addViewCount(Long id);
}
