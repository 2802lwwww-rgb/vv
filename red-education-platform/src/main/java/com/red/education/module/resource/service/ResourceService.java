package com.red.education.module.resource.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.module.resource.dto.AuditResourceDTO;
import com.red.education.module.resource.dto.ResourceDTO;
import com.red.education.module.resource.entity.ResourceCategory;
import com.red.education.module.resource.vo.ResourceDetailVO;
import com.red.education.module.resource.vo.ResourceVO;

import java.util.List;

/**
 * 资源Service接口
 */
public interface ResourceService {

    /**
     * 上传资源
     */
    /**
     * 上传资源
     */
    void uploadResource(Long userId, ResourceDTO resourceDTO);

    /**
     * 管理员上传资源（自动通过）
     */
    void uploadResourceByAdmin(Long userId, ResourceDTO resourceDTO);

    /**
     * 获取资源详情（管理员用，无状态限制）
     */
    ResourceDetailVO getAdminResourceDetail(Long id);

    /**
     * 更新资源
     */
    void updateResource(Long resourceId, ResourceDTO resourceDTO);

    /**
     * 删除资源
     */
    void deleteResource(Long resourceId);

    /**
     * 分页查询资源列表
     */
    Page<ResourceVO> listResources(Integer current, Integer size, Long categoryId, String keyword, Integer status);

    /**
     * 分页查询资源列表（支持排序）
     */
    Page<ResourceVO> listResources(Integer current, Integer size, Long categoryId, String keyword, Integer status,
            String orderBy);

    /**
     * 获取资源详情
     */
    ResourceDetailVO getResourceDetail(Long id);

    /**
     * 增加浏览量
     */
    void addViewCount(Long id);

    /**
     * 增加下载量
     */
    void addDownloadCount(Long id);

    /**
     * 审核资源（管理员）
     */
    void auditResource(Long adminId, Long resourceId, AuditResourceDTO auditResourceDTO);

    /**
     * 获取待审核资源列表（管理员）
     */
    Page<ResourceVO> getPendingResources(Integer current, Integer size);

    /**
     * 获取所有分类
     */
    List<ResourceCategory> getAllCategories();

    /**
     * 获取当前用户的资源列表（包含所有审核状态）
     */
    Page<ResourceVO> getMyResources(Long userId, Integer current, Integer size);
}
