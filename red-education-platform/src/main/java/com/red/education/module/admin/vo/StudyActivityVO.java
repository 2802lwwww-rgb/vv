package com.red.education.module.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学习活跃度数据VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("学习活跃度数据")
public class StudyActivityVO {

    @ApiModelProperty("日期（格式：MM-DD）")
    private String date;

    @ApiModelProperty("学习分钟数")
    private Integer minutes;
}
