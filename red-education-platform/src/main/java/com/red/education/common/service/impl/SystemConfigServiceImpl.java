package com.red.education.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.red.education.common.dto.ConfigUpdateDTO;
import com.red.education.common.dto.SystemConfigDTO;
import com.red.education.common.entity.SystemConfig;
import com.red.education.common.exception.BusinessException;
import com.red.education.common.mapper.SystemConfigMapper;
import com.red.education.common.service.SystemConfigService;
import com.red.education.common.utils.RedisUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 系统配置服务实现类
 */
@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    @Autowired
    private RedisUtils redisUtils;

    private static final String CACHE_KEY_PREFIX = "system:config:";
    private static final long CACHE_EXPIRE_HOURS = 24;

    // 公开配置的键前缀
    private static final String[] PUBLIC_CONFIG_PREFIXES = { "site.", "upload." };

    @Override
    public String getConfigValue(String configKey) {
        return getConfigValue(configKey, null);
    }

    @Override
    public String getConfigValue(String configKey, String defaultValue) {
        // 先从缓存获取
        String cacheKey = CACHE_KEY_PREFIX + configKey;
        Object cachedValue = redisUtils.get(cacheKey);
        if (cachedValue != null) {
            return cachedValue.toString();
        }

        // 从数据库获取
        LambdaQueryWrapper<SystemConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemConfig::getConfigKey, configKey);
        SystemConfig config = systemConfigMapper.selectOne(queryWrapper);

        if (config == null || config.getConfigValue() == null) {
            return defaultValue;
        }

        // 写入缓存
        redisUtils.set(cacheKey, config.getConfigValue(), CACHE_EXPIRE_HOURS, TimeUnit.HOURS);

        return config.getConfigValue();
    }

    @Override
    public List<SystemConfig> getAllConfigs() {
        return systemConfigMapper.selectList(null);
    }

    @Override
    public Map<String, String> getPublicConfigs() {
        List<SystemConfig> allConfigs = getAllConfigs();
        Map<String, String> publicConfigs = new HashMap<>();

        for (SystemConfig config : allConfigs) {
            // 只返回公开的配置（以site.开头）
            for (String prefix : PUBLIC_CONFIG_PREFIXES) {
                if (config.getConfigKey().startsWith(prefix)) {
                    publicConfigs.put(config.getConfigKey(), config.getConfigValue());
                    break;
                }
            }
        }

        return publicConfigs;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(SystemConfigDTO configDTO) {
        LambdaQueryWrapper<SystemConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemConfig::getConfigKey, configDTO.getConfigKey());
        SystemConfig existingConfig = systemConfigMapper.selectOne(queryWrapper);

        if (existingConfig == null) {
            // 新建配置
            SystemConfig newConfig = new SystemConfig();
            BeanUtils.copyProperties(configDTO, newConfig);
            systemConfigMapper.insert(newConfig);
        } else {
            // 更新配置
            existingConfig.setConfigValue(configDTO.getConfigValue());
            if (configDTO.getDescription() != null) {
                existingConfig.setDescription(configDTO.getDescription());
            }
            systemConfigMapper.updateById(existingConfig);
        }

        // 清除缓存（忽略Redis异常）
        try {
            clearCache(configDTO.getConfigKey());
        } catch (Exception e) {
            // Redis不可用时忽略缓存清除，不影响数据保存
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateConfigs(ConfigUpdateDTO updateDTO) {
        for (SystemConfigDTO configDTO : updateDTO.getConfigs()) {
            updateConfig(configDTO);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(String configKey) {
        LambdaQueryWrapper<SystemConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemConfig::getConfigKey, configKey);
        SystemConfig config = systemConfigMapper.selectOne(queryWrapper);

        if (config == null) {
            throw new BusinessException("配置不存在");
        }

        systemConfigMapper.deleteById(config.getId());

        // 清除缓存
        clearCache(configKey);
    }

    @Override
    public void clearCache() {
        // 清除所有配置缓存
        try {
            redisUtils.deleteByPattern(CACHE_KEY_PREFIX + "*");
        } catch (Exception e) {
            // 忽略缓存清除异常，不影响主业务
        }
    }

    @Override
    public void clearCache(String configKey) {
        try {
            String cacheKey = CACHE_KEY_PREFIX + configKey;
            redisUtils.delete(cacheKey);
        } catch (Exception e) {
            // 忽略缓存清除异常
        }
    }
}
