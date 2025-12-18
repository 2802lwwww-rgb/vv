package com.red.education.module.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.red.education.module.admin.service.AdminStatisticsService;
import com.red.education.module.admin.vo.*;
import com.red.education.module.community.entity.Post;
import com.red.education.module.community.mapper.PostMapper;
import com.red.education.module.course.entity.Course;
import com.red.education.module.course.mapper.CourseMapper;
import com.red.education.module.course.mapper.DailyStudyLogMapper;
import com.red.education.module.exam.entity.ExamScore;
import com.red.education.module.exam.mapper.ExamMapper;
import com.red.education.module.exam.mapper.ExamScoreMapper;
import com.red.education.module.resource.entity.Resource;
import com.red.education.module.resource.mapper.ResourceMapper;
import com.red.education.module.user.entity.User;
import com.red.education.module.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 后台统计服务实现
 */
@Service
public class AdminStatisticsServiceImpl implements AdminStatisticsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private DailyStudyLogMapper dailyStudyLogMapper;

    @Autowired
    private ExamScoreMapper examScoreMapper;

    @Override
    public DashboardStatsVO getDashboardStats() {
        DashboardStatsVO stats = new DashboardStatsVO();

        // 统计总用户数
        stats.setTotalUsers(userMapper.selectCount(null));

        // 统计总资源数
        stats.setTotalResources(resourceMapper.selectCount(null));

        // 统计总课程数
        stats.setTotalCourses(courseMapper.selectCount(null));

        // 统计总帖子数
        stats.setTotalPosts(postMapper.selectCount(null));

        // 统计总试卷数
        stats.setTotalExams(examMapper.selectCount(null));

        // 统计待审核帖子数（status = 0）
        LambdaQueryWrapper<Post> postWrapper = new LambdaQueryWrapper<>();
        postWrapper.eq(Post::getStatus, 0);
        stats.setPendingPosts(postMapper.selectCount(postWrapper));

        // 统计待审核资源数（status = 0）
        LambdaQueryWrapper<Resource> resourceWrapper = new LambdaQueryWrapper<>();
        resourceWrapper.eq(Resource::getStatus, 0);
        stats.setPendingResources(resourceMapper.selectCount(resourceWrapper));

        // 在线用户数和今日访问量暂时设置为0（需要Redis或其他缓存支持）
        stats.setOnlineUsers(0);
        stats.setTodayVisits(0);

        // 计算过去6个月的用户增长数据
        stats.setUserGrowthData(calculateUserGrowth());

        // 计算环比增长率
        stats.setUserGrowthRate(calculateUserGrowthRate());
        stats.setResourceGrowthRate(calculateResourceGrowthRate());
        stats.setCourseGrowthRate(calculateCourseGrowthRate());
        stats.setPostGrowthRate(calculatePostGrowthRate());

        // 新增：学习活跃度数据
        stats.setStudyActivityData(getStudyActivityData());

        // 新增：考试通过率统计
        stats.setExamPassRate(getExamPassRate());

        // 新增：热门资源/课程
        stats.setPopularItems(getPopularItems());

        return stats;
    }

    /**
     * 获取学习活跃度数据（近30天）
     */
    private List<StudyActivityVO> getStudyActivityData() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29); // 包含今天共30天

        List<Map<String, Object>> dbResults = dailyStudyLogMapper.getDailyStudyStats(startDate, endDate);

        // 创建完整的30天数据，初始化为0
        List<StudyActivityVO> activityList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = 0; i < 30; i++) {
            LocalDate date = startDate.plusDays(i);
            String dateStr = date.format(formatter);

            // 查找数据库中是否有该日期的数据
            Integer minutes = 0;
            for (Map<String, Object> result : dbResults) {
                Object studyDateObj = result.get("study_date");
                LocalDate studyDate;

                if (studyDateObj instanceof Date) {
                    studyDate = ((Date) studyDateObj).toLocalDate();
                } else if (studyDateObj instanceof LocalDate) {
                    studyDate = (LocalDate) studyDateObj;
                } else {
                    continue;
                }

                if (studyDate.equals(date)) {
                    Object minutesObj = result.get("total_minutes");
                    if (minutesObj instanceof Number) {
                        minutes = ((Number) minutesObj).intValue();
                    }
                    break;
                }
            }

            activityList.add(new StudyActivityVO(dateStr, minutes));
        }

        return activityList;
    }

    /**
     * 获取考试通过率统计
     */
    private ExamPassRateVO getExamPassRate() {
        ExamPassRateVO vo = new ExamPassRateVO();

        // 统计总考试次数
        Long totalExams = examScoreMapper.selectCount(null);
        vo.setTotalExams(totalExams);

        if (totalExams == 0) {
            vo.setPassCount(0L);
            vo.setFailCount(0L);
            vo.setPassRate(0.0);
            return vo;
        }

        // 统计及格次数（pass = 1）
        LambdaQueryWrapper<ExamScore> passWrapper = new LambdaQueryWrapper<>();
        passWrapper.eq(ExamScore::getPass, 1);
        Long passCount = examScoreMapper.selectCount(passWrapper);
        vo.setPassCount(passCount);

        // 统计不及格次数
        Long failCount = totalExams - passCount;
        vo.setFailCount(failCount);

        // 计算通过率
        double passRate = (passCount.doubleValue() / totalExams.doubleValue()) * 100;
        vo.setPassRate(Math.round(passRate * 10) / 10.0); // 保留一位小数

        return vo;
    }

    /**
     * 获取热门资源/课程列表
     */
    private List<PopularItemVO> getPopularItems() {
        List<PopularItemVO> items = new ArrayList<>();

        // 查询Top 5热门资源（按下载量排序，只要已通过的）
        LambdaQueryWrapper<Resource> resourceWrapper = new LambdaQueryWrapper<>();
        resourceWrapper.eq(Resource::getStatus, 1)
                .orderByDesc(Resource::getDownloadCount)
                .last("LIMIT 5");
        List<Resource> topResources = resourceMapper.selectList(resourceWrapper);

        for (Resource resource : topResources) {
            PopularItemVO item = new PopularItemVO();
            item.setId(resource.getId());
            item.setTitle(resource.getTitle());
            item.setType("resource");
            item.setCount(resource.getDownloadCount());
            item.setCoverImage(resource.getCoverImage());
            items.add(item);
        }

        // 查询Top 5热门课程（按浏览量排序，只要已发布的）
        LambdaQueryWrapper<Course> courseWrapper = new LambdaQueryWrapper<>();
        courseWrapper.eq(Course::getStatus, 1)
                .orderByDesc(Course::getViewCount)
                .last("LIMIT 5");
        List<Course> topCourses = courseMapper.selectList(courseWrapper);

        for (Course course : topCourses) {
            PopularItemVO item = new PopularItemVO();
            item.setId(course.getId());
            item.setTitle(course.getTitle());
            item.setType("course");
            item.setCount(course.getViewCount());
            item.setCoverImage(course.getCoverImage());
            items.add(item);
        }

        // 按count降序排序，取前10个
        items.sort((a, b) -> b.getCount().compareTo(a.getCount()));
        if (items.size() > 10) {
            items = items.subList(0, 10);
        }

        return items;
    }

    /**
     * 计算过去6个月的用户增长数据
     */
    private int[] calculateUserGrowth() {
        int[] growth = new int[6];
        LocalDate today = LocalDate.now();

        for (int i = 5; i >= 0; i--) {
            YearMonth month = YearMonth.from(today.minusMonths(i));
            LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
            LocalDateTime endOfMonth = month.atEndOfMonth().atTime(23, 59, 59);

            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.between(User::getCreateTime, startOfMonth, endOfMonth);
            growth[5 - i] = Math.toIntExact(userMapper.selectCount(wrapper));
        }

        return growth;
    }

    /**
     * 计算用户环比增长率
     */
    private Double calculateUserGrowthRate() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        YearMonth lastMonth = currentMonth.minusMonths(1);

        long currentCount = countUsersByMonth(currentMonth);
        long lastCount = countUsersByMonth(lastMonth);

        return calculateGrowthRate(currentCount, lastCount);
    }

    /**
     * 计算资源环比增长率
     */
    private Double calculateResourceGrowthRate() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        YearMonth lastMonth = currentMonth.minusMonths(1);

        long currentCount = countResourcesByMonth(currentMonth);
        long lastCount = countResourcesByMonth(lastMonth);

        return calculateGrowthRate(currentCount, lastCount);
    }

    /**
     * 计算课程环比增长率
     */
    private Double calculateCourseGrowthRate() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        YearMonth lastMonth = currentMonth.minusMonths(1);

        long currentCount = countCoursesByMonth(currentMonth);
        long lastCount = countCoursesByMonth(lastMonth);

        return calculateGrowthRate(currentCount, lastCount);
    }

    /**
     * 计算帖子环比增长率
     */
    private Double calculatePostGrowthRate() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        YearMonth lastMonth = currentMonth.minusMonths(1);

        long currentCount = countPostsByMonth(currentMonth);
        long lastCount = countPostsByMonth(lastMonth);

        return calculateGrowthRate(currentCount, lastCount);
    }

    /**
     * 统计指定月份新增用户数
     */
    private long countUsersByMonth(YearMonth month) {
        LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = month.atEndOfMonth().atTime(23, 59, 59);

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(User::getCreateTime, startOfMonth, endOfMonth);
        return userMapper.selectCount(wrapper);
    }

    /**
     * 统计指定月份新增资源数
     */
    private long countResourcesByMonth(YearMonth month) {
        LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = month.atEndOfMonth().atTime(23, 59, 59);

        LambdaQueryWrapper<Resource> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(Resource::getCreateTime, startOfMonth, endOfMonth);
        return resourceMapper.selectCount(wrapper);
    }

    /**
     * 统计指定月份新增课程数
     */
    private long countCoursesByMonth(YearMonth month) {
        LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = month.atEndOfMonth().atTime(23, 59, 59);

        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(Course::getCreateTime, startOfMonth, endOfMonth);
        return courseMapper.selectCount(wrapper);
    }

    /**
     * 统计指定月份新增帖子数
     */
    private long countPostsByMonth(YearMonth month) {
        LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = month.atEndOfMonth().atTime(23, 59, 59);

        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(Post::getCreateTime, startOfMonth, endOfMonth);
        return postMapper.selectCount(wrapper);
    }

    /**
     * 计算环比增长率
     * 
     * @param current 本期数量
     * @param last    上期数量
     * @return 增长率百分比，如 12.5 表示增长12.5%
     */
    private Double calculateGrowthRate(long current, long last) {
        if (last == 0) {
            // 上期为0，本期有数据则返回100%，无数据返回0%
            return current > 0 ? 100.0 : 0.0;
        }
        double rate = ((double) (current - last) / last) * 100;
        // 保留一位小数
        return Math.round(rate * 10) / 10.0;
    }
}
