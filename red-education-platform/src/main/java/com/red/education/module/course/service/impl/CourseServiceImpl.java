package com.red.education.module.course.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.red.education.module.course.dto.CourseDTO;
import com.red.education.module.course.dto.CourseQueryDTO;
import com.red.education.module.course.entity.Course;
import com.red.education.module.course.mapper.CourseMapper;
import com.red.education.module.course.service.CourseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 课程服务实现类
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @org.springframework.beans.factory.annotation.Autowired
    private com.red.education.common.service.FileCleanupService fileCleanupService;

    @Override
    public Page<Course> pageList(Page<Course> page, CourseQueryDTO queryDTO) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();

        // 查询条件
        if (StrUtil.isNotBlank(queryDTO.getTitle())) {
            wrapper.like(Course::getTitle, queryDTO.getTitle());
        }
        if (StrUtil.isNotBlank(queryDTO.getCategory())) {
            wrapper.eq(Course::getCategory, queryDTO.getCategory());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Course::getStatus, queryDTO.getStatus());
        }
        if (queryDTO.getIsRecommend() != null) {
            wrapper.eq(Course::getIsRecommend, queryDTO.getIsRecommend());
        }

        // 关键词搜索 - 搜索标题和简介
        if (StrUtil.isNotBlank(queryDTO.getKeyword())) {
            wrapper.and(w -> w.like(Course::getTitle, queryDTO.getKeyword())
                    .or().like(Course::getIntro, queryDTO.getKeyword()));
        }

        // 排序
        String orderBy = queryDTO.getOrderBy();
        if ("studyCount".equals(orderBy)) {
            wrapper.orderByDesc(Course::getCompleteCount);
        } else {
            // 默认按创建时间排序，推荐优先
            wrapper.orderByDesc(Course::getIsRecommend)
                    .orderByDesc(Course::getCreateTime);
        }

        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCourse(CourseDTO courseDTO) {
        Course course = new Course();
        BeanUtil.copyProperties(courseDTO, course);

        // 设置默认值
        course.setViewCount(0);
        course.setCompleteCount(0);
        course.setCreateTime(LocalDateTime.now());
        course.setUpdateTime(LocalDateTime.now());

        // 如果状态为空，默认为上架
        if (course.getStatus() == null) {
            course.setStatus(1);
        }

        this.save(course);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCourse(CourseDTO courseDTO) {
        Course course = this.getById(courseDTO.getId());
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }

        BeanUtil.copyProperties(courseDTO, course);
        course.setUpdateTime(LocalDateTime.now());

        this.updateById(course);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Course course = this.getById(id);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }

        course.setStatus(status);
        course.setUpdateTime(LocalDateTime.now());
        this.updateById(course);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addViewCount(Long id) {
        // 使用 SQL 直接更新：update course set view_count = view_count + 1 where id = ?
        // 这里为了简单使用 MyBatis-Plus 的 update
        // 实际高并发场景建议使用 Redis 缓存计数，定时同步到数据库
        Course course = this.getById(id);
        if (course != null) {
            course.setViewCount(course.getViewCount() + 1);
            this.updateById(course);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(java.io.Serializable id) {
        // 先获取课程信息，以便删除关联的文件
        Course course = this.getById(id);
        if (course != null) {
            // 删除关联的文件（视频和封面图）
            fileCleanupService.deleteFiles(course.getVideoUrl(), course.getCoverImage());
        }

        // 调用父类方法删除课程记录
        return super.removeById(id);
    }
}
