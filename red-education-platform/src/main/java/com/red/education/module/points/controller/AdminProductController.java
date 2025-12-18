package com.red.education.module.points.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.common.result.Result;
import com.red.education.module.points.dto.ProductDTO;
import com.red.education.module.points.dto.ProductQueryDTO;
import com.red.education.module.points.service.ProductService;
import com.red.education.module.points.vo.ProductVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 商品管理Controller（管理员）
 */
@Api(tags = "商品管理")
@RestController
@RequestMapping("/admin/goods")
@Validated
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @ApiOperation("创建商品")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    @PostMapping
    public Result<Void> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        productService.createProduct(productDTO);
        return Result.<Void>success("创建成功");
    }

    @ApiOperation("更新商品")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    @PutMapping
    public Result<Void> updateProduct(@Valid @RequestBody ProductDTO productDTO) {
        productService.updateProduct(productDTO);
        return Result.<Void>success("更新成功");
    }

    @ApiOperation("删除商品")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.<Void>success("删除成功");
    }

    @ApiOperation("更新商品状态")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    @PutMapping("/{id}/status/{status}")
    public Result<Void> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        productService.updateStatus(id, status);
        return Result.<Void>success("状态更新成功");
    }

    @ApiOperation("商品列表（含下架商品）")
    @PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    @GetMapping("/list")
    public Result<Page<ProductVO>> listAllProducts(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            ProductQueryDTO queryDTO) {
        Page<ProductVO> page = productService.listProducts(current, size, queryDTO);
        return Result.success(page);
    }
}
