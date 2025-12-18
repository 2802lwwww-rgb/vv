package com.red.education.module.resource.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资源详情VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResourceDetailVO extends ResourceVO {

    /**
     * 详细内容
     */
    private String content;
}
