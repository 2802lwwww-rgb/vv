package com.red.education.module.points.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.red.education.module.points.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 商品Mapper接口
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 减少商品库存（使用乐观锁）
     * 
     * @param productId 商品ID
     * @param quantity  减少数量
     * @return 影响行数
     */
    @Update("UPDATE product SET stock = stock - #{quantity}, exchange_count = exchange_count + #{quantity} " +
            "WHERE id = #{productId} AND stock >= #{quantity} AND status = 1")
    int decreaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}
