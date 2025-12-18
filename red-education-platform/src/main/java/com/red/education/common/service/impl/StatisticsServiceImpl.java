package com.red.education.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.red.education.common.service.StatisticsService;
import com.red.education.common.vo.*;
import com.red.education.module.community.entity.Post;
import com.red.education.module.community.mapper.CommentMapper;
import com.red.education.module.community.mapper.LikeRecordMapper;
import com.red.education.module.community.mapper.PostMapper;
import com.red.education.module.course.entity.StudyRecord;
import com.red.education.module.course.mapper.CourseMapper;
import com.red.education.module.course.mapper.StudyRecordMapper;
import com.red.education.module.exam.entity.ExamScore;
import com.red.education.module.exam.mapper.ExamMapper;
import com.red.education.module.exam.mapper.ExamScoreMapper;
import com.red.education.module.exam.mapper.QuestionMapper;
import com.red.education.module.points.entity.PointRecord;
import com.red.education.module.points.mapper.PointRecordMapper;
import com.red.education.module.resource.entity.Resource;
import com.red.education.module.resource.mapper.ResourceMapper;
import com.red.education.module.user.entity.User;
import com.red.education.module.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 统计服务实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private StudyRecordMapper studyRecordMapper;

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private ExamScoreMapper examScoreMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private LikeRecordMapper likeRecordMapper;

    @Autowired
    private PointRecordMapper pointRecordMapper;

    @Override
    public OverviewVO getOverview() {
        OverviewVO vo = new OverviewVO();

        // 统计各模块总数
        vo.setTotalUsers(userMapper.selectCount(null));
        vo.setTotalResources(resourceMapper.selectCount(null));
        vo.setTotalCourses(courseMapper.selectCount(null));
        vo.setTotalExams(examMapper.selectCount(null));
        vo.setTotalPosts(postMapper.selectCount(null));

        // 统计总积分发放量
        LambdaQueryWrapper<PointRecord> pointQuery = new LambdaQueryWrapper<>();
        pointQuery.gt(PointRecord::getPoints, 0); // 只统计正积分
        Long totalPoints = pointRecordMapper.selectList(pointQuery).stream()
                .mapToLong(PointRecord::getPoints)
                .sum();
        vo.setTotalPoints(totalPoints);

        return vo;
    }

    @Override
    public UserStatisticsVO getUserStatistics() {
        UserStatisticsVO vo = new UserStatisticsVO();

        // 总用户数
        vo.setTotalUsers(userMapper.selectCount(null));

        // 今日新增
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LambdaQueryWrapper<User> todayQuery = new LambdaQueryWrapper<>();
        todayQuery.ge(User::getCreateTime, todayStart);
        vo.setTodayNewUsers(userMapper.selectCount(todayQuery));

        // 本周新增
        LocalDateTime weekStart = LocalDateTime.of(LocalDate.now().minusDays(7), LocalTime.MIN);
        LambdaQueryWrapper<User> weekQuery = new LambdaQueryWrapper<>();
        weekQuery.ge(User::getCreateTime, weekStart);
        vo.setWeekNewUsers(userMapper.selectCount(weekQuery));

        // 本月新增
        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().minusDays(30), LocalTime.MIN);
        LambdaQueryWrapper<User> monthQuery = new LambdaQueryWrapper<>();
        monthQuery.ge(User::getCreateTime, monthStart);
        vo.setMonthNewUsers(userMapper.selectCount(monthQuery));

        // 活跃用户（近7天有学习记录或发帖的用户）
        LocalDateTime days7Ago = LocalDateTime.now().minusDays(7);
        LambdaQueryWrapper<StudyRecord> activeQuery7 = new LambdaQueryWrapper<>();
        activeQuery7.ge(StudyRecord::getUpdateTime, days7Ago);
        activeQuery7.select(StudyRecord::getUserId).groupBy(StudyRecord::getUserId);
        vo.setActiveUsers7Days((long) studyRecordMapper.selectList(activeQuery7).size());

        // 活跃用户（近30天）
        LocalDateTime days30Ago = LocalDateTime.now().minusDays(30);
        LambdaQueryWrapper<StudyRecord> activeQuery30 = new LambdaQueryWrapper<>();
        activeQuery30.ge(StudyRecord::getUpdateTime, days30Ago);
        activeQuery30.select(StudyRecord::getUserId).groupBy(StudyRecord::getUserId);
        vo.setActiveUsers30Days((long) studyRecordMapper.selectList(activeQuery30).size());

        return vo;
    }

    @Override
    public ResourceStatisticsVO getResourceStatistics() {
        ResourceStatisticsVO vo = new ResourceStatisticsVO();

        // 资源总数
        vo.setTotalResources(resourceMapper.selectCount(null));

        // 按类型统计（假设category_id: 1-党史, 2-英雄, 3-时政, 4-视频）
        LambdaQueryWrapper<Resource> query1 = new LambdaQueryWrapper<>();
        query1.eq(Resource::getCategoryId, 1L);
        vo.setPartyHistoryCount(resourceMapper.selectCount(query1));

        LambdaQueryWrapper<Resource> query2 = new LambdaQueryWrapper<>();
        query2.eq(Resource::getCategoryId, 2L);
        vo.setHeroStoryCount(resourceMapper.selectCount(query2));

        LambdaQueryWrapper<Resource> query3 = new LambdaQueryWrapper<>();
        query3.eq(Resource::getCategoryId, 3L);
        vo.setCurrentAffairsCount(resourceMapper.selectCount(query3));

        LambdaQueryWrapper<Resource> query4 = new LambdaQueryWrapper<>();
        query4.eq(Resource::getCategoryId, 4L);
        vo.setVideoCount(resourceMapper.selectCount(query4));

        // 总浏览量
        Long totalViews = resourceMapper.selectList(null).stream()
                .mapToLong(r -> r.getViewCount() != null ? r.getViewCount() : 0)
                .sum();
        vo.setTotalViews(totalViews);

        // 待审核资源
        LambdaQueryWrapper<Resource> pendingQuery = new LambdaQueryWrapper<>();
        pendingQuery.eq(Resource::getStatus, 0);
        vo.setPendingAudit(resourceMapper.selectCount(pendingQuery));

        return vo;
    }

    @Override
    public StudyStatisticsVO getStudyStatistics() {
        StudyStatisticsVO vo = new StudyStatisticsVO();

        // 课程总数
        vo.setTotalCourses(courseMapper.selectCount(null));

        // 学习人数（有学习记录的用户数）
        LambdaQueryWrapper<StudyRecord> userQuery = new LambdaQueryWrapper<>();
        userQuery.select(StudyRecord::getUserId).groupBy(StudyRecord::getUserId);
        vo.setStudyUserCount((long) studyRecordMapper.selectList(userQuery).size());

        // 总学习时长
        Long totalTime = studyRecordMapper.selectList(null).stream()
                .mapToLong(r -> r.getStudyTime() != null ? r.getStudyTime() : 0)
                .sum();
        vo.setTotalStudyTime(totalTime);

        // 平均学习时长
        Long recordCount = studyRecordMapper.selectCount(null);
        vo.setAvgStudyTime(recordCount > 0 ? (double) totalTime / recordCount : 0.0);

        // 课程完成总数
        LambdaQueryWrapper<StudyRecord> completedQuery = new LambdaQueryWrapper<>();
        completedQuery.eq(StudyRecord::getIsCompleted, 1);
        vo.setCompletedCourses(studyRecordMapper.selectCount(completedQuery));

        // 课程完成率
        Long totalRecords = studyRecordMapper.selectCount(null);
        vo.setCourseCompletionRate(totalRecords > 0 ? (double) vo.getCompletedCourses() / totalRecords * 100 : 0.0);

        return vo;
    }

    @Override
    public ExamStatisticsVO getExamStatistics() {
        ExamStatisticsVO vo = new ExamStatisticsVO();

        // 试卷总数
        vo.setTotalExams(examMapper.selectCount(null));

        // 题目总数
        vo.setTotalQuestions(questionMapper.selectCount(null));

        // 参与考试人数（有成绩记录的用户数）
        LambdaQueryWrapper<ExamScore> userQuery = new LambdaQueryWrapper<>();
        userQuery.select(ExamScore::getUserId).groupBy(ExamScore::getUserId);
        vo.setParticipantCount((long) examScoreMapper.selectList(userQuery).size());

        // 总考试次数
        vo.setTotalExamAttempts(examScoreMapper.selectCount(null));

        // 平均分数
        Double avgScore = examScoreMapper.selectList(null).stream()
                .mapToDouble(ExamScore::getScore)
                .average()
                .orElse(0.0);
        vo.setAvgScore(Double.parseDouble(String.format("%.2f", avgScore)));

        // 及格率
        LambdaQueryWrapper<ExamScore> passQuery = new LambdaQueryWrapper<>();
        passQuery.eq(ExamScore::getPass, 1);
        Long passCount = examScoreMapper.selectCount(passQuery);
        Long totalAttempts = vo.getTotalExamAttempts();
        vo.setPassRate(
                totalAttempts > 0 ? Double.parseDouble(String.format("%.2f", (double) passCount / totalAttempts * 100))
                        : 0.0);

        // 优秀率（90分以上）
        LambdaQueryWrapper<ExamScore> excellentQuery = new LambdaQueryWrapper<>();
        excellentQuery.ge(ExamScore::getScore, 90);
        Long excellentCount = examScoreMapper.selectCount(excellentQuery);
        vo.setExcellentRate(totalAttempts > 0
                ? Double.parseDouble(String.format("%.2f", (double) excellentCount / totalAttempts * 100))
                : 0.0);

        return vo;
    }

    @Override
    public CommunityStatisticsVO getCommunityStatistics() {
        CommunityStatisticsVO vo = new CommunityStatisticsVO();

        // 帖子总数
        vo.setTotalPosts(postMapper.selectCount(null));

        // 评论总数
        vo.setTotalComments(commentMapper.selectCount(null));

        // 点赞总数
        vo.setTotalLikes(likeRecordMapper.selectCount(null));

        // 待审核帖子
        LambdaQueryWrapper<Post> pendingQuery = new LambdaQueryWrapper<>();
        pendingQuery.eq(Post::getStatus, 0);
        vo.setPendingPosts(postMapper.selectCount(pendingQuery));

        // 今日新增帖子
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LambdaQueryWrapper<Post> todayQuery = new LambdaQueryWrapper<>();
        todayQuery.ge(Post::getCreateTime, todayStart);
        vo.setTodayNewPosts(postMapper.selectCount(todayQuery));

        // 活跃用户（近7天发帖或评论的用户数）
        LocalDateTime days7Ago = LocalDateTime.now().minusDays(7);
        LambdaQueryWrapper<Post> activePostQuery = new LambdaQueryWrapper<>();
        activePostQuery.ge(Post::getCreateTime, days7Ago);
        activePostQuery.select(Post::getUserId).groupBy(Post::getUserId);
        vo.setActiveUsers((long) postMapper.selectList(activePostQuery).size());

        return vo;
    }
}
