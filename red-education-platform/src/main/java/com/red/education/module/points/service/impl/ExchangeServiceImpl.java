package com.red.education.module.points.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.common.exception.BusinessException;
import com.red.education.module.points.dto.ExchangeDTO;
import com.red.education.module.points.entity.ExchangeRecord;
import com.red.education.module.points.entity.Product;
import com.red.education.module.points.mapper.ExchangeRecordMapper;
import com.red.education.module.points.service.ExchangeService;
import com.red.education.module.points.service.PointService;
import com.red.education.module.points.service.ProductService;
import com.red.education.module.points.vo.ExchangeRecordVO;
import com.red.education.module.user.entity.User;
import com.red.education.module.user.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

/**
 * 兑换服务实现类
 */
@Service
public class ExchangeServiceImpl implements ExchangeService {

    @Autowired
    private ExchangeRecordMapper exchangeRecordMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private PointService pointService;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExchangeRecord exchangeProduct(Long userId, ExchangeDTO exchangeDTO) {
        Long productId = exchangeDTO.getProductId();
        Integer quantity = exchangeDTO.getQuantity() != null ? exchangeDTO.getQuantity() : 1;

        // 1. 获取商品信息
        Product product = productService.getProductById(productId);

        // 2. 检查商品状态
        if (product.getStatus() == 0) {
            throw new BusinessException("商品已下架");
        }

        // 3. 检查库存
        if (product.getStock() < quantity) {
            throw new BusinessException("商品库存不足");
        }

        // 4. 计算所需积分
        int totalPoints = product.getPointsRequired() * quantity;

        // 5. 检查用户积分
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getPoints() < totalPoints) {
            throw new BusinessException("积分不足，当前积分：" + user.getPoints() + "，需要积分：" + totalPoints);
        }

        // 6. 减少商品库存（使用乐观锁）
        boolean stockDecreased = productService.decreaseStock(productId, quantity);
        if (!stockDecreased) {
            throw new BusinessException("兑换失败，商品库存不足或商品已下架");
        }

        // 7. 扣除用户积分
        pointService.addPoints(userId, -totalPoints, "EXCHANGE", productId,
                "兑换商品：" + product.getName());

        // 8. 生成取件码（实体商品）
        String pickupCode = null;
        if (product.getType() == 2) { // 实体商品
            pickupCode = generatePickupCode();
        }

        // 9. 创建兑换记录
        ExchangeRecord record = new ExchangeRecord();
        record.setUserId(userId);
        record.setProductId(productId);
        record.setProductName(product.getName());
        record.setProductType(product.getType());
        record.setPointsCost(totalPoints);
        record.setPickupCode(pickupCode);
        record.setStatus(0); // 待发货

        exchangeRecordMapper.insert(record);

        return record;
    }

    @Override
    public Page<ExchangeRecordVO> getMyExchangeRecords(Long userId, Integer current, Integer size) {
        Page<ExchangeRecord> page = new Page<>(current, size);

        LambdaQueryWrapper<ExchangeRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExchangeRecord::getUserId, userId)
                .orderByDesc(ExchangeRecord::getCreateTime);

        Page<ExchangeRecord> recordPage = exchangeRecordMapper.selectPage(page, queryWrapper);

        // 转换为VO
        Page<ExchangeRecordVO> voPage = new Page<>(recordPage.getCurrent(), recordPage.getSize(),
                recordPage.getTotal());
        voPage.setRecords(recordPage.getRecords().stream().map(this::convertToVO).toList());

        return voPage;
    }

    @Override
    public ExchangeRecordVO getExchangeDetail(Long userId, Long exchangeId) {
        ExchangeRecord record = exchangeRecordMapper.selectById(exchangeId);
        if (record == null) {
            throw new BusinessException("兑换记录不存在");
        }

        // 验证是否是本人的兑换记录
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException("无权查看此兑换记录");
        }

        return convertToVO(record);
    }

    @Override
    public void updateExchangeStatus(Long exchangeId, Integer status) {
        ExchangeRecord record = exchangeRecordMapper.selectById(exchangeId);
        if (record == null) {
            throw new BusinessException("兑换记录不存在");
        }

        record.setStatus(status);
        exchangeRecordMapper.updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExchangeRecordVO verifyPickupCode(String pickupCode) {
        // 1. 根据取件码查询记录
        LambdaQueryWrapper<ExchangeRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExchangeRecord::getPickupCode, pickupCode);
        ExchangeRecord record = exchangeRecordMapper.selectOne(queryWrapper);

        if (record == null) {
            throw new BusinessException("取件码无效或不存在");
        }

        // 2. 验证是否为实体商品
        if (record.getProductType() != 2) {
            throw new BusinessException("该记录不是实体商品，无需核销");
        }

        // 3. 验证状态
        if (record.getStatus() == 2) {
            throw new BusinessException("该取件码已核销");
        }

        // 4. 更新状态为已完成
        record.setStatus(2);
        exchangeRecordMapper.updateById(record);

        return convertToVO(record);
    }

    @Override
    public Page<ExchangeRecordVO> getAllExchangeRecords(Integer current, Integer size, String pickupCode,
            Integer status) {
        Page<ExchangeRecord> page = new Page<>(current, size);

        LambdaQueryWrapper<ExchangeRecord> queryWrapper = new LambdaQueryWrapper<>();

        // 可选条件：取件码
        if (pickupCode != null && !pickupCode.trim().isEmpty()) {
            queryWrapper.like(ExchangeRecord::getPickupCode, pickupCode);
        }

        // 可选条件：状态
        if (status != null) {
            queryWrapper.eq(ExchangeRecord::getStatus, status);
        }

        queryWrapper.orderByDesc(ExchangeRecord::getCreateTime);

        Page<ExchangeRecord> recordPage = exchangeRecordMapper.selectPage(page, queryWrapper);

        // 转换为VO
        Page<ExchangeRecordVO> voPage = new Page<>(recordPage.getCurrent(), recordPage.getSize(),
                recordPage.getTotal());
        voPage.setRecords(recordPage.getRecords().stream().map(this::convertToVO).toList());

        return voPage;
    }

    /**
     * 生成取件码（8位大写字母+数字）
     */
    private String generatePickupCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }

        // 检查取件码是否已存在（极小概率）
        LambdaQueryWrapper<ExchangeRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExchangeRecord::getPickupCode, code.toString());
        Long count = exchangeRecordMapper.selectCount(queryWrapper);

        if (count > 0) {
            // 如果存在冲突，递归重新生成
            return generatePickupCode();
        }

        return code.toString();
    }

    /**
     * 转换为VO
     */
    private ExchangeRecordVO convertToVO(ExchangeRecord record) {
        ExchangeRecordVO vo = new ExchangeRecordVO();
        BeanUtils.copyProperties(record, vo);

        // 设置类型名称
        if (record.getProductType() != null) {
            vo.setProductTypeName(record.getProductType() == 1 ? "虚拟商品" : "实体商品");
        }

        // 设置状态名称
        if (record.getStatus() != null) {
            switch (record.getStatus()) {
                case 0:
                    vo.setStatusName("待发货");
                    break;
                case 1:
                    vo.setStatusName("已发货");
                    break;
                case 2:
                    vo.setStatusName("已完成");
                    break;
                default:
                    vo.setStatusName("未知");
            }
        }

        // 查询用户信息
        User user = userMapper.selectById(record.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
        }

        return vo;
    }
}
