package com.red.education.common.service;

import com.red.education.common.dto.ConfigUpdateDTO;
import com.red.education.common.dto.SystemConfigDTO;
import com.red.education.common.entity.SystemConfig;

import java.util.List;
import java.util.Map;

/**
 * 系统配置服务接口
 */
public interface SystemConfigService {

    /**
     * 获取配置值
     *
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValue(String configKey);

    /**
     * 获取配置值（带默认值）
     *
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    String getConfigValue(String configKey, String defaultValue);

    /**
     * 获取所有配置
     *
     * @return 配置列表
     */
    List<SystemConfig> getAllConfigs();

    /**
     * 获取公开配置（不需要登录）
     *
     * @return 配置Map
     */
    Map<String, String> getPublicConfigs();

    /**
     * 更新配置
     *
     * @param configDTO 配置信息
     */
    void updateConfig(SystemConfigDTO configDTO);

    /**
     * 批量更新配置
     *
     * @param updateDTO 批量更新DTO
     */
    void batchUpdateConfigs(ConfigUpdateDTO updateDTO);

    /**
     * 删除配置
     *
     * @param configKey 配置键
     */
    void deleteConfig(String configKey);

    /**
     * 清除所有配置缓存
     */
    void clearCache();

    /**
     * 清除指定配置缓存
     *
     * @param configKey 配置键
     */
    void clearCache(String configKey);
}
