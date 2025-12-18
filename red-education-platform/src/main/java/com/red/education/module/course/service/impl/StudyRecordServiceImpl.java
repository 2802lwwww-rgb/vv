package com.red.education.module.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.red.education.module.course.dto.StudyRecordDTO;
import com.red.education.module.course.entity.Course;
import com.red.education.module.course.entity.DailyStudyLog;
import com.red.education.module.course.entity.StudyRecord;
import com.red.education.module.course.mapper.CourseMapper;
import com.red.education.module.course.mapper.DailyStudyLogMapper;
import com.red.education.module.course.mapper.StudyRecordMapper;
import com.red.education.module.course.service.StudyRecordService;
import com.red.education.module.points.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学习记录服务实现类
 */
@Service
public class StudyRecordServiceImpl extends ServiceImpl<StudyRecordMapper, StudyRecord> implements StudyRecordService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private PointService pointService;

    @Autowired
    private DailyStudyLogMapper dailyStudyLogMapper;

    @Override
    public StudyRecord getUserStudyRecord(Long userId, Long courseId) {
        LambdaQueryWrapper<StudyRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyRecord::getUserId, userId)
                .eq(StudyRecord::getCourseId, courseId);
        return this.getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProgress(Long userId, StudyRecordDTO recordDTO) {
        // 1. 检查课程是否存在
        Course course = courseMapper.selectById(recordDTO.getCourseId());
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }

        // 2. 查询现有记录
        StudyRecord record = this.getUserStudyRecord(userId, recordDTO.getCourseId());
        boolean isNew = false;

        if (record == null) {
            isNew = true;
            record = new StudyRecord();
            record.setUserId(userId);
            record.setCourseId(recordDTO.getCourseId());
            record.setCreateTime(LocalDateTime.now());
            record.setIsCompleted(0);
            record.setProgress(0);
            record.setStudyTime(0);
        }

        // 3. 计算本次学习时长（支持 duration 和 studyTime 两种字段）
        int addedMinutes = 0;
        if (recordDTO.getDuration() != null && recordDTO.getDuration() > 0) {
            // 前端发送的是秒，转换为分钟
            addedMinutes = recordDTO.getDuration() / 60;
        } else if (recordDTO.getStudyTime() != null && recordDTO.getStudyTime() > 0) {
            addedMinutes = recordDTO.getStudyTime();
        }

        // 4. 累加学习时长（而非覆盖）
        int currentStudyTime = record.getStudyTime() != null ? record.getStudyTime() : 0;
        record.setStudyTime(currentStudyTime + addedMinutes);
        record.setLastPosition(recordDTO.getLastPosition());

        // 5. 更新每日学习日志
        if (addedMinutes > 0) {
            updateDailyStudyLog(userId, addedMinutes);
        }

        // 如果进度更大，则更新进度（避免重复播放导致进度回退）
        Integer currentProgress = record.getProgress() == null ? 0 : record.getProgress();
        if (recordDTO.getProgress() != null && recordDTO.getProgress() > currentProgress) {
            record.setProgress(recordDTO.getProgress());
        }

        // 6. 检查是否完成
        if (record.getIsCompleted() == 0 && record.getProgress() >= 100) {
            record.setIsCompleted(1);
            record.setCompleteTime(LocalDateTime.now());

            // 更新课程完成人数
            course.setCompleteCount(course.getCompleteCount() + 1);
            courseMapper.updateById(course);

            // 发放积分奖励
            Integer pointsReward = course.getPointsReward() != null ? course.getPointsReward() : 10;
            pointService.addPoints(
                    userId,
                    pointsReward,
                    "COURSE_COMPLETE",
                    recordDTO.getCourseId(),
                    "完成课程：" + course.getTitle());
        }

        record.setUpdateTime(LocalDateTime.now());

        // 7. 保存
        if (isNew) {
            this.save(record);
        } else {
            this.updateById(record);
        }
    }

    /**
     * 更新每日学习日志
     */
    private void updateDailyStudyLog(Long userId, int addedMinutes) {
        LocalDate today = LocalDate.now();

        // 查询今日是否已有记录
        LambdaQueryWrapper<DailyStudyLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyStudyLog::getUserId, userId)
                .eq(DailyStudyLog::getStudyDate, today);
        DailyStudyLog log = dailyStudyLogMapper.selectOne(wrapper);

        if (log == null) {
            // 新建今日记录
            log = new DailyStudyLog();
            log.setUserId(userId);
            log.setStudyDate(today);
            log.setStudyMinutes(addedMinutes);
            log.setCreateTime(LocalDateTime.now());
            log.setUpdateTime(LocalDateTime.now());
            dailyStudyLogMapper.insert(log);
        } else {
            // 累加今日学习时长
            log.setStudyMinutes(log.getStudyMinutes() + addedMinutes);
            log.setUpdateTime(LocalDateTime.now());
            dailyStudyLogMapper.updateById(log);
        }
    }

    @Override
    public List<com.red.education.module.course.vo.StudyHistoryVO> getUserStudyHistory(Long userId) {
        LambdaQueryWrapper<StudyRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyRecord::getUserId, userId)
                .orderByDesc(StudyRecord::getUpdateTime);
        List<StudyRecord> records = this.list(wrapper);

        // 转换为VO，并关联课程信息，过滤掉已删除的课程
        return records.stream().map(record -> {
            // 获取课程信息
            Course course = courseMapper.selectById(record.getCourseId());
            if (course == null) {
                // 课程已被删除，跳过此记录
                return null;
            }

            com.red.education.module.course.vo.StudyHistoryVO vo = new com.red.education.module.course.vo.StudyHistoryVO();
            vo.setId(record.getId());
            vo.setUserId(record.getUserId());
            vo.setCourseId(record.getCourseId());
            vo.setStudyTime(record.getStudyTime());
            vo.setTotalDuration(record.getStudyTime()); // 总学习时长（分钟）
            vo.setProgress(record.getProgress());
            vo.setIsCompleted(record.getIsCompleted());
            vo.setLastPosition(record.getLastPosition());
            vo.setLastStudyTime(record.getUpdateTime());
            vo.setCreateTime(record.getCreateTime());

            vo.setCourseTitle(course.getTitle());
            vo.setCourseCover(course.getCoverImage());
            // TODO: 如果有章节表，可以查询章节数量
            vo.setTotalChapters(0);
            vo.setCompletedChapters(0);

            return vo;
        }).filter(vo -> vo != null).collect(Collectors.toList());
    }

    @Override
    public com.red.education.module.course.vo.StudyReportVO getStudyReport(Long userId) {
        com.red.education.module.course.vo.StudyReportVO report = new com.red.education.module.course.vo.StudyReportVO();

        // 获取所有学习记录（直接查询，不使用getUserStudyHistory因为返回类型不同）
        LambdaQueryWrapper<StudyRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(StudyRecord::getUserId, userId);
        List<StudyRecord> records = this.list(recordWrapper);

        if (records.isEmpty()) {
            // 如果没有学习记录，返回空报告
            report.setTotalStudyTime(0L);
            report.setCompletedCourses(0);
            report.setStudyDays(0);
            report.setInProgressCourses(0);
            report.setCourseCompletionRate(0.0);
            report.setTotalPoints(0);
            report.setAvgDailyStudyTime(0.0);
            report.setStudyDates(new ArrayList<>());
            report.setDailyStudyTime(new ArrayList<>());
            return report;
        }

        // 1. 总学习时长
        long totalTime = records.stream()
                .mapToLong(r -> r.getStudyTime() != null ? r.getStudyTime() : 0)
                .sum();
        report.setTotalStudyTime(totalTime);

        // 2. 已完成课程数
        int completed = (int) records.stream()
                .filter(r -> r.getIsCompleted() == 1)
                .count();
        report.setCompletedCourses(completed);

        // 3. 学习中的课程数
        int inProgress = (int) records.stream()
                .filter(r -> r.getIsCompleted() == 0)
                .count();
        report.setInProgressCourses(inProgress);

        // 4. 课程完成率
        double completionRate = records.size() > 0 ? (double) completed / records.size() * 100 : 0.0;
        report.setCourseCompletionRate(Double.parseDouble(String.format("%.2f", completionRate)));

        // 5. 从每日学习日志获取最近30天的真实数据
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(29);

        List<DailyStudyLog> dailyLogs = dailyStudyLogMapper.findByUserIdAndDateRange(userId, startDate, today);

        // 将日志转换为Map方便查询
        Map<LocalDate, Integer> dailyLogMap = dailyLogs.stream()
                .collect(Collectors.toMap(DailyStudyLog::getStudyDate, DailyStudyLog::getStudyMinutes));

        // 6. 学习天数统计（有真实学习记录的天数）
        int studyDays = dailyLogMap.size();
        report.setStudyDays(studyDays);

        // 7. 平均每天学习时长（按30天计算）
        int totalDailyMinutes = dailyLogMap.values().stream().mapToInt(Integer::intValue).sum();
        double avgDaily = totalDailyMinutes / 30.0;
        report.setAvgDailyStudyTime(Double.parseDouble(String.format("%.2f", avgDaily)));

        // 8. 构建最近30天的日期和学习时长列表
        List<String> dates = new ArrayList<>();
        List<Integer> dailyTimes = new ArrayList<>();

        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            dates.add(date.toString());
            dailyTimes.add(dailyLogMap.getOrDefault(date, 0));
        }
        report.setStudyDates(dates);
        report.setDailyStudyTime(dailyTimes);

        // 9. 获得积分总数（简化处理）
        report.setTotalPoints(completed * 10);

        // 10. 计算学习勤奋度（0-5星）
        // 以120分钟（2小时）日均学习为满分5星
        double diligenceScore = Math.min(5.0, (avgDaily / 120.0) * 5.0);
        report.setDiligenceScore(Double.parseDouble(String.format("%.1f", diligenceScore)));

        return report;
    }
}
