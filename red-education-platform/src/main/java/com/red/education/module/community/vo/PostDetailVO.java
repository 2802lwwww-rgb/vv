package com.red.education.module.community.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 帖子详情VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PostDetailVO extends PostVO {

    /**
     * 完整内容（不截取）
     */
    private String fullContent;

    /**
     * 图片列表
     */
    private List<String> imageList;
}
