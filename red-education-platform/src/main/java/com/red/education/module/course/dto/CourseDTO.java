package com.red.education.module.course.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 课程DTO
 */
@Data
public class CourseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "课程标题不能为空")
    private String title;

    private String intro;

    @NotBlank(message = "课程内容不能为空")
    private String content;

    private String videoUrl;

    private String coverImage;

    private String category;

    private String tags;

    @NotNull(message = "积分奖励不能为空")
    private Integer pointsReward;

    private Integer duration;

    private Integer status;

    private Integer isRecommend;

    private Integer sort;
}
