package com.red.education.module.resource.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资源实体
 */
@Data
@TableName("resource")
public class Resource {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 资源分类ID
     */
    private Long categoryId;

    /**
     * 资源标题
     */
    private String title;

    /**
     * 资源描述
     */
    private String description;

    /**
     * 详细内容
     */
    private String content;

    /**
     * 封面图URL
     */
    private String coverImage;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 文件类型：PDF, DOC, DOCX, MP4等
     */
    private String fileType;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 标签（逗号分隔）
     */
    private String tags;

    /**
     * 上传者ID
     */
    private Long uploaderId;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 状态：0-待审核, 1-已通过, 2-已驳回
     */
    private Integer status;

    /**
     * 审核管理员ID
     */
    private Long auditAdminId;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 审核意见
     */
    private String auditComment;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
