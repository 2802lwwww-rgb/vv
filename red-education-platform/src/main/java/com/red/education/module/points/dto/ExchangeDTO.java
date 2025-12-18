package com.red.education.module.points.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 兑换DTO
 */
@Data
public class ExchangeDTO {

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    /**
     * 兑换数量（默认为1，预留扩展）
     */
    @Min(value = 1, message = "兑换数量必须大于0")
    private Integer quantity = 1;
}
