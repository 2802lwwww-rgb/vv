package com.red.education.module.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 热门内容VO
 */
@Data
@ApiModel("热门内容")
public class PopularItemVO {

    @ApiModelProperty("资源/课程ID")
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("类型：resource-资源, course-课程")
    private String type;

    @ApiModelProperty("数量（下载量或学习次数）")
    private Integer count;

    @ApiModelProperty("封面图URL")
    private String coverImage;
}
