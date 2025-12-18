package com.red.education.module.resource.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资源VO
 */
@Data
public class ResourceVO {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String description;
    private String coverImage;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String tags;
    private Long uploaderId;
    private String uploaderName;
    private Integer viewCount;
    private Integer downloadCount;
    private Integer status;
    private LocalDateTime createTime;
}
