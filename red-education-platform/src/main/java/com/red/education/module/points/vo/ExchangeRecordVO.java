package com.red.education.module.points.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 兑换记录VO
 */
@Data
public class ExchangeRecordVO {

    /**
     * 兑换记录ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品类型：1-虚拟商品, 2-实体商品
     */
    private Integer productType;

    /**
     * 商品类型名称
     */
    private String productTypeName;

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
     * 状态名称
     */
    private String statusName;

    /**
     * 兑换时间
     */
    private LocalDateTime createTime;
}
