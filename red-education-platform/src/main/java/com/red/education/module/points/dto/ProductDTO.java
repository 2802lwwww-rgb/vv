package com.red.education.module.points.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 商品DTO
 */
@Data
public class ProductDTO {

    /**
     * 商品ID（更新时必填）
     */
    private Long id;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String name;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品图片URL
     */
    private String image;

    /**
     * 类型：1-虚拟商品, 2-实体商品
     */
    @NotNull(message = "商品类型不能为空")
    @Min(value = 1, message = "商品类型必须为1或2")
    private Integer type;

    /**
     * 所需积分
     */
    @NotNull(message = "所需积分不能为空")
    @Min(value = 1, message = "所需积分必须大于0")
    private Integer pointsRequired;

    /**
     * 库存
     */
    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;

    /**
     * 状态：0-下架, 1-上架
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;
}
