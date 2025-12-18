package com.red.education.common.controller;

import com.red.education.common.dto.ConfigUpdateDTO;
import com.red.education.common.dto.SystemConfigDTO;
import com.red.education.common.entity.SystemConfig;
import com.red.education.common.result.Result;
import com.red.education.common.service.SystemConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 系统配置Controller（管理员）
 */
@Api(tags = "系统配置管理")
@RestController
@RequestMapping("/admin/config")
@Validated
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @ApiOperation("获取所有配置")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    @GetMapping("/list")
    public Result<List<SystemConfig>> getAllConfigs() {
        List<SystemConfig> configs = systemConfigService.getAllConfigs();
        return Result.success(configs);
    }

    @ApiOperation("获取单个配置")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    @GetMapping("/{key}")
    public Result<String> getConfig(@PathVariable("key") String configKey) {
        String value = systemConfigService.getConfigValue(configKey);
        return Result.success(value);
    }

    @ApiOperation("更新配置")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    @PutMapping
    public Result<Void> updateConfig(@Valid @RequestBody SystemConfigDTO configDTO) {
        systemConfigService.updateConfig(configDTO);
        return Result.<Void>success("配置更新成功");
    }

    @ApiOperation("批量更新配置")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    @PostMapping("/batch")
    public Result<Void> batchUpdateConfigs(@Valid @RequestBody ConfigUpdateDTO updateDTO) {
        systemConfigService.batchUpdateConfigs(updateDTO);
        return Result.<Void>success("批量更新成功");
    }

    @ApiOperation("删除配置")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    @DeleteMapping("/{key}")
    public Result<Void> deleteConfig(@PathVariable("key") String configKey) {
        systemConfigService.deleteConfig(configKey);
        return Result.<Void>success("配置删除成功");
    }

    @ApiOperation("清除配置缓存")
    @PreAuthorize("hasRole('SYS_ADMIN')")
    @DeleteMapping("/cache")
    public Result<Void> clearCache() {
        systemConfigService.clearCache();
        return Result.<Void>success("缓存清除成功");
    }
}
