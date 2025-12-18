package com.red.education.module.points.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.common.result.Result;
import com.red.education.module.points.dto.ExchangeDTO;
import com.red.education.module.points.entity.ExchangeRecord;
import com.red.education.module.points.service.ExchangeService;
import com.red.education.module.points.vo.ExchangeRecordVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 兑换Controller
 */
@Api(tags = "积分兑换")
@RestController
@RequestMapping("/exchange")
@Validated
public class ExchangeController {

    @Autowired
    private ExchangeService exchangeService;

    @ApiOperation("兑换商品")
    @PostMapping
    public Result<ExchangeRecord> exchangeProduct(
            Authentication authentication,
            @Valid @RequestBody ExchangeDTO exchangeDTO) {
        Long userId = (Long) authentication.getPrincipal();
        ExchangeRecord record = exchangeService.exchangeProduct(userId, exchangeDTO);
        return Result.success(record);
    }

    @ApiOperation("我的兑换记录")
    @GetMapping("/my")
    public Result<Page<ExchangeRecordVO>> getMyExchangeRecords(
            Authentication authentication,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) authentication.getPrincipal();
        Page<ExchangeRecordVO> page = exchangeService.getMyExchangeRecords(userId, current, size);
        return Result.success(page);
    }

    @ApiOperation("兑换详情")
    @GetMapping("/{id}")
    public Result<ExchangeRecordVO> getExchangeDetail(
            Authentication authentication,
            @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        ExchangeRecordVO detail = exchangeService.getExchangeDetail(userId, id);
        return Result.success(detail);
    }

    @ApiOperation("更新兑换状态（管理员）")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    @PutMapping("/{id}/status/{status}")
    public Result<Void> updateExchangeStatus(
            @PathVariable Long id,
            @PathVariable Integer status) {
        exchangeService.updateExchangeStatus(id, status);
        return Result.<Void>success("状态更新成功");
    }

    @ApiOperation("核销取件码（管理员）")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    @PostMapping("/verify/{pickupCode}")
    public Result<ExchangeRecordVO> verifyPickupCode(@PathVariable String pickupCode) {
        ExchangeRecordVO record = exchangeService.verifyPickupCode(pickupCode);
        return Result.success(record);
    }

    @ApiOperation("获取所有兑换记录（管理员）")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('SYS_ADMIN', 'CONTENT_ADMIN')")
    @GetMapping("/admin/list")
    public Result<Page<ExchangeRecordVO>> getAllExchangeRecords(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String pickupCode,
            @RequestParam(required = false) Integer status) {
        Page<ExchangeRecordVO> page = exchangeService.getAllExchangeRecords(current, size, pickupCode, status);
        return Result.success(page);
    }
}
