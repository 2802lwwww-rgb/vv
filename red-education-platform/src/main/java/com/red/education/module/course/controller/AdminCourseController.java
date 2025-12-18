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
 * 课程管理Controller（管理员）
 */
@Api(tags = "课程管理（后台）")
@RestController
@RequestMapping("/admin/courses")
@Validated
public class AdminCourseController {

    @Autowired
    private CourseService courseService;

    @ApiOperation("获取课程列表")
    @GetMapping
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    public Result<Page<Course>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            CourseQueryDTO queryDTO) {
        Page<Course> pageParam = new Page<>(page, pageSize);
        return Result.success(courseService.pageList(pageParam, queryDTO));
    }

    @ApiOperation("创建课程")
    @PostMapping
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    public Result<Void> create(@Valid @RequestBody CourseDTO courseDTO) {
        courseService.createCourse(courseDTO);
        return Result.success("创建成功");
    }

    @ApiOperation("更新课程")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CourseDTO courseDTO) {
        // 确保ID一致
        courseDTO.setId(id);
        courseService.updateCourse(courseDTO);
        return Result.success("更新成功");
    }

    @ApiOperation("删除课程")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        courseService.removeById(id);
        return Result.success("删除成功");
    }
}
