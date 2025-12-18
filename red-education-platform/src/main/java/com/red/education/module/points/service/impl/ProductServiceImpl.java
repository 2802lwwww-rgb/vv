package com.red.education.module.points.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.common.exception.BusinessException;
import com.red.education.module.points.dto.ProductDTO;
import com.red.education.module.points.dto.ProductQueryDTO;
import com.red.education.module.points.entity.Product;
import com.red.education.module.points.mapper.ProductMapper;
import com.red.education.module.points.service.ProductService;
import com.red.education.module.points.vo.ProductVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 商品服务实现类
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public void createProduct(ProductDTO productDTO) {
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);

        // 默认值
        if (product.getStatus() == null) {
            product.setStatus(1); // 默认上架
        }
        if (product.getSort() == null) {
            product.setSort(0);
        }
        if (product.getExchangeCount() == null) {
            product.setExchangeCount(0);
        }

        productMapper.insert(product);
    }

    @Override
    public void updateProduct(ProductDTO productDTO) {
        if (productDTO.getId() == null) {
            throw new BusinessException("商品ID不能为空");
        }

        Product product = productMapper.selectById(productDTO.getId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        BeanUtils.copyProperties(productDTO, product);
        productMapper.updateById(product);
    }

    @Override
    public void deleteProduct(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        productMapper.deleteById(productId);
    }

    @Override
    public void updateStatus(Long productId, Integer status) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        product.setStatus(status);
        productMapper.updateById(product);
    }

    @Override
    public Page<ProductVO> listProducts(Integer current, Integer size, ProductQueryDTO queryDTO) {
        Page<Product> page = new Page<>(current, size);

        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();

        // 查询条件
        if (queryDTO != null) {
            if (queryDTO.getType() != null) {
                queryWrapper.eq(Product::getType, queryDTO.getType());
            }
            if (queryDTO.getStatus() != null) {
                queryWrapper.eq(Product::getStatus, queryDTO.getStatus());
            }
            if (StringUtils.hasText(queryDTO.getKeyword())) {
                queryWrapper.and(wrapper -> wrapper
                        .like(Product::getName, queryDTO.getKeyword())
                        .or()
                        .like(Product::getDescription, queryDTO.getKeyword()));
            }
        }

        // 排序
        queryWrapper.orderByAsc(Product::getSort)
                .orderByDesc(Product::getCreateTime);

        Page<Product> productPage = productMapper.selectPage(page, queryWrapper);

        // 转换为VO
        Page<ProductVO> voPage = new Page<>(productPage.getCurrent(), productPage.getSize(), productPage.getTotal());
        voPage.setRecords(productPage.getRecords().stream().map(this::convertToVO).toList());

        return voPage;
    }

    @Override
    public Product getProductById(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        return product;
    }

    @Override
    public boolean decreaseStock(Long productId, Integer quantity) {
        int rows = productMapper.decreaseStock(productId, quantity);
        return rows > 0;
    }

    /**
     * 转换为VO
     */
    private ProductVO convertToVO(Product product) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);

        // 设置类型名称
        if (product.getType() != null) {
            vo.setTypeName(product.getType() == 1 ? "虚拟商品" : "实体商品");
        }

        return vo;
    }
}
