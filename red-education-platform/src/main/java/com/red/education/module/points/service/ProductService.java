package com.red.education.module.points.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.module.points.dto.ProductDTO;
import com.red.education.module.points.dto.ProductQueryDTO;
import com.red.education.module.points.entity.Product;
import com.red.education.module.points.vo.ProductVO;

/**
 * 商品服务接口
 */
public interface ProductService {

    /**
     * 创建商品
     *
     * @param productDTO 商品信息
     */
    void createProduct(ProductDTO productDTO);

    /**
     * 更新商品
     *
     * @param productDTO 商品信息
     */
    void updateProduct(ProductDTO productDTO);

    /**
     * 删除商品
     *
     * @param productId 商品ID
     */
    void deleteProduct(Long productId);

    /**
     * 更新商品状态（上下架）
     *
     * @param productId 商品ID
     * @param status    状态：0-下架, 1-上架
     */
    void updateStatus(Long productId, Integer status);

    /**
     * 分页查询商品列表
     *
     * @param current  当前页
     * @param size     每页大小
     * @param queryDTO 查询条件
     * @return 商品列表
     */
    Page<ProductVO> listProducts(Integer current, Integer size, ProductQueryDTO queryDTO);

    /**
     * 获取商品详情
     *
     * @param productId 商品ID
     * @return 商品详情
     */
    Product getProductById(Long productId);

    /**
     * 减少商品库存（使用乐观锁）
     *
     * @param productId 商品ID
     * @param quantity  数量
     * @return 是否成功
     */
    boolean decreaseStock(Long productId, Integer quantity);
}
