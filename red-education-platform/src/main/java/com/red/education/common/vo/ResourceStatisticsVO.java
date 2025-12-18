package com.red.education.common.vo;

import lombok.Data;

/**
 * 资源统计VO
 */
@Data
public class ResourceStatisticsVO {

    /**
     * 资源总数
     */
    private Long totalResources;

    /**
     * 党史文献数量
     */
    private Long partyHistoryCount;

    /**
     * 英雄事迹数量
     */
    private Long heroStoryCount;

    /**
     * 时政热点数量
     */
    private Long currentAffairsCount;

    /**
     * 视频资源数量
     */
    private Long videoCount;

    /**
     * 总浏览量
     */
    private Long totalViews;

    /**
     * 待审核资源数
     */
    private Long pendingAudit;
}
