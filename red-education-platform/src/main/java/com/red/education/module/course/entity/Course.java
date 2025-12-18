package com.red.education.module.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课程实体类
 */
@Data
@TableName("course")
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String intro;

    private String content;

    private String videoUrl;

    private String coverImage;

    private String category;

    private String tags;

    private Integer pointsReward;

    private Integer duration;

    private Integer status;

    private Integer isRecommend;

    private Integer sort;

    private Integer viewCount;

    private Integer completeCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
