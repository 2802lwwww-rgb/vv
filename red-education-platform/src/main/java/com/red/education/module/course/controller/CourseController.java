package com.red.education.module.course.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.common.result.Result;
import com.red.education.module.course.dto.CourseDTO;
import com.red.education.module.course.dto.CourseQueryDTO;
import com.red.education.module.course.entity.Course;
import com.red.education.module.course.service.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 课程Controller
 */
@Api(tags = "课程管理")
@RestController
@RequestMapping("/course")
@Validated
public class CourseController {

    @Autowired
    private CourseService courseService;

    @ApiOperation("分页查询课程列表")
    @GetMapping("/list")
    public Result<Page<Course>> list(Page<Course> page, CourseQueryDTO queryDTO) {
        // 前端用户只能查看上架课程
        if (queryDTO.getStatus() == null) {
            queryDTO.setStatus(1);
        }
        return Result.success(courseService.pageList(page, queryDTO));
    }

    @ApiOperation("获取课程详情")
    @GetMapping("/{id}")
    public Result<Course> getDetail(@PathVariable Long id) {
        Course course = courseService.getById(id);
        // 下架课程不允许访问
        if (course == null || course.getStatus() == 0) {
            return Result.fail("课程不存在或已下架");
        }
        courseService.addViewCount(id);
        return Result.success(course);
    }

    @ApiOperation("创建课程")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody CourseDTO courseDTO) {
        courseService.createCourse(courseDTO);
        return Result.success("创建成功");
    }

    @ApiOperation("更新课程")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody CourseDTO courseDTO) {
        courseService.updateCourse(courseDTO);
        return Result.success("更新成功");
    }

    @ApiOperation("删除课程")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        courseService.removeById(id);
        return Result.success("删除成功");
    }

    @ApiOperation("更新课程状态")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    @PutMapping("/{id}/status/{status}")
    public Result<Void> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        courseService.updateStatus(id, status);
        return Result.success("状态更新成功");
    }
}
