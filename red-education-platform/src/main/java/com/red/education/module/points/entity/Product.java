package com.red.education.module.points.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品实体类
 */
@Data
@TableName("product")
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
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
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
