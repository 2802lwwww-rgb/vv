package com.red.education.module.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.red.education.module.course.dto.StudyRecordDTO;
import com.red.education.module.course.entity.StudyRecord;

import java.util.List;

/**
 * 学习记录服务接口
 */
public interface StudyRecordService extends IService<StudyRecord> {

    /**
     * 获取用户对某课程的学习记录
     *
     * @param userId   用户ID
     * @param courseId 课程ID
     * @return 学习记录
     */
    StudyRecord getUserStudyRecord(Long userId, Long courseId);

    /**
     * 更新学习进度
     *
     * @param userId    用户ID
     * @param recordDTO 进度信息
     */
    void updateProgress(Long userId, StudyRecordDTO recordDTO);

    /**
     * 获取用户的学习历史（包含课程信息）
     *
     * @param userId 用户ID
     * @return 学习历史列表
     */
    List<com.red.education.module.course.vo.StudyHistoryVO> getUserStudyHistory(Long userId);

    /**
     * 获取用户学习报告
     *
     * @param userId 用户ID
     * @return 学习报告
     */
    com.red.education.module.course.vo.StudyReportVO getStudyReport(Long userId);
}
