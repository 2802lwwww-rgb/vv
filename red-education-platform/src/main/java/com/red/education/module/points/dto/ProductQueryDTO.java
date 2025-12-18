package com.red.education.module.points.dto;

import lombok.Data;

/**
 * 商品查询DTO
 */
@Data
public class ProductQueryDTO {

    /**
     * 商品类型：1-虚拟商品, 2-实体商品
     */
    private Integer type;

    /**
     * 状态：0-下架, 1-上架
     */
    private Integer status;

    /**
     * 关键词搜索（商品名称或描述）
     */
    private String keyword;
}
