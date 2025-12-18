package com.red.education.module.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 考试通过率统计VO
 */
@Data
@ApiModel("考试通过率统计")
public class ExamPassRateVO {

    @ApiModelProperty("总考试次数")
    private Long totalExams;

    @ApiModelProperty("及格次数")
    private Long passCount;

    @ApiModelProperty("不及格次数")
    private Long failCount;

    @ApiModelProperty("通过率百分比")
    private Double passRate;
}
