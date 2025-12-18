package com.red.education.module.points.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 兑换记录实体类
 */
@Data
@TableName("exchange_record")
public class ExchangeRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称（冗余字段，防止商品删除后无法查看）
     */
    private String productName;

    /**
     * 商品类型：1-虚拟商品, 2-实体商品
     */
    private Integer productType;

    /**
     * 消耗积分
     */
    private Integer pointsCost;

    /**
     * 取件码（实体商品专用）
     */
    private String pickupCode;

    /**
     * 状态：0-待发货, 1-已发货, 2-已完成
     */
    private Integer status;

    /**
     * 兑换时间
     */
    private LocalDateTime createTime;
}
