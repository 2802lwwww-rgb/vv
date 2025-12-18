package com.red.education.module.points.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.common.result.Result;
import com.red.education.module.points.dto.ProductQueryDTO;
import com.red.education.module.points.entity.Product;
import com.red.education.module.points.service.ProductService;
import com.red.education.module.points.vo.ProductVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商品Controller（用户端）
 */
@Api(tags = "积分商城")
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @ApiOperation("商品列表")
    @GetMapping("/list")
    public Result<Page<ProductVO>> listProducts(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            ProductQueryDTO queryDTO) {
        // 用户端只显示上架商品
        if (queryDTO == null) {
            queryDTO = new ProductQueryDTO();
        }
        queryDTO.setStatus(1); // 强制只查询上架商品

        Page<ProductVO> page = productService.listProducts(current, size, queryDTO);
        return Result.success(page);
    }

    @ApiOperation("商品详情")
    @GetMapping("/{id}")
    public Result<Product> getProductDetail(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return Result.success(product);
    }
}
