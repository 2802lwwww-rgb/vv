package com.red.education.module.points.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品VO
 */
@Data
public class ProductVO {

    /**
     * 商品ID
     */
    private Long id;

    /**
     * 商品名称
     */
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
    private Integer type;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 所需积分
     */
    private Integer pointsRequired;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 兑换次数
     */
    private Integer exchangeCount;

    /**
     * 状态：0-下架, 1-上架
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
