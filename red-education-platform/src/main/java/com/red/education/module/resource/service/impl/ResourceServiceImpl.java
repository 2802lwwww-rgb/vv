package com.red.education.module.resource.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.common.exception.BusinessException;
import com.red.education.module.points.service.PointService;
import com.red.education.module.resource.dto.AuditResourceDTO;
import com.red.education.module.resource.dto.ResourceDTO;
import com.red.education.module.resource.entity.Resource;
import com.red.education.module.resource.entity.ResourceCategory;
import com.red.education.module.resource.mapper.ResourceCategoryMapper;
import com.red.education.module.resource.mapper.ResourceMapper;
import com.red.education.module.resource.service.ResourceService;
import com.red.education.module.resource.vo.ResourceDetailVO;
import com.red.education.module.resource.vo.ResourceVO;
import com.red.education.module.user.entity.User;
import com.red.education.module.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 资源Service实现类
 */
@Slf4j
@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private ResourceCategoryMapper categoryMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PointService pointService;

    @Autowired
    private com.red.education.common.service.FileCleanupService fileCleanupService;

    @Override
    public void uploadResource(Long userId, ResourceDTO resourceDTO) {
        Resource resource = new Resource();
        BeanUtils.copyProperties(resourceDTO, resource);
        resource.setUploaderId(userId);
        resource.setStatus(0); // 待审核
        resource.setViewCount(0);
        resource.setDownloadCount(0);

        resourceMapper.insert(resource);
        log.info("用户{}上传资源成功，资源ID={}", userId, resource.getId());
    }

    @Override
    public void uploadResourceByAdmin(Long userId, ResourceDTO resourceDTO) {
        Resource resource = new Resource();
        BeanUtils.copyProperties(resourceDTO, resource);
        resource.setUploaderId(userId);
        resource.setStatus(1); // 管理员上传直接通过
        resource.setViewCount(0);
        resource.setDownloadCount(0);
        resource.setAuditAdminId(userId);
        resource.setAuditTime(java.time.LocalDateTime.now());
        resource.setAuditComment("管理员上传自动通过");

        resourceMapper.insert(resource);
        log.info("管理员{}上传资源成功（自动通过），资源ID={}", userId, resource.getId());
    }

    @Override
    public void updateResource(Long resourceId, ResourceDTO resourceDTO) {
        Resource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new BusinessException("资源不存在");
        }

        // 调试日志
        log.info("更新资源{}: 接收到的coverImage={}", resourceId, resourceDTO.getCoverImage());

        // 手动设置各字段，确保coverImage被更新
        resource.setTitle(resourceDTO.getTitle());
        resource.setCategoryId(resourceDTO.getCategoryId());
        resource.setDescription(resourceDTO.getDescription());
        resource.setContent(resourceDTO.getContent());
        resource.setTags(resourceDTO.getTags());

        // 更新封面图（如果提供了新值）
        if (resourceDTO.getCoverImage() != null) {
            resource.setCoverImage(resourceDTO.getCoverImage());
        }

        // 更新文件信息（如果提供了新值）
        if (resourceDTO.getFileUrl() != null) {
            resource.setFileUrl(resourceDTO.getFileUrl());
        }
        if (resourceDTO.getFileType() != null) {
            resource.setFileType(resourceDTO.getFileType());
        }
        if (resourceDTO.getFileSize() != null) {
            resource.setFileSize(resourceDTO.getFileSize());
        }

        resourceMapper.updateById(resource);
        log.info("资源{}更新成功, 最终coverImage={}", resourceId, resource.getCoverImage());
    }

    @Override
    public void deleteResource(Long resourceId) {
        // 先获取资源信息，以便删除关联的文件
        Resource resource = resourceMapper.selectById(resourceId);
        if (resource != null) {
            // 删除关联的文件
            fileCleanupService.deleteFiles(resource.getFileUrl(), resource.getCoverImage());
        }

        resourceMapper.deleteById(resourceId);
        log.info("资源{}及其关联文件删除成功", resourceId);
    }

    @Override
    public Page<ResourceVO> listResources(Integer current, Integer size, Long categoryId, String keyword,
            Integer status) {
        // 默认按创建时间排序
        return listResources(current, size, categoryId, keyword, status, "createTime");
    }

    @Override
    public Page<ResourceVO> listResources(Integer current, Integer size, Long categoryId, String keyword,
            Integer status, String orderBy) {
        Page<Resource> page = new Page<>(current, size);
        LambdaQueryWrapper<Resource> wrapper = new LambdaQueryWrapper<>();

        // 状态筛选
        if (status != null) {
            wrapper.eq(Resource::getStatus, status);
        }

        // 分类筛选
        if (categoryId != null) {
            wrapper.eq(Resource::getCategoryId, categoryId);
        }

        // 关键词搜索
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Resource::getTitle, keyword)
                    .or().like(Resource::getDescription, keyword)
                    .or().like(Resource::getTags, keyword));
        }

        // 排序
        if ("viewCount".equals(orderBy)) {
            wrapper.orderByDesc(Resource::getViewCount);
        } else if ("downloadCount".equals(orderBy)) {
            wrapper.orderByDesc(Resource::getDownloadCount);
        } else {
            wrapper.orderByDesc(Resource::getCreateTime);
        }

        Page<Resource> resourcePage = resourceMapper.selectPage(page, wrapper);
        return convertToResourceVOPage(resourcePage);
    }

    @Override
    public ResourceDetailVO getResourceDetail(Long id) {
        Resource resource = resourceMapper.selectById(id);
        if (resource == null) {
            throw new BusinessException("资源不存在");
        }

        if (resource.getStatus() != 1) {
            throw new BusinessException("资源未通过审核");
        }

        ResourceDetailVO vo = new ResourceDetailVO();
        BeanUtils.copyProperties(resource, vo);

        // 设置分类名称
        ResourceCategory category = categoryMapper.selectById(resource.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }

        // 设置上传者名称
        User user = userMapper.selectById(resource.getUploaderId());
        log.info("获取资源详情{}: uploaderId={}, user={}", id, resource.getUploaderId(),
                user != null ? user.getUsername() : "null");
        if (user != null) {
            vo.setUploaderName(user.getUsername());
        }

        return vo;
    }

    @Override
    public ResourceDetailVO getAdminResourceDetail(Long id) {
        Resource resource = resourceMapper.selectById(id);
        if (resource == null) {
            throw new BusinessException("资源不存在");
        }

        ResourceDetailVO vo = new ResourceDetailVO();
        BeanUtils.copyProperties(resource, vo);

        // 设置分类名称
        if (resource.getCategoryId() != null) {
            ResourceCategory category = categoryMapper.selectById(resource.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }

        // 设置上传者信息
        if (resource.getUploaderId() != null) {
            // 这里应该调用UserService获取用户信息，暂时模拟
            // 实际项目中应该注入UserService或UserMapper
            vo.setUploaderName("用户" + resource.getUploaderId());
        }

        return vo;
    }

    @Override
    public void addViewCount(Long id) {
        Resource resource = resourceMapper.selectById(id);
        if (resource != null) {
            resource.setViewCount(resource.getViewCount() + 1);
            resourceMapper.updateById(resource);
        }
    }

    @Override
    public void addDownloadCount(Long id) {
        Resource resource = resourceMapper.selectById(id);
        if (resource != null) {
            Integer currentCount = resource.getDownloadCount();
            resource.setDownloadCount(currentCount == null ? 1 : currentCount + 1);
            resourceMapper.updateById(resource);
            log.info("资源{}下载量+1，当前下载量={}", id, resource.getDownloadCount());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditResource(Long adminId, Long resourceId, AuditResourceDTO auditResourceDTO) {
        Resource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new BusinessException("资源不存在");
        }

        if (resource.getStatus() != 0) {
            throw new BusinessException("该资源已审核");
        }

        // 使用UpdateWrapper只更新审核相关字段，避免影响其他字段
        LambdaUpdateWrapper<Resource> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Resource::getId, resourceId)
                .set(Resource::getStatus, auditResourceDTO.getStatus())
                .set(Resource::getAuditAdminId, adminId)
                .set(Resource::getAuditTime, LocalDateTime.now())
                .set(Resource::getAuditComment, auditResourceDTO.getAuditComment());

        resourceMapper.update(null, updateWrapper);

        // 更新内存中的对象状态，以便后续使用
        resource.setStatus(auditResourceDTO.getStatus());

        // 审核通过，给上传者增加积分 (+5分)
        if (auditResourceDTO.getStatus() == 1) {
            pointService.addPoints(resource.getUploaderId(), 5, "RESOURCE_AUDIT", resourceId, "资源审核通过");
            log.info("资源{}审核通过，用户{}获得5积分", resourceId, resource.getUploaderId());
        }

        log.info("管理员{}审核资源{}，结果={}", adminId, resourceId, auditResourceDTO.getStatus());
    }

    @Override
    public Page<ResourceVO> getPendingResources(Integer current, Integer size) {
        Page<Resource> page = new Page<>(current, size);
        LambdaQueryWrapper<Resource> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(Resource::getStatus, 0);
        wrapper.orderByDesc(Resource::getCreateTime);

        Page<Resource> resourcePage = resourceMapper.selectPage(page, wrapper);
        return convertToResourceVOPage(resourcePage);
    }

    @Override
    public List<ResourceCategory> getAllCategories() {
        LambdaQueryWrapper<ResourceCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(ResourceCategory::getSort);
        return categoryMapper.selectList(wrapper);
    }

    @Override
    public Page<ResourceVO> getMyResources(Long userId, Integer current, Integer size) {
        Page<Resource> page = new Page<>(current, size);
        LambdaQueryWrapper<Resource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resource::getUploaderId, userId);
        wrapper.orderByDesc(Resource::getCreateTime);

        Page<Resource> resourcePage = resourceMapper.selectPage(page, wrapper);
        return convertToResourceVOPage(resourcePage);
    }

    private Page<ResourceVO> convertToResourceVOPage(Page<Resource> resourcePage) {
        List<Resource> resources = resourcePage.getRecords();

        // 批量查询分类信息
        List<Long> categoryIds = resources.stream()
                .map(Resource::getCategoryId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, ResourceCategory> categoryMap;
        if (!categoryIds.isEmpty()) {
            List<ResourceCategory> categories = categoryMapper.selectBatchIds(categoryIds);
            categoryMap = categories.stream()
                    .collect(Collectors.toMap(ResourceCategory::getId, c -> c));
        } else {
            categoryMap = Map.of();
        }

        // 批量查询用户信息
        List<Long> userIds = resources.stream()
                .map(Resource::getUploaderId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, User> userMap;
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));
        } else {
            userMap = Map.of();
        }

        // 转换为VO
        List<ResourceVO> voList = resources.stream().map(resource -> {
            ResourceVO vo = new ResourceVO();
            BeanUtils.copyProperties(resource, vo);

            // 设置分类名称
            ResourceCategory category = categoryMap.get(resource.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }

            // 设置上传者名称
            User user = userMap.get(resource.getUploaderId());
            if (user != null) {
                vo.setUploaderName(user.getUsername());
            }

            return vo;
        }).collect(Collectors.toList());

        Page<ResourceVO> voPage = new Page<>(resourcePage.getCurrent(), resourcePage.getSize(),
                resourcePage.getTotal());
        voPage.setRecords(voList);

        return voPage;
    }
}
