package com.red.education.common.controller;

import com.red.education.common.result.Result;
import com.red.education.common.service.SystemConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 公开配置Controller
 */
@Api(tags = "公开配置")
@RestController
@RequestMapping("/public/config")
public class PublicConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @ApiOperation("获取公开配置")
    @GetMapping
    public Result<Map<String, String>> getPublicConfigs() {
        Map<String, String> publicConfigs = systemConfigService.getPublicConfigs();
        return Result.success(publicConfigs);
    }
}
