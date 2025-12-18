package com.red.education.module.points.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.module.points.dto.ExchangeDTO;
import com.red.education.module.points.entity.ExchangeRecord;
import com.red.education.module.points.vo.ExchangeRecordVO;

/**
 * 兑换服务接口
 */
public interface ExchangeService {

    /**
     * 兑换商品
     *
     * @param userId      用户ID
     * @param exchangeDTO 兑换信息
     * @return 兑换记录
     */
    ExchangeRecord exchangeProduct(Long userId, ExchangeDTO exchangeDTO);

    /**
     * 获取用户兑换记录（分页）
     *
     * @param userId  用户ID
     * @param current 当前页
     * @param size    每页大小
     * @return 兑换记录列表
     */
    Page<ExchangeRecordVO> getMyExchangeRecords(Long userId, Integer current, Integer size);

    /**
     * 获取兑换详情
     *
     * @param userId     用户ID
     * @param exchangeId 兑换ID
     * @return 兑换详情
     */
    ExchangeRecordVO getExchangeDetail(Long userId, Long exchangeId);

    /**
     * 更新兑换状态（管理员）
     *
     * @param exchangeId 兑换ID
     * @param status     状态：0-待发货, 1-已发货, 2已完成
     */
    void updateExchangeStatus(Long exchangeId, Integer status);

    /**
     * 核销取件码（管理员）
     *
     * @param pickupCode 取件码
     * @return 兑换记录详情
     */
    ExchangeRecordVO verifyPickupCode(String pickupCode);

    /**
     * 获取所有兑换记录（管理员）
     *
     * @param current    当前页
     * @param size       每页大小
     * @param pickupCode 取件码（可选）
     * @param status     状态（可选）
     * @return 兑换记录列表
     */
    Page<ExchangeRecordVO> getAllExchangeRecords(Integer current, Integer size, String pickupCode, Integer status);
}
