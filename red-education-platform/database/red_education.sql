/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80041 (8.0.41)
 Source Host           : localhost:3306
 Source Schema         : red_education

 Target Server Type    : MySQL
 Target Server Version : 80041 (8.0.41)
 File Encoding         : 65001

 Date: 18/12/2025 16:36:49
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父评论ID（0表示一级评论）',
  `reply_to_user_id` bigint NULL DEFAULT NULL COMMENT '回复给谁（用户ID）',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-已删除, 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_post_id`(`post_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (1, 1, 4, '说得太好了！党的历史就是一部奋斗史。', 0, NULL, 0, 1, '2025-11-28 22:45:21');
INSERT INTO `comment` VALUES (2, 1, 5, '深有同感，我们要传承红色基因。', 0, NULL, 0, 1, '2025-11-28 22:45:21');
INSERT INTO `comment` VALUES (3, 1, 3, '感谢分享，很受启发！', 0, NULL, 0, 1, '2025-11-28 22:45:21');
INSERT INTO `comment` VALUES (4, 2, 3, '我也去过这个纪念馆，确实很震撼。', 0, NULL, 0, 1, '2025-11-28 22:45:21');
INSERT INTO `comment` VALUES (5, 2, 5, '革命先烈永垂不朽！', 0, NULL, 0, 1, '2025-11-28 22:45:21');
INSERT INTO `comment` VALUES (6, 1, 5, '是的，我们要不忘初心。', 1, 4, 0, 1, '2025-11-28 22:45:21');
INSERT INTO `comment` VALUES (7, 5, 3, '非常好', 0, NULL, 0, 1, '2025-12-03 13:43:45');
INSERT INTO `comment` VALUES (9, 5, 3, '@周恒悦 我也觉得\n', 0, NULL, 0, 1, '2025-12-03 18:25:10');
INSERT INTO `comment` VALUES (10, 1, 3, '@周恒悦 哈哈\n', 0, NULL, 0, 1, '2025-12-03 18:25:43');
INSERT INTO `comment` VALUES (11, 4, 3, '111', 0, NULL, 0, 1, '2025-12-03 18:31:43');
INSERT INTO `comment` VALUES (12, 5, 3, '@周恒悦 是的\n', 0, NULL, 0, 1, '2025-12-03 18:50:03');
INSERT INTO `comment` VALUES (14, 2, 3, '11111', 0, NULL, 0, 1, '2025-12-03 18:59:54');
INSERT INTO `comment` VALUES (15, 2, 1, '很好', 0, NULL, 0, 1, '2025-12-09 00:47:36');
INSERT INTO `comment` VALUES (16, 2, 1, '@testuser 你好', 0, NULL, 0, 1, '2025-12-18 11:20:00');

-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程标题',
  `intro` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '课程简介',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程内容',
  `video_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程视频URL',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图URL',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程分类',
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签（逗号分隔）',
  `points_reward` int NULL DEFAULT 10 COMMENT '完成奖励积分',
  `duration` int NULL DEFAULT NULL COMMENT '预计学习时长（分钟）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-下架, 1-上架',
  `is_recommend` tinyint NULL DEFAULT 0 COMMENT '是否推荐：0-否, 1-是',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `complete_count` int NULL DEFAULT 0 COMMENT '完成人数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_is_recommend`(`is_recommend` ASC) USING BTREE,
  INDEX `idx_sort`(`sort` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO `course` VALUES (1, '英雄模范人物事迹学习', '学习革命先烈和时代楷模的光辉事迹', '<h2>革命烈士</h2><p>刘胡兰、董存瑞等革命先烈...</p><h2>时代楷模</h2><p>雷锋、焦裕禄等先进模范...</p>', NULL, NULL, '先进事迹', '英雄,模范,学习', 10, 60, 1, 1, 3, 29, 1, '2025-11-27 12:10:54', '2025-11-27 19:39:27');
INSERT INTO `course` VALUES (13, '四级急救班', '四级一定过', '四级一定过\n', '/files/2025/12/17/d4de4e62de904222b383fba72b57d5eb.mp4', '/files/2025/12/17/02a91040b10a47938080a3c907747685.png', '时政热点', NULL, 50, 0, 1, 1, 0, 27, 0, '2025-12-17 15:55:28', '2025-12-17 15:55:28');

-- ----------------------------
-- Table structure for daily_study_log
-- ----------------------------
DROP TABLE IF EXISTS `daily_study_log`;
CREATE TABLE `daily_study_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `study_date` date NOT NULL,
  `study_minutes` int NULL DEFAULT 0 COMMENT '当日学习时长（分钟）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_date`(`user_id` ASC, `study_date` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_study_date`(`study_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '每日学习日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of daily_study_log
-- ----------------------------
INSERT INTO `daily_study_log` VALUES (1, 3, '2025-11-28', 60, '2025-12-15 15:47:10', '2025-12-15 15:47:10');
INSERT INTO `daily_study_log` VALUES (2, 1, '2025-12-15', 17, '2025-12-15 20:35:08', '2025-12-15 20:47:22');
INSERT INTO `daily_study_log` VALUES (3, 2, '2025-12-17', 28, '2025-12-17 15:10:04', '2025-12-17 15:43:11');
INSERT INTO `daily_study_log` VALUES (4, 3, '2025-12-17', 1, '2025-12-17 15:46:15', '2025-12-17 15:46:15');
INSERT INTO `daily_study_log` VALUES (5, 1, '2025-12-17', 3, '2025-12-17 15:57:01', '2025-12-17 15:58:26');
INSERT INTO `daily_study_log` VALUES (6, 3, '2025-12-18', 1, '2025-12-18 11:08:23', '2025-12-18 11:08:23');

-- ----------------------------
-- Table structure for exam
-- ----------------------------
DROP TABLE IF EXISTS `exam`;
CREATE TABLE `exam`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '试卷ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '试卷名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '试卷说明',
  `type` tinyint NOT NULL COMMENT '类型：1-随机组卷, 2-固定试卷',
  `duration` int NOT NULL COMMENT '考试时长（分钟）',
  `total_score` int NOT NULL COMMENT '总分',
  `pass_score` int NOT NULL COMMENT '及格分数',
  `question_count` int NOT NULL COMMENT '题目数量',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用, 1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '试卷表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam
-- ----------------------------
INSERT INTO `exam` VALUES (1, '党史知识测试（初级）', '测试对党史基础知识的掌握程度', 2, 30, 50, 30, 10, 1, '2025-11-27 12:10:54', '2025-12-08 17:29:56');
INSERT INTO `exam` VALUES (2, '新时代理论知识测试', '测试对习近平新时代中国特色社会主义思想的理解', 2, 45, 50, 30, 10, 1, '2025-11-27 12:10:54', '2025-12-05 19:32:05');
INSERT INTO `exam` VALUES (7, '综合测试1', '测试综合能力', 2, 60, 90, 54, 15, 1, '2025-12-08 21:32:47', '2025-12-15 10:57:41');
INSERT INTO `exam` VALUES (8, '综合测试2', '\n..........', 2, 60, 95, 57, 15, 1, '2025-12-08 21:42:35', '2025-12-15 10:58:12');
INSERT INTO `exam` VALUES (9, '综合测试1.2', '......', 2, 60, 70, 42, 10, 1, '2025-12-08 21:52:37', '2025-12-08 21:52:37');
INSERT INTO `exam` VALUES (10, '综合测试3', '', 2, 60, 0, 0, 0, 0, '2025-12-15 10:57:25', '2025-12-15 10:57:37');
INSERT INTO `exam` VALUES (11, '综合考试4', '', 1, 60, 55, 33, 10, 1, '2025-12-18 16:06:13', '2025-12-18 16:06:13');

-- ----------------------------
-- Table structure for exam_question
-- ----------------------------
DROP TABLE IF EXISTS `exam_question`;
CREATE TABLE `exam_question`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `exam_id` bigint NOT NULL COMMENT '试卷ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_exam_id`(`exam_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 152 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '试卷题目关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam_question
-- ----------------------------
INSERT INTO `exam_question` VALUES (1, 1, 1, 1);
INSERT INTO `exam_question` VALUES (2, 1, 2, 2);
INSERT INTO `exam_question` VALUES (3, 1, 3, 3);
INSERT INTO `exam_question` VALUES (4, 1, 4, 4);
INSERT INTO `exam_question` VALUES (5, 1, 5, 5);
INSERT INTO `exam_question` VALUES (6, 1, 6, 6);
INSERT INTO `exam_question` VALUES (7, 1, 7, 7);
INSERT INTO `exam_question` VALUES (8, 1, 8, 8);
INSERT INTO `exam_question` VALUES (9, 1, 9, 9);
INSERT INTO `exam_question` VALUES (10, 1, 10, 10);
INSERT INTO `exam_question` VALUES (11, 2, 2, 1);
INSERT INTO `exam_question` VALUES (12, 2, 4, 2);
INSERT INTO `exam_question` VALUES (13, 2, 6, 3);
INSERT INTO `exam_question` VALUES (14, 2, 7, 4);
INSERT INTO `exam_question` VALUES (15, 2, 9, 5);
INSERT INTO `exam_question` VALUES (16, 2, 10, 6);
INSERT INTO `exam_question` VALUES (17, 2, 1, 7);
INSERT INTO `exam_question` VALUES (18, 2, 3, 8);
INSERT INTO `exam_question` VALUES (19, 2, 5, 9);
INSERT INTO `exam_question` VALUES (20, 2, 8, 10);
INSERT INTO `exam_question` VALUES (21, 1, 1, 1);
INSERT INTO `exam_question` VALUES (22, 1, 2, 2);
INSERT INTO `exam_question` VALUES (23, 1, 3, 3);
INSERT INTO `exam_question` VALUES (24, 1, 6, 4);
INSERT INTO `exam_question` VALUES (25, 1, 7, 5);
INSERT INTO `exam_question` VALUES (26, 2, 1, 1);
INSERT INTO `exam_question` VALUES (27, 2, 2, 2);
INSERT INTO `exam_question` VALUES (28, 2, 3, 3);
INSERT INTO `exam_question` VALUES (29, 2, 4, 4);
INSERT INTO `exam_question` VALUES (30, 2, 5, 5);
INSERT INTO `exam_question` VALUES (31, 2, 6, 6);
INSERT INTO `exam_question` VALUES (32, 2, 7, 7);
INSERT INTO `exam_question` VALUES (33, 2, 8, 8);
INSERT INTO `exam_question` VALUES (34, 2, 9, 9);
INSERT INTO `exam_question` VALUES (35, 2, 10, 10);
INSERT INTO `exam_question` VALUES (102, 7, 2, 1);
INSERT INTO `exam_question` VALUES (103, 7, 3, 2);
INSERT INTO `exam_question` VALUES (104, 7, 4, 3);
INSERT INTO `exam_question` VALUES (105, 7, 5, 4);
INSERT INTO `exam_question` VALUES (106, 7, 6, 5);
INSERT INTO `exam_question` VALUES (107, 7, 7, 6);
INSERT INTO `exam_question` VALUES (108, 7, 8, 7);
INSERT INTO `exam_question` VALUES (109, 7, 9, 8);
INSERT INTO `exam_question` VALUES (110, 7, 10, 9);
INSERT INTO `exam_question` VALUES (111, 7, 13, 10);
INSERT INTO `exam_question` VALUES (112, 7, 14, 11);
INSERT INTO `exam_question` VALUES (113, 7, 16, 12);
INSERT INTO `exam_question` VALUES (114, 7, 18, 13);
INSERT INTO `exam_question` VALUES (115, 7, 19, 14);
INSERT INTO `exam_question` VALUES (116, 7, 20, 15);
INSERT INTO `exam_question` VALUES (117, 8, 4, 1);
INSERT INTO `exam_question` VALUES (118, 8, 5, 2);
INSERT INTO `exam_question` VALUES (119, 8, 6, 3);
INSERT INTO `exam_question` VALUES (120, 8, 7, 4);
INSERT INTO `exam_question` VALUES (121, 8, 8, 5);
INSERT INTO `exam_question` VALUES (122, 8, 9, 6);
INSERT INTO `exam_question` VALUES (123, 8, 10, 7);
INSERT INTO `exam_question` VALUES (124, 8, 13, 8);
INSERT INTO `exam_question` VALUES (125, 8, 14, 9);
INSERT INTO `exam_question` VALUES (126, 8, 15, 10);
INSERT INTO `exam_question` VALUES (127, 8, 16, 11);
INSERT INTO `exam_question` VALUES (128, 8, 17, 12);
INSERT INTO `exam_question` VALUES (129, 8, 18, 13);
INSERT INTO `exam_question` VALUES (130, 8, 19, 14);
INSERT INTO `exam_question` VALUES (131, 8, 20, 15);
INSERT INTO `exam_question` VALUES (132, 9, 9, 1);
INSERT INTO `exam_question` VALUES (133, 9, 10, 2);
INSERT INTO `exam_question` VALUES (134, 9, 13, 3);
INSERT INTO `exam_question` VALUES (135, 9, 14, 4);
INSERT INTO `exam_question` VALUES (136, 9, 15, 5);
INSERT INTO `exam_question` VALUES (137, 9, 16, 6);
INSERT INTO `exam_question` VALUES (138, 9, 17, 7);
INSERT INTO `exam_question` VALUES (139, 9, 18, 8);
INSERT INTO `exam_question` VALUES (140, 9, 19, 9);
INSERT INTO `exam_question` VALUES (141, 9, 20, 10);
INSERT INTO `exam_question` VALUES (142, 11, 19, 1);
INSERT INTO `exam_question` VALUES (143, 11, 8, 2);
INSERT INTO `exam_question` VALUES (144, 11, 14, 3);
INSERT INTO `exam_question` VALUES (145, 11, 5, 4);
INSERT INTO `exam_question` VALUES (146, 11, 4, 5);
INSERT INTO `exam_question` VALUES (147, 11, 9, 6);
INSERT INTO `exam_question` VALUES (148, 11, 2, 7);
INSERT INTO `exam_question` VALUES (149, 11, 17, 8);
INSERT INTO `exam_question` VALUES (150, 11, 1, 9);
INSERT INTO `exam_question` VALUES (151, 11, 6, 10);

-- ----------------------------
-- Table structure for exam_score
-- ----------------------------
DROP TABLE IF EXISTS `exam_score`;
CREATE TABLE `exam_score`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '成绩ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `exam_id` bigint NOT NULL COMMENT '试卷ID',
  `score` int NOT NULL COMMENT '得分',
  `total_score` int NOT NULL COMMENT '总分',
  `pass` tinyint NOT NULL COMMENT '是否及格：0-不及格, 1-及格',
  `correct_count` int NOT NULL COMMENT '正确题数',
  `wrong_count` int NOT NULL COMMENT '错误题数',
  `duration` int NULL DEFAULT NULL COMMENT '用时（分钟）',
  `answers` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '答案JSON',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_exam_id`(`exam_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考试成绩表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam_score
-- ----------------------------
INSERT INTO `exam_score` VALUES (2, 3, 1, 30, 50, 1, 6, 4, 120, '{\"1\":\"B\",\"2\":\"B\",\"3\":\"B\",\"4\":\"false\",\"5\":\"true\",\"6\":\"B\",\"7\":\"B\",\"8\":\"A\",\"9\":\"true\",\"10\":\"true\"}', '2025-12-02 17:17:24');
INSERT INTO `exam_score` VALUES (5, 3, 1, 30, 50, 1, 6, 4, 60, '{\"1\":\"B\",\"2\":\"B\",\"3\":\"B\",\"4\":\"true\",\"5\":\"true\",\"6\":\"B\",\"7\":\"B\",\"8\":\"A\",\"9\":\"true\",\"10\":\"true\"}', '2025-12-02 18:59:43');
INSERT INTO `exam_score` VALUES (11, 3, 2, 45, 50, 1, 9, 1, 16, '{\"1\":\"B\",\"2\":\"B\",\"3\":\"A\",\"4\":\"A\",\"5\":\"A\",\"6\":\"B\",\"7\":\"B\",\"8\":\"A\",\"9\":\"A\",\"10\":\"A\"}', '2025-12-03 13:12:20');
INSERT INTO `exam_score` VALUES (12, 3, 1, 15, 50, 0, 3, 7, 5, '{\"1\":\"B\",\"2\":\"B\",\"3\":\"B\",\"4\":\"B\"}', '2025-12-03 13:13:06');
INSERT INTO `exam_score` VALUES (13, 3, 2, 50, 50, 1, 10, 0, 124, '{\"1\":\"B\",\"2\":\"B\",\"3\":\"B\",\"4\":\"A\",\"5\":\"A\",\"6\":\"B\",\"7\":\"B\",\"8\":\"A\",\"9\":\"A\",\"10\":\"A\"}', '2025-12-03 13:17:21');
INSERT INTO `exam_score` VALUES (14, 3, 2, 50, 50, 1, 10, 0, 21, '{\"1\":\"B\",\"2\":\"B\",\"3\":\"B\",\"4\":\"A\",\"5\":\"A\",\"6\":\"B\",\"7\":\"B\",\"8\":\"A\",\"9\":\"A\",\"10\":\"A\"}', '2025-12-03 13:17:55');
INSERT INTO `exam_score` VALUES (15, 3, 1, 0, 50, 0, 0, 10, 3, '{}', '2025-12-03 19:12:09');
INSERT INTO `exam_score` VALUES (16, 3, 1, 50, 50, 1, 10, 0, 37, '{\"1\":\"B\",\"2\":\"B\",\"3\":\"B\",\"4\":\"A\",\"5\":\"A\",\"6\":\"B\",\"7\":\"B\",\"8\":\"A\",\"9\":\"A\",\"10\":\"A\"}', '2025-12-03 23:40:43');
INSERT INTO `exam_score` VALUES (17, 3, 1, 0, 50, 0, 0, 10, 29, '{}', '2025-12-04 14:44:29');
INSERT INTO `exam_score` VALUES (18, 1, 1, 50, 50, 1, 10, 0, 28, '{\"1\":\"B\",\"2\":\"B\",\"3\":\"B\",\"4\":\"A\",\"5\":\"A\",\"6\":\"B\",\"7\":\"B\",\"8\":\"A\",\"9\":\"A\",\"10\":\"A\"}', '2025-12-05 19:32:51');
INSERT INTO `exam_score` VALUES (19, 1, 2, 50, 50, 1, 10, 0, 29, '{\"1\":\"B\",\"2\":\"B\",\"3\":\"B\",\"4\":\"A\",\"5\":\"A\",\"6\":\"B\",\"7\":\"B\",\"8\":\"A\",\"9\":\"A\",\"10\":\"A\"}', '2025-12-05 19:33:33');
INSERT INTO `exam_score` VALUES (20, 1, 5, 75, 75, 1, 13, 2, 50, '{\"4\":\"A\",\"5\":\"A\",\"6\":\"B\",\"7\":\"B\",\"8\":\"A\",\"9\":\"A\",\"10\":\"A\",\"13\":\"C\",\"14\":\"C\",\"15\":\"B\",\"16\":\"A\",\"17\":\"A\",\"18\":\"A\",\"19\":\"A\",\"20\":\"A\"}', '2025-12-08 20:51:01');
INSERT INTO `exam_score` VALUES (21, 1, 6, 70, 95, 1, 12, 3, 35, '{\"2\":\"B\",\"3\":\"B\",\"4\":\"A\",\"5\":\"A\",\"6\":\"B\",\"7\":\"B\",\"8\":\"A\",\"9\":\"A\",\"10\":\"A\",\"13\":\"B\",\"14\":\"C\",\"15\":\"B\"}', '2025-12-08 21:24:59');
INSERT INTO `exam_score` VALUES (22, 1, 7, 50, 90, 0, 10, 5, 27, '{\"2\":\"B\",\"3\":\"B\",\"4\":\"A\",\"5\":\"A\",\"6\":\"B\",\"7\":\"B\",\"8\":\"A\",\"9\":\"A\",\"10\":\"A\",\"13\":\"C\",\"14\":\"C\"}', '2025-12-08 21:34:08');
INSERT INTO `exam_score` VALUES (23, 1, 7, 60, 90, 1, 11, 4, 20, '{\"2\":\"B\",\"3\":\"B\",\"4\":\"A\",\"5\":\"A\",\"6\":\"B\",\"7\":\"B\",\"8\":\"A\",\"9\":\"A\",\"10\":\"A\",\"13\":\"B\",\"14\":\"C\"}', '2025-12-08 21:34:35');
INSERT INTO `exam_score` VALUES (24, 1, 7, 60, 90, 1, 11, 4, 23, '{\"2\":\"B\",\"3\":\"B\",\"4\":\"A\",\"5\":\"A\",\"6\":\"B\",\"7\":\"B\",\"8\":\"A\",\"9\":\"A\",\"10\":\"A\",\"13\":\"B\",\"14\":\"C\"}', '2025-12-08 21:41:53');
INSERT INTO `exam_score` VALUES (25, 1, 8, 70, 95, 1, 12, 3, 27, '{\"4\":\"A\",\"5\":\"A\",\"6\":\"B\",\"7\":\"B\",\"8\":\"A\",\"9\":\"A\",\"10\":\"A\",\"13\":\"B\",\"14\":\"C\",\"15\":\"B\",\"16\":\"A\",\"17\":\"A\"}', '2025-12-08 21:43:20');
INSERT INTO `exam_score` VALUES (26, 3, 8, 65, 95, 1, 11, 4, 20, '{\"4\":\"A\",\"5\":\"A\",\"6\":\"B\",\"7\":\"B\",\"8\":\"A\",\"9\":\"A\",\"10\":\"A\",\"13\":\"B\",\"14\":\"C\",\"15\":\"B\",\"16\":\"A\"}', '2025-12-08 21:45:07');
INSERT INTO `exam_score` VALUES (27, 1, 9, 45, 70, 1, 7, 3, 14, '{\"9\":\"A\",\"10\":\"A\",\"13\":\"B\",\"14\":\"C\",\"15\":\"B\",\"16\":\"A\",\"17\":\"A\"}', '2025-12-08 21:53:17');
INSERT INTO `exam_score` VALUES (28, 1, 9, 65, 70, 1, 9, 1, 15, '{\"9\":\"A\",\"10\":\"A\",\"13\":\"B\",\"15\":\"B\",\"16\":\"A\",\"17\":\"A\",\"18\":\"A\",\"19\":\"B\",\"20\":\"A\"}', '2025-12-08 21:53:46');
INSERT INTO `exam_score` VALUES (29, 1, 9, 70, 70, 1, 10, 0, 22, '{\"9\":\"A\",\"10\":\"A\",\"13\":\"B\",\"14\":\"C\",\"15\":\"B\",\"16\":\"A\",\"17\":\"A\",\"18\":\"A\",\"19\":\"B\",\"20\":\"A\"}', '2025-12-08 21:54:23');
INSERT INTO `exam_score` VALUES (30, 1, 2, 50, 50, 1, 10, 0, 42, '{\"1\":\"B\",\"2\":\"B\",\"3\":\"B\",\"4\":\"A\",\"5\":\"A\",\"6\":\"B\",\"7\":\"B\",\"8\":\"A\",\"9\":\"A\",\"10\":\"A\"}', '2025-12-10 18:12:08');
INSERT INTO `exam_score` VALUES (31, 1, 9, 70, 70, 1, 10, 0, 23, '{\"9\":\"A\",\"10\":\"A\",\"13\":\"B\",\"14\":\"C\",\"15\":\"B\",\"16\":\"A\",\"17\":\"A\",\"18\":\"A\",\"19\":\"B\",\"20\":\"A\"}', '2025-12-10 18:52:18');
INSERT INTO `exam_score` VALUES (32, 1, 9, 70, 70, 1, 10, 0, 25, '{\"9\":\"A\",\"10\":\"A\",\"13\":\"B\",\"14\":\"C\",\"15\":\"B\",\"16\":\"A\",\"17\":\"A\",\"18\":\"A\",\"19\":\"B\",\"20\":\"A\"}', '2025-12-12 19:36:01');
INSERT INTO `exam_score` VALUES (33, 1, 9, 70, 70, 1, 10, 0, 60, '{\"9\":\"A\",\"10\":\"A\",\"13\":\"B\",\"14\":\"C\",\"15\":\"B\",\"16\":\"A\",\"17\":\"A\",\"18\":\"A\",\"19\":\"B\",\"20\":\"A\"}', '2025-12-12 19:51:12');
INSERT INTO `exam_score` VALUES (34, 1, 7, 60, 90, 1, 11, 4, 47, '{\"2\":\"B\",\"3\":\"B\",\"4\":\"A\",\"5\":\"A\",\"6\":\"B\",\"7\":\"B\",\"8\":\"B\",\"9\":\"A\",\"10\":\"A\",\"13\":\"A\",\"14\":\"B\",\"16\":\"A\",\"18\":\"A\",\"19\":\"A\",\"20\":\"A\"}', '2025-12-18 11:18:14');

-- ----------------------------
-- Table structure for exchange_record
-- ----------------------------
DROP TABLE IF EXISTS `exchange_record`;
CREATE TABLE `exchange_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `product_type` tinyint NOT NULL COMMENT '商品类型',
  `points_cost` int NOT NULL COMMENT '消耗积分',
  `pickup_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '取件码（实体商品）',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-待发货, 1-已发货, 2-已完成',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '兑换时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_pickup_code`(`pickup_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '兑换记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exchange_record
-- ----------------------------
INSERT INTO `exchange_record` VALUES (1, 1, 1, '红色经典电子书套装', 1, 50, NULL, 2, '2025-11-28 23:54:17');
INSERT INTO `exchange_record` VALUES (2, 1, 2, '在线课程优惠券(50积分)', 1, 30, NULL, 2, '2025-11-28 23:54:17');
INSERT INTO `exchange_record` VALUES (3, 1, 6, '《红星照耀中国》实体书', 2, 150, 'ABC12345', 2, '2025-11-28 23:54:17');
INSERT INTO `exchange_record` VALUES (4, 1, 10, '红色文化书签套装', 2, 50, 'XYZ67890', 0, '2025-11-28 23:54:17');
INSERT INTO `exchange_record` VALUES (5, 3, 12, '红色文化T恤', 2, 120, 'RDBZI3GS', 1, '2025-12-04 16:50:16');
INSERT INTO `exchange_record` VALUES (6, 1, 15, '革命历史明信片套装', 2, 40, 'AEMD5LU0', 2, '2025-12-18 10:57:20');
INSERT INTO `exchange_record` VALUES (7, 1, 15, '革命历史明信片套装', 2, 40, 'ZQ8V6POA', 2, '2025-12-18 11:20:07');

-- ----------------------------
-- Table structure for like_record
-- ----------------------------
DROP TABLE IF EXISTS `like_record`;
CREATE TABLE `like_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `target_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '点赞类型：POST-帖子, COMMENT-评论',
  `target_id` bigint NOT NULL COMMENT '目标ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_target`(`user_id` ASC, `target_type` ASC, `target_id` ASC) USING BTREE,
  INDEX `idx_target`(`target_type` ASC, `target_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of like_record
-- ----------------------------
INSERT INTO `like_record` VALUES (1, 3, 'POST', 1, '2025-11-28 22:45:21');
INSERT INTO `like_record` VALUES (2, 3, 'POST', 2, '2025-11-28 22:45:21');
INSERT INTO `like_record` VALUES (3, 4, 'POST', 1, '2025-11-28 22:45:21');
INSERT INTO `like_record` VALUES (4, 5, 'POST', 1, '2025-11-28 22:45:21');
INSERT INTO `like_record` VALUES (5, 5, 'POST', 2, '2025-11-28 22:45:21');
INSERT INTO `like_record` VALUES (9, 3, 'POST', 5, '2025-12-03 18:31:55');
INSERT INTO `like_record` VALUES (10, 3, 'POST', 3, '2025-12-03 18:36:54');
INSERT INTO `like_record` VALUES (14, 1, 'POST', 2, '2025-12-15 10:14:03');
INSERT INTO `like_record` VALUES (18, 1, 'POST', 14, '2025-12-18 11:19:34');

-- ----------------------------
-- Table structure for login_log
-- ----------------------------
DROP TABLE IF EXISTS `login_log`;
CREATE TABLE `login_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登录IP',
  `login_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登录地点',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '浏览器',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作系统',
  `status` tinyint NOT NULL COMMENT '登录状态：0-失败, 1-成功',
  `message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '提示信息',
  `login_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_login_time`(`login_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 165 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '登录日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of login_log
-- ----------------------------
INSERT INTO `login_log` VALUES (1, 4, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-11-27 12:35:10');
INSERT INTO `login_log` VALUES (2, 5, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-11-27 12:39:17');
INSERT INTO `login_log` VALUES (3, 5, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-11-27 12:43:42');
INSERT INTO `login_log` VALUES (4, 5, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-11-27 12:46:24');
INSERT INTO `login_log` VALUES (5, 6, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-11-27 12:58:26');
INSERT INTO `login_log` VALUES (6, 7, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-11-27 13:05:41');
INSERT INTO `login_log` VALUES (7, 7, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-11-27 13:21:19');
INSERT INTO `login_log` VALUES (8, 7, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-11-27 13:50:56');
INSERT INTO `login_log` VALUES (9, 7, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-11-27 13:59:50');
INSERT INTO `login_log` VALUES (10, 8, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 15:13:46');
INSERT INTO `login_log` VALUES (11, 8, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 15:16:33');
INSERT INTO `login_log` VALUES (12, 1, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 15:59:35');
INSERT INTO `login_log` VALUES (13, 1, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 16:07:59');
INSERT INTO `login_log` VALUES (14, 3, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 16:09:13');
INSERT INTO `login_log` VALUES (15, 8, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 16:09:21');
INSERT INTO `login_log` VALUES (16, 8, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 16:14:47');
INSERT INTO `login_log` VALUES (17, 1, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 16:15:25');
INSERT INTO `login_log` VALUES (18, 1, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 16:15:31');
INSERT INTO `login_log` VALUES (19, 1, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 16:15:44');
INSERT INTO `login_log` VALUES (20, 1, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 16:16:05');
INSERT INTO `login_log` VALUES (21, 1, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 16:16:16');
INSERT INTO `login_log` VALUES (22, 8, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 16:20:48');
INSERT INTO `login_log` VALUES (23, 1, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 16:21:20');
INSERT INTO `login_log` VALUES (24, 8, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 16:27:37');
INSERT INTO `login_log` VALUES (25, 1, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 16:28:16');
INSERT INTO `login_log` VALUES (26, 3, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 16:30:18');
INSERT INTO `login_log` VALUES (27, 3, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 16:33:51');
INSERT INTO `login_log` VALUES (28, 3, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 16:33:54');
INSERT INTO `login_log` VALUES (29, 8, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 16:36:10');
INSERT INTO `login_log` VALUES (30, 3, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 16:36:27');
INSERT INTO `login_log` VALUES (31, 3, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 16:38:25');
INSERT INTO `login_log` VALUES (32, 11, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 17:36:57');
INSERT INTO `login_log` VALUES (33, 11, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 17:37:42');
INSERT INTO `login_log` VALUES (34, 11, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 17:43:42');
INSERT INTO `login_log` VALUES (35, 11, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 17:44:04');
INSERT INTO `login_log` VALUES (36, 15, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 17:52:38');
INSERT INTO `login_log` VALUES (37, 17, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 17:52:45');
INSERT INTO `login_log` VALUES (38, 17, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 17:52:53');
INSERT INTO `login_log` VALUES (39, 15, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 17:53:04');
INSERT INTO `login_log` VALUES (40, 15, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 17:53:13');
INSERT INTO `login_log` VALUES (41, 17, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 17:54:08');
INSERT INTO `login_log` VALUES (42, 17, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 17:56:24');
INSERT INTO `login_log` VALUES (43, 17, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 17:56:42');
INSERT INTO `login_log` VALUES (44, 15, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 17:59:56');
INSERT INTO `login_log` VALUES (45, 15, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 18:00:11');
INSERT INTO `login_log` VALUES (46, 17, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 18:01:08');
INSERT INTO `login_log` VALUES (47, 17, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 18:01:23');
INSERT INTO `login_log` VALUES (48, 17, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 18:01:39');
INSERT INTO `login_log` VALUES (49, 15, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 18:59:45');
INSERT INTO `login_log` VALUES (50, 17, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 0, '密码错误', '2025-11-27 18:59:52');
INSERT INTO `login_log` VALUES (51, 15, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 19:00:03');
INSERT INTO `login_log` VALUES (52, 17, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 19:00:29');
INSERT INTO `login_log` VALUES (53, 17, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 19:01:04');
INSERT INTO `login_log` VALUES (54, 17, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 19:01:52');
INSERT INTO `login_log` VALUES (55, 17, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 19:06:07');
INSERT INTO `login_log` VALUES (56, 15, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 19:09:02');
INSERT INTO `login_log` VALUES (57, 1, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 19:12:19');
INSERT INTO `login_log` VALUES (58, 1, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 19:19:17');
INSERT INTO `login_log` VALUES (59, 3, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 19:25:31');
INSERT INTO `login_log` VALUES (60, 3, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 19:29:21');
INSERT INTO `login_log` VALUES (61, 3, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 19:32:44');
INSERT INTO `login_log` VALUES (62, 3, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 19:32:53');
INSERT INTO `login_log` VALUES (63, 3, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 19:39:40');
INSERT INTO `login_log` VALUES (64, 3, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 19:48:10');
INSERT INTO `login_log` VALUES (65, 3, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 19:49:25');
INSERT INTO `login_log` VALUES (66, 3, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 19:55:54');
INSERT INTO `login_log` VALUES (67, 3, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 19:58:04');
INSERT INTO `login_log` VALUES (68, 3, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 21:52:34');
INSERT INTO `login_log` VALUES (69, 3, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 22:37:32');
INSERT INTO `login_log` VALUES (70, 3, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-27 22:37:44');
INSERT INTO `login_log` VALUES (71, 3, '0:0:0:0:0:0:0:1', '未知', 'Unknown null', 'Unknown', 1, '登录成功', '2025-11-28 10:46:19');
INSERT INTO `login_log` VALUES (72, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-11-30 13:42:29');
INSERT INTO `login_log` VALUES (73, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-01 11:13:31');
INSERT INTO `login_log` VALUES (74, 3, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-01 11:42:53');
INSERT INTO `login_log` VALUES (75, 1, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-01 11:55:41');
INSERT INTO `login_log` VALUES (76, 1, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-01 17:02:51');
INSERT INTO `login_log` VALUES (77, 1, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-01 17:08:16');
INSERT INTO `login_log` VALUES (78, 1, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-01 17:19:52');
INSERT INTO `login_log` VALUES (79, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-01 17:55:54');
INSERT INTO `login_log` VALUES (80, 3, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-02 00:04:49');
INSERT INTO `login_log` VALUES (81, 1, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-02 00:09:02');
INSERT INTO `login_log` VALUES (82, 2, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-02 00:11:34');
INSERT INTO `login_log` VALUES (83, 3, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-02 13:30:57');
INSERT INTO `login_log` VALUES (84, 3, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-02 13:37:57');
INSERT INTO `login_log` VALUES (85, 3, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-02 13:42:31');
INSERT INTO `login_log` VALUES (86, 20, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-02 13:51:32');
INSERT INTO `login_log` VALUES (87, 20, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-02 13:52:25');
INSERT INTO `login_log` VALUES (88, 3, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-02 13:57:47');
INSERT INTO `login_log` VALUES (89, 1, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-02 14:11:07');
INSERT INTO `login_log` VALUES (90, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-02 16:13:44');
INSERT INTO `login_log` VALUES (91, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-02 16:51:27');
INSERT INTO `login_log` VALUES (92, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 0, '密码错误', '2025-12-02 18:41:56');
INSERT INTO `login_log` VALUES (93, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-02 18:43:26');
INSERT INTO `login_log` VALUES (94, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-02 23:32:07');
INSERT INTO `login_log` VALUES (95, 1, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-04 16:56:36');
INSERT INTO `login_log` VALUES (96, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-04 17:39:51');
INSERT INTO `login_log` VALUES (97, 1, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-04 17:40:12');
INSERT INTO `login_log` VALUES (98, 1, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-04 17:40:25');
INSERT INTO `login_log` VALUES (99, 1, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-04 17:40:35');
INSERT INTO `login_log` VALUES (100, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-05 11:17:29');
INSERT INTO `login_log` VALUES (101, 1, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-05 11:18:12');
INSERT INTO `login_log` VALUES (102, 1, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-05 16:07:49');
INSERT INTO `login_log` VALUES (103, 1, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-05 16:11:36');
INSERT INTO `login_log` VALUES (104, 1, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-05 16:17:32');
INSERT INTO `login_log` VALUES (105, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-05 17:48:39');
INSERT INTO `login_log` VALUES (106, 1, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 0, '密码错误', '2025-12-05 17:53:55');
INSERT INTO `login_log` VALUES (107, 1, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 142.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-05 17:54:04');
INSERT INTO `login_log` VALUES (108, 1, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 0, '密码错误', '2025-12-08 18:38:16');
INSERT INTO `login_log` VALUES (109, 1, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 0, '密码错误', '2025-12-08 20:14:13');
INSERT INTO `login_log` VALUES (110, 1, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 0, '密码错误', '2025-12-08 20:15:00');
INSERT INTO `login_log` VALUES (111, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-08 20:40:18');
INSERT INTO `login_log` VALUES (112, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-08 20:40:58');
INSERT INTO `login_log` VALUES (113, 1, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-08 20:41:17');
INSERT INTO `login_log` VALUES (114, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-08 20:48:33');
INSERT INTO `login_log` VALUES (115, 1, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-08 20:49:02');
INSERT INTO `login_log` VALUES (116, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-08 21:44:38');
INSERT INTO `login_log` VALUES (117, 1, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-08 21:51:19');
INSERT INTO `login_log` VALUES (118, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-08 22:09:43');
INSERT INTO `login_log` VALUES (119, 1, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-08 22:19:11');
INSERT INTO `login_log` VALUES (120, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-08 22:19:54');
INSERT INTO `login_log` VALUES (121, 1, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-08 22:26:05');
INSERT INTO `login_log` VALUES (122, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-08 22:35:23');
INSERT INTO `login_log` VALUES (123, 1, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-08 22:36:29');
INSERT INTO `login_log` VALUES (124, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-08 22:37:07');
INSERT INTO `login_log` VALUES (125, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-08 22:42:00');
INSERT INTO `login_log` VALUES (126, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-08 22:49:38');
INSERT INTO `login_log` VALUES (127, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-08 22:50:04');
INSERT INTO `login_log` VALUES (128, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-08 22:50:19');
INSERT INTO `login_log` VALUES (129, 1, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-08 22:57:28');
INSERT INTO `login_log` VALUES (130, 1, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-08 23:49:17');
INSERT INTO `login_log` VALUES (131, 1, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-08 23:51:15');
INSERT INTO `login_log` VALUES (132, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-09 00:01:36');
INSERT INTO `login_log` VALUES (133, 1, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-09 00:28:29');
INSERT INTO `login_log` VALUES (134, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-09 01:16:38');
INSERT INTO `login_log` VALUES (135, 1, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-09 11:50:36');
INSERT INTO `login_log` VALUES (136, 3, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-09 11:57:56');
INSERT INTO `login_log` VALUES (137, 1, '0:0:0:0:0:0:0:1', '未知', 'MSEdge 143.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-09 11:58:21');
INSERT INTO `login_log` VALUES (138, 1, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 0, '密码错误', '2025-12-10 18:09:00');
INSERT INTO `login_log` VALUES (139, 1, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 0, '密码错误', '2025-12-10 18:11:07');
INSERT INTO `login_log` VALUES (140, 1, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-11 00:12:58');
INSERT INTO `login_log` VALUES (141, 1, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-12 17:09:00');
INSERT INTO `login_log` VALUES (142, 1, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-12 19:10:46');
INSERT INTO `login_log` VALUES (143, 3, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-15 10:59:00');
INSERT INTO `login_log` VALUES (144, 3, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-15 15:59:18');
INSERT INTO `login_log` VALUES (145, 3, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-15 16:00:24');
INSERT INTO `login_log` VALUES (146, 1, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-15 17:02:53');
INSERT INTO `login_log` VALUES (147, 1, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-15 17:59:10');
INSERT INTO `login_log` VALUES (148, 2, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-17 14:52:12');
INSERT INTO `login_log` VALUES (149, 2, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-17 15:00:54');
INSERT INTO `login_log` VALUES (150, 3, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-17 15:43:35');
INSERT INTO `login_log` VALUES (151, 1, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-17 15:52:04');
INSERT INTO `login_log` VALUES (152, 3, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-17 16:02:05');
INSERT INTO `login_log` VALUES (153, 1, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-17 16:48:56');
INSERT INTO `login_log` VALUES (154, 1, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-17 19:36:43');
INSERT INTO `login_log` VALUES (155, 3, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-17 20:51:01');
INSERT INTO `login_log` VALUES (156, 1, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 0, '密码错误', '2025-12-17 20:51:58');
INSERT INTO `login_log` VALUES (157, 1, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-17 20:52:15');
INSERT INTO `login_log` VALUES (158, 2, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-18 10:25:16');
INSERT INTO `login_log` VALUES (159, 1, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-18 10:35:09');
INSERT INTO `login_log` VALUES (160, 2, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-18 11:01:10');
INSERT INTO `login_log` VALUES (161, 4, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-18 11:03:25');
INSERT INTO `login_log` VALUES (162, 3, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-18 11:07:04');
INSERT INTO `login_log` VALUES (163, 1, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-18 11:15:40');
INSERT INTO `login_log` VALUES (164, 1, '0:0:0:0:0:0:0:1', '未知', 'Chrome 140.0.0.0', 'Windows 10 or Windows Server 2016', 1, '登录成功', '2025-12-18 11:24:37');

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名',
  `operation` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作名称',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求方法',
  `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求参数',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作地点',
  `status` tinyint NOT NULL COMMENT '状态：0-失败, 1-成功',
  `error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_log
-- ----------------------------

-- ----------------------------
-- Table structure for point_record
-- ----------------------------
DROP TABLE IF EXISTS `point_record`;
CREATE TABLE `point_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `points` int NOT NULL COMMENT '积分变化（正数为增加，负数为减少）',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型：COURSE_COMPLETE-完成课程, EXAM_PASS-考试及格, EXAM_EXCELLENT-考试优秀, EXAM_PERFECT-考试满分, POST_PUBLISH-发布帖子, POST_LIKED-帖子被点赞, COMMENT_PUBLISH-发表评论, EXCHANGE-积分兑换',
  `related_id` bigint NULL DEFAULT NULL COMMENT '关联ID（课程ID、试卷ID等）',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 52 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '积分记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of point_record
-- ----------------------------
INSERT INTO `point_record` VALUES (1, 3, 10, 'COURSE_COMPLETE', 1, '完成课程：英雄模范人物事迹学习', '2025-11-28 10:51:38');
INSERT INTO `point_record` VALUES (2, 3, 15, 'EXAM_PASS', 1, '考试及格奖励', '2025-12-02 16:58:06');
INSERT INTO `point_record` VALUES (3, 3, 15, 'EXAM_PASS', 1, '考试及格奖励', '2025-12-02 17:17:24');
INSERT INTO `point_record` VALUES (4, 3, 15, 'EXAM_PASS', 1, '考试及格奖励', '2025-12-02 18:46:51');
INSERT INTO `point_record` VALUES (5, 3, 15, 'EXAM_PASS', 2, '考试及格奖励', '2025-12-02 18:50:21');
INSERT INTO `point_record` VALUES (6, 3, 15, 'EXAM_PASS', 1, '考试及格奖励', '2025-12-02 18:59:44');
INSERT INTO `point_record` VALUES (7, 3, 15, 'EXAM_PASS', 2, '考试及格奖励', '2025-12-02 19:03:15');
INSERT INTO `point_record` VALUES (8, 3, 15, 'EXAM_PASS', 2, '考试及格奖励', '2025-12-02 19:04:46');
INSERT INTO `point_record` VALUES (9, 3, 50, 'EXAM_PERFECT', 2, '考试满分奖励', '2025-12-02 19:10:33');
INSERT INTO `point_record` VALUES (10, 3, 50, 'EXAM_PERFECT', 1, '考试满分奖励', '2025-12-03 13:08:30');
INSERT INTO `point_record` VALUES (11, 3, 30, 'EXAM_EXCELLENT', 2, '考试优秀奖励', '2025-12-03 13:12:21');
INSERT INTO `point_record` VALUES (12, 3, 50, 'EXAM_PERFECT', 2, '考试满分奖励', '2025-12-03 13:17:21');
INSERT INTO `point_record` VALUES (13, 3, 50, 'EXAM_PERFECT', 2, '考试满分奖励', '2025-12-03 13:17:56');
INSERT INTO `point_record` VALUES (14, 3, 2, 'COMMENT', 5, '发表评论', '2025-12-03 13:43:46');
INSERT INTO `point_record` VALUES (15, 3, 2, 'COMMENT', 2, '发表评论', '2025-12-03 13:49:02');
INSERT INTO `point_record` VALUES (16, 3, 2, 'COMMENT', 5, '发表评论', '2025-12-03 18:25:11');
INSERT INTO `point_record` VALUES (17, 3, 2, 'COMMENT', 1, '发表评论', '2025-12-03 18:25:43');
INSERT INTO `point_record` VALUES (18, 3, 2, 'COMMENT', 4, '发表评论', '2025-12-03 18:31:43');
INSERT INTO `point_record` VALUES (19, 3, 2, 'COMMENT', 5, '发表评论', '2025-12-03 18:50:04');
INSERT INTO `point_record` VALUES (20, 3, 2, 'COMMENT', 2, '发表评论', '2025-12-03 18:59:53');
INSERT INTO `point_record` VALUES (21, 3, 2, 'COMMENT', 2, '发表评论', '2025-12-03 18:59:54');
INSERT INTO `point_record` VALUES (22, 3, 50, 'EXAM_PERFECT', 1, '考试满分奖励', '2025-12-03 23:40:44');
INSERT INTO `point_record` VALUES (23, 3, -120, 'EXCHANGE', 12, '兑换商品：红色文化T恤', '2025-12-04 16:50:16');
INSERT INTO `point_record` VALUES (24, 3, 5, 'RESOURCE_AUDIT', 8, '资源审核通过', '2025-12-05 01:31:31');
INSERT INTO `point_record` VALUES (25, 1, 5, '帖子审核通过', NULL, NULL, '2025-12-08 20:05:31');
INSERT INTO `point_record` VALUES (26, 1, 5, '帖子审核通过', NULL, NULL, '2025-12-08 20:12:24');
INSERT INTO `point_record` VALUES (27, 1, 5, '帖子审核通过', NULL, NULL, '2025-12-08 20:19:17');
INSERT INTO `point_record` VALUES (28, 1, 15, 'EXAM_PASS', 9, '考试及格奖励', '2025-12-08 21:53:17');
INSERT INTO `point_record` VALUES (29, 1, 15, 'EXAM_EXCELLENT', 9, '考试成绩提升奖励（45分→65分）', '2025-12-08 21:53:47');
INSERT INTO `point_record` VALUES (30, 1, 20, 'EXAM_PERFECT', 9, '考试成绩提升奖励（65分→70分）', '2025-12-08 21:54:24');
INSERT INTO `point_record` VALUES (31, 1, 5, 'POST_APPROVED', 13, '帖子审核通过', '2025-12-08 22:19:24');
INSERT INTO `point_record` VALUES (32, 3, 5, 'RESOURCE_AUDIT', 14, '资源审核通过', '2025-12-08 22:19:35');
INSERT INTO `point_record` VALUES (33, 1, 5, 'RESOURCE_AUDIT', 15, '资源审核通过', '2025-12-08 22:28:06');
INSERT INTO `point_record` VALUES (34, 3, 5, 'RESOURCE_AUDIT', 16, '资源审核通过', '2025-12-08 22:36:43');
INSERT INTO `point_record` VALUES (35, 1, 2, 'COMMENT', 2, '发表评论', '2025-12-09 00:47:37');
INSERT INTO `point_record` VALUES (36, 1, 50, 'EXAM_PERFECT', 2, '考试满分奖励', '2025-12-10 18:12:08');
INSERT INTO `point_record` VALUES (37, 1, 5, 'RESOURCE_AUDIT', 19, '资源审核通过', '2025-12-10 20:22:11');
INSERT INTO `point_record` VALUES (38, 3, 1, 'LIKE', 5, '帖子被点赞', '2025-12-12 18:47:35');
INSERT INTO `point_record` VALUES (39, 3, 1, 'LIKE', 5, '帖子被点赞', '2025-12-12 18:47:36');
INSERT INTO `point_record` VALUES (40, 3, 1, 'LIKE', 2, '帖子被点赞', '2025-12-15 10:14:03');
INSERT INTO `point_record` VALUES (41, 3, 1, 'LIKE', 2, '帖子被点赞', '2025-12-15 10:14:04');
INSERT INTO `point_record` VALUES (42, 1, 1, 'LIKE', 13, '帖子被点赞', '2025-12-15 16:30:30');
INSERT INTO `point_record` VALUES (43, 1, 1, 'LIKE', 13, '帖子被点赞', '2025-12-15 16:30:31');
INSERT INTO `point_record` VALUES (44, 1, 1, 'LIKE', 11, '帖子被点赞', '2025-12-15 16:30:35');
INSERT INTO `point_record` VALUES (45, 2, 10, 'COURSE_COMPLETE', 11, '完成课程：四级急救班1', '2025-12-17 15:09:34');
INSERT INTO `point_record` VALUES (46, 1, -40, 'EXCHANGE', 15, '兑换商品：革命历史明信片套装', '2025-12-18 10:57:21');
INSERT INTO `point_record` VALUES (47, 1, 5, 'RESOURCE_AUDIT', 21, '资源审核通过', '2025-12-18 11:16:59');
INSERT INTO `point_record` VALUES (48, 1, 15, 'EXAM_PASS', 7, '考试及格奖励', '2025-12-18 11:18:15');
INSERT INTO `point_record` VALUES (49, 1, 5, 'POST_APPROVED', 14, '帖子审核通过', '2025-12-18 11:19:28');
INSERT INTO `point_record` VALUES (50, 1, 2, 'COMMENT', 2, '发表评论', '2025-12-18 11:20:00');
INSERT INTO `point_record` VALUES (51, 1, -40, 'EXCHANGE', 15, '兑换商品：革命历史明信片套装', '2025-12-18 11:20:08');

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `images` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片URL（逗号分隔）',
  `topic` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '话题标签',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览量',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-待审核, 1-已通过, 2-已驳回',
  `audit_admin_id` bigint NULL DEFAULT NULL COMMENT '审核管理员ID',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `audit_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核意见',
  `is_top` tinyint NULL DEFAULT 0 COMMENT '是否置顶：0-否, 1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_is_top`(`is_top` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '帖子表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post
-- ----------------------------
INSERT INTO `post` VALUES (2, 3, '参观革命纪念馆有感', '今天参观了革命纪念馆，看到了许多珍贵的历史文物和革命先烈的事迹。那些为了民族独立和人民解放而英勇牺牲的英雄们，用生命诠释了什么是信仰和担当。我们今天的幸福生活来之不易，要倍加珍惜，继续发扬革命传统。', 'http://example.com/memorial1.jpg,http://example.com/memorial2.jpg', '红色教育', 29, 5, 269, 1, NULL, NULL, NULL, 0, '2025-11-28 22:45:30', '2025-11-28 22:45:30');
INSERT INTO `post` VALUES (3, 3, '学习新时代中国特色社会主义思想', '新时代中国特色社会主义思想是马克思主义中国化的最新成果，为我们指明了前进方向。通过系统学习，我更加深刻地理解了\"两个确立\"的决定性意义，增强了\"四个意识\"、坚定了\"四个自信\"、做到了\"两个维护\"。', NULL, '理论学习', 13, 0, 101, 1, NULL, NULL, NULL, 0, '2025-11-28 22:45:30', '2025-11-28 22:45:30');
INSERT INTO `post` VALUES (4, 3, '重温长征精神', '长征是人类历史上的伟大壮举，红军战士用坚定的信念和顽强的意志，完成了震惊世界的两万五千里长征。今天我们学习长征精神，就是要学习那种不怕困难、勇往直前的革命乐观主义精神，在新时代的长征路上继续奋斗。', 'http://example.com/changzheng.jpg', '党史学习', 20, 1, 188, 1, NULL, NULL, NULL, 0, '2025-11-28 22:45:30', '2025-11-28 22:45:30');
INSERT INTO `post` VALUES (5, 3, '观看《觉醒年代》的感悟', '最近观看了电视剧《觉醒年代》，被那个风云激荡的时代深深震撼。陈独秀、李大钊等革命先驱为了寻找救国救民的道路，不懈探索、勇敢实践。他们的爱国情怀和革命精神值得我们永远学习和传承。', NULL, '影视学习', 36, 3, 330, 1, NULL, NULL, NULL, 0, '2025-11-28 22:45:30', '2025-11-28 22:45:30');
INSERT INTO `post` VALUES (8, 1, '测试帖子上传功能', '测试帖子上传功能测试帖子上传功能测试帖子上传功能', '', '其他', 0, 0, 2, 2, 1, '2025-12-08 19:53:12', '不可以', 0, '2025-12-08 19:49:26', '2025-12-08 19:49:26');
INSERT INTO `post` VALUES (11, 1, '测试上传功能', '测试上传功能测试上传功能测试上传功能测试上传功能测试上传功能', '/files/2025/12/08/ffb0e0851750426ca053b9731b18c9a7.png', '其他', 0, 0, 6, 1, 1, '2025-12-08 20:19:17', '', 0, '2025-12-08 20:19:05', '2025-12-08 20:19:05');
INSERT INTO `post` VALUES (13, 1, '测试帖子功能', '测试帖子功能测试帖子功能', '/files/2025/12/08/a01b6562c1194393bbeb66f618f205f2.png', '其他', 0, 0, 9, 1, 1, '2025-12-08 22:19:24', '', 0, '2025-12-08 20:35:34', '2025-12-08 20:35:34');
INSERT INTO `post` VALUES (14, 1, '测试帖子功能', '测试帖子功能测试帖子功能', '/files/2025/12/18/3e838fc4be474df981755dfacd122026.png', '其他', 1, 0, 1, 1, 1, '2025-12-18 11:19:28', '', 0, '2025-12-18 11:19:17', '2025-12-18 11:19:17');

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品描述',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片',
  `type` tinyint NOT NULL COMMENT '类型：1-虚拟商品, 2-实体商品',
  `points_required` int NOT NULL COMMENT '所需积分',
  `stock` int NOT NULL DEFAULT 0 COMMENT '库存',
  `exchange_count` int NULL DEFAULT 0 COMMENT '兑换次数',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-下架, 1-上架',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_sort`(`sort` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, '电子学习资料包', '包含党史学习资料、思想理论电子书等', '', 1, 50, 999, 0, 0, 0, '2025-11-27 12:10:54', '2025-11-27 12:10:54');
INSERT INTO `product` VALUES (2, '红色文化纪念品', '精美的红色文化主题纪念品', NULL, 2, 200, 50, 0, 1, 2, '2025-11-27 12:10:54', '2025-11-27 12:10:54');
INSERT INTO `product` VALUES (3, '党史学习书籍', '精选党史学习书籍一本', NULL, 2, 300, 30, 0, 1, 3, '2025-11-27 12:10:54', '2025-11-27 12:10:54');
INSERT INTO `product` VALUES (5, '红色经典电子书套装', '包含《红星照耀中国》《长征》等经典红色书籍电子版', '/images/products/ebook_set.jpg', 1, 50, 1000, 0, 0, 0, '2025-11-28 23:54:17', '2025-11-28 23:54:17');
INSERT INTO `product` VALUES (9, '学习资料包', '包含各类红色教育学习资料、习题集等', '/images/products/study_pack.jpg', 1, 60, 800, 0, 0, 0, '2025-11-28 23:54:17', '2025-11-28 23:54:17');
INSERT INTO `product` VALUES (10, '《红星照耀中国》实体书', '经典红色书籍，埃德加·斯诺著，中文版', '/images/products/red_star.jpg', 2, 150, 100, 0, 1, 10, '2025-11-28 23:54:17', '2025-11-28 23:54:17');
INSERT INTO `product` VALUES (11, '《长征》纪念版图书', '王树增著，详细记录长征历史', '/images/products/long_march.jpg', 2, 180, 80, 0, 1, 11, '2025-11-28 23:54:17', '2025-11-28 23:54:17');
INSERT INTO `product` VALUES (12, '红色文化T恤', '印有红色经典标语的文化T恤', '/images/products/tshirt.jpg', 2, 120, 149, 1, 1, 12, '2025-11-28 23:54:17', '2025-12-04 16:50:16');
INSERT INTO `product` VALUES (13, '党徽纪念章', '精美党徽纪念章，金色镀金工艺', '/images/products/badge.jpg', 2, 80, 200, 0, 1, 13, '2025-11-28 23:54:17', '2025-11-28 23:54:17');
INSERT INTO `product` VALUES (14, '红色文化书签套装', '包含10枚精美书签，印有红色名言', '/images/products/bookmark.jpg', 2, 50, 300, 0, 1, 14, '2025-11-28 23:54:17', '2025-11-28 23:54:17');
INSERT INTO `product` VALUES (15, '革命历史明信片套装', '包含20张革命历史主题明信片', '/images/products/postcard.jpg', 2, 40, 248, 2, 1, 15, '2025-11-28 23:54:17', '2025-12-18 11:20:07');
INSERT INTO `product` VALUES (16, '红色经典笔记本', '精装笔记本，封面印有红色经典图案', '/images/products/notebook.jpg', 2, 70, 180, 0, 1, 16, '2025-11-28 23:54:17', '2025-11-28 23:54:17');
INSERT INTO `product` VALUES (17, '红色教育主题背包', '高品质帆布背包，印有红色教育主题图案', '/images/products/backpack.jpg', 2, 300, 50, 0, 1, 20, '2025-11-28 23:54:17', '2025-11-28 23:54:17');
INSERT INTO `product` VALUES (19, '测试商品（已下架）', '此商品用于测试，已下架', NULL, 1, 10, 0, 0, 0, 99, '2025-11-28 23:54:17', '2025-11-28 23:54:17');
INSERT INTO `product` VALUES (20, '测试商品', '测试商品。。。。。。', '/files/2025/12/08/20f7733992dc41729fbde85eecc1073c.png', 1, 100, 100, 0, 0, 0, '2025-12-08 18:43:41', '2025-12-08 18:43:41');

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '题目ID',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题型：SINGLE_CHOICE-单选题, JUDGE-判断题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题目内容',
  `option_a` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '选项A',
  `option_b` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '选项B',
  `option_c` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '选项C',
  `option_d` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '选项D',
  `correct_answer` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '正确答案',
  `explanation` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '答案解析',
  `difficulty` tinyint NULL DEFAULT 1 COMMENT '难度：1-简单, 2-中等, 3-困难',
  `score` int NULL DEFAULT 5 COMMENT '分值',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '知识点分类',
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_difficulty`(`difficulty` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question
-- ----------------------------
INSERT INTO `question` VALUES (1, 'SINGLE_CHOICE', '中国共产党成立于哪一年？', '1919年', '1921年', '1927年', '1949年', 'B', '中国共产党第一次全国代表大会于1921年7月在上海召开，标志着中国共产党的成立。', 1, 5, '党史知识', NULL, '2025-11-27 12:10:54', '2025-11-27 12:10:54');
INSERT INTO `question` VALUES (2, 'SINGLE_CHOICE', '中国共产党的根本宗旨是什么？', '实现共产主义', '全心全意为人民服务', '发展生产力', '民族复兴', 'B', '全心全意为人民服务是中国共产党的根本宗旨。', 1, 5, '党的理论', NULL, '2025-11-27 12:10:54', '2025-11-27 12:10:54');
INSERT INTO `question` VALUES (3, 'SINGLE_CHOICE', '新中国成立于哪一年？', '1945年', '1949年', '1950年', '1978年', 'B', '1949年10月1日，中华人民共和国成立。', 1, 5, '党史知识', NULL, '2025-11-27 12:10:54', '2025-11-27 12:10:54');
INSERT INTO `question` VALUES (4, 'JUDGE', '中国共产党是中国工人阶级的先锋队，同时是中国人民和中华民族的先锋队。', '正确', '错误', NULL, NULL, 'A', '这是对中国共产党性质的准确表述。', 1, 5, '党的理论', NULL, '2025-11-27 12:10:54', '2025-12-02 16:23:18');
INSERT INTO `question` VALUES (5, 'JUDGE', '改革开放是1978年开始的。', '正确', '错误', NULL, NULL, 'A', '1978年12月，党的十一届三中全会作出实行改革开放的历史性决策。', 1, 5, '党史知识', NULL, '2025-11-27 12:10:54', '2025-12-02 16:23:21');
INSERT INTO `question` VALUES (6, 'SINGLE_CHOICE', '中国特色社会主义最本质的特征是什么？', '人民民主专政', '中国共产党领导', '公有制经济', '依法治国', 'B', '党的领导是中国特色社会主义最本质的特征。', 2, 5, '党的理论', NULL, '2025-11-27 12:10:54', '2025-11-27 12:10:54');
INSERT INTO `question` VALUES (7, 'SINGLE_CHOICE', '社会主义核心价值观中，国家层面的价值目标是什么？', '自由、平等、公正、法治', '富强、民主、文明、和谐', '爱国、敬业、诚信、友善', '改革、创新、发展、稳定', 'B', '富强、民主、文明、和谐是国家层面的价值目标。', 2, 5, '价值观', NULL, '2025-11-27 12:10:54', '2025-11-27 12:10:54');
INSERT INTO `question` VALUES (8, 'SINGLE_CHOICE', '\"两个一百年\"奋斗目标是指什么？', '建党一百年和建国一百年', '新中国成立一百年和改革开放一百年', '第一个百年和第二个百年', '建党百年和建国百年', 'A', '\"两个一百年\"分别指中国共产党成立100年（2021年）和中华人民共和国成立100年（2049年）。', 2, 5, '国家战略', NULL, '2025-11-27 12:10:54', '2025-11-27 12:10:54');
INSERT INTO `question` VALUES (9, 'JUDGE', '四个自信是指道路自信、理论自信、制度自信、文化自信。', '正确', '错误', NULL, NULL, 'A', '这是习近平新时代中国特色社会主义思想的重要内容。', 2, 5, '党的理论', NULL, '2025-11-27 12:10:54', '2025-12-02 16:23:26');
INSERT INTO `question` VALUES (10, 'JUDGE', '我国现阶段的主要矛盾是人民日益增长的美好生活需要和不平衡不充分的发展之间的矛盾。', '正确', '错误', NULL, NULL, 'A', '这是党的十九大对我国社会主要矛盾的新判断。', 2, 5, '党的理论', NULL, '2025-11-27 12:10:54', '2025-12-02 16:23:28');
INSERT INTO `question` VALUES (13, 'SINGLE_CHOICE', '中国共产党第一次全国代表大会召开地点是?', '北京', '上海', '广州', '南京', 'B', '中共一大在上海召开', 2, 10, '党史', '建党历史', '2025-11-28 22:46:07', '2025-11-28 22:46:07');
INSERT INTO `question` VALUES (14, 'SINGLE_CHOICE', '抗日战争胜利是哪一年?', '1943年', '1944年', '1945年', '1946年', 'C', '抗日战争于1945年9月2日胜利结束', 1, 5, '党史', '抗日战争', '2025-11-28 22:46:07', '2025-11-28 22:46:07');
INSERT INTO `question` VALUES (15, 'SINGLE_CHOICE', '改革开放开始于哪一年?', '1976年', '1978年', '1980年', '1982年', 'B', '改革开放始于1978年十一届三中全会', 2, 10, '党史', '改革开放', '2025-11-28 22:46:07', '2025-11-28 22:46:07');
INSERT INTO `question` VALUES (16, 'JUDGE', '毛泽东是中国共产党的主要创始人之一', '正确', '错误', NULL, NULL, 'A', '毛泽东是中国共产党、中国人民解放军和中华人民共和国的主要缔造者', 1, 5, '党史', '领导人', '2025-11-28 22:46:07', '2025-11-28 22:46:07');
INSERT INTO `question` VALUES (17, 'JUDGE', '中国共产党的最高理想和最终目标是实现共产主义', '正确', '错误', NULL, NULL, 'A', '这是党章明确规定的', 1, 5, '理论', '党的性质', '2025-11-28 22:46:07', '2025-11-28 22:46:07');
INSERT INTO `question` VALUES (18, 'JUDGE', '社会主义核心价值观包括富强、民主、文明、和谐', '正确', '错误', NULL, NULL, 'A', '这是国家层面的价值目标', 1, 5, '理论', '核心价值观', '2025-11-28 22:46:07', '2025-11-28 22:46:07');
INSERT INTO `question` VALUES (19, 'JUDGE', '中国特色社会主义进入新时代是在党的十八大提出的', '正确', '错误', NULL, NULL, 'B', '是在党的十九大提出的', 2, 10, '时政', '新时代', '2025-11-28 22:46:07', '2025-11-28 22:46:07');
INSERT INTO `question` VALUES (20, 'JUDGE', '两个一百年奋斗目标是指建党一百年和建国一百年', '正确', '错误', NULL, NULL, 'A', '第一个百年目标是建党一百年全面建成小康社会，第二个百年目标是新中国成立一百年建成社会主义现代化强国', 2, 10, '时政', '奋斗目标', '2025-11-28 22:46:07', '2025-11-28 22:46:07');

-- ----------------------------
-- Table structure for resource
-- ----------------------------
DROP TABLE IF EXISTS `resource`;
CREATE TABLE `resource`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资源标题',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '资源描述',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '详细内容',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图URL',
  `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件URL',
  `file_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件类型：PDF, DOC, DOCX, MP4等',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小（字节）',
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签（逗号分隔）',
  `uploader_id` bigint NOT NULL COMMENT '上传者ID',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `download_count` int NULL DEFAULT 0 COMMENT '下载次数',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-待审核, 1-已通过, 2-已驳回',
  `audit_admin_id` bigint NULL DEFAULT NULL COMMENT '审核管理员ID',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `audit_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核意见',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_uploader_id`(`uploader_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  FULLTEXT INDEX `idx_fulltext`(`title`, `description`, `tags`)
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '资源表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of resource
-- ----------------------------
INSERT INTO `resource` VALUES (1, '中国共产党简史', 1, '全面介绍中国共产党百年奋斗历程的权威读本', '中国共产党自1921年成立以来，始终把为中国人民谋幸福、为中华民族谋复兴作为自己的初心使命...', 'http://example.com/party-history-cover.jpg', 'http://example.com/party-history.pdf', 'PDF', 5242880, '党史,历史,学习', 1, 162, 0, 1, NULL, NULL, NULL, '2025-11-28 23:01:05', '2025-11-28 23:01:05');
INSERT INTO `resource` VALUES (2, '改革开放简史', 1, '回顾改革开放40多年的光辉历程', '', '/files/2025/12/05/0c9a0ff31d504533889bb86257249370.png', 'http://example.com/reform.pdf', 'PDF', 3145728, '改革开放,历史', 1, 96, 0, 1, NULL, NULL, NULL, '2025-11-28 23:01:05', '2025-11-28 23:01:05');
INSERT INTO `resource` VALUES (3, '黄继光英雄事迹', 2, '抗美援朝战争中的特级英雄黄继光', '1952年10月，在朝鲜上甘岭战役中，黄继光所在营与美军为首的\"联合国军\"和南朝鲜军激战4昼夜后...', 'http://example.com/huangjiguang-cover.jpg', 'http://example.com/huangjiguang.pdf', 'PDF', 2097152, '英雄,抗美援朝', 1, 240, 0, 1, NULL, NULL, NULL, '2025-11-28 23:01:05', '2025-11-28 23:01:05');
INSERT INTO `resource` VALUES (4, '雷锋精神永流传', 2, '学习雷锋同志的先进事迹', '', 'http://example.com/leifeng-cover.jpg', 'http://example.com/leifeng.pdf', 'PDF', 7736529, '雷锋,精神,学习', 1, 314, 0, 1, NULL, NULL, NULL, '2025-11-28 23:01:05', '2025-11-28 23:01:05');
INSERT INTO `resource` VALUES (5, '学习强国平台介绍', 3, '了解学习强国学习平台的功能和使用', '', 'http://example.com/xuexiqiangguo-cover.jpg', 'http://example.com/xuexiqiangguo.pdf', 'PDF', 4194304, '学习强国,平台', 1, 192, 0, 1, NULL, NULL, NULL, '2025-11-28 23:01:05', '2025-11-28 23:01:05');
INSERT INTO `resource` VALUES (6, '《觉醒年代》精彩片段', 4, '重温建党初期的峥嵘岁月', '', 'http://example.com/juexing-cover.jpg', '/files/2025/12/11/88d6d571dd7847d19d748473ffc3efb5.mp4', 'video', 111097601, '觉醒年代,视频,党史', 1, 608, 3, 1, NULL, NULL, NULL, '2025-11-28 23:01:05', '2025-11-28 23:01:05');
INSERT INTO `resource` VALUES (8, '新时代中国特色社会主义思想学习纲要', 1, '系统学习新时代中国特色社会主义思想', '习近平新时代中国特色社会主义思想是马克思主义中国化最新成果...', NULL, 'http://example.com/xinshidai.pdf', 'PDF', 6291456, '新时代,思想,学习', 3, 8, 0, 1, 1, '2025-12-05 01:31:31', NULL, '2025-11-28 23:01:05', '2025-11-28 23:01:05');
INSERT INTO `resource` VALUES (10, '管理员测试', 3, '管理员测试管理员测试管理员测试管理员测试管理员测试管理员测试管理员测试管理员测试管理员测试', NULL, '/files/2025/12/05/cf4733f5d43e48eeb27b98b9708b3ac1.png', '/files/2025/12/05/06fd79db7d4446d8942e9428141342f7.pdf', 'document', 21626866, '', 1, 31, 0, 1, 1, '2025-12-05 15:55:17', '管理员上传自动通过', '2025-12-05 15:55:17', '2025-12-05 15:55:17');
INSERT INTO `resource` VALUES (11, '测试上传资源er', 3, '测试上传资源er测试上传资源er测试上传资源er测试上传资源er测试上传资源er', '', '/files/2025/12/05/67c31f08fe634e66b63ced68896c675d.png', '/files/2025/12/05/79302c9b232840e9bedaaa9ccfb7f9ad.pdf', 'document', 10539988, '', 1, 17, 1, 1, 1, '2025-12-05 16:19:13', '管理员上传自动通过', '2025-12-05 16:19:12', '2025-12-05 16:19:12');
INSERT INTO `resource` VALUES (15, '测试上传资源', 3, '测试上传资源测试上传资源测试上传资源', '', '/files/2025/12/08/cd06b8a1258848378cfcfeb2284498f7.png', '/files/2025/12/08/f84052920dfe46be87077c47cc81ce21.pdf', '', 931239, '', 1, 16, 3, 1, 1, '2025-12-08 22:28:06', NULL, '2025-12-08 22:27:32', '2025-12-08 22:28:06');
INSERT INTO `resource` VALUES (16, '测试资源2', 3, '测试上传资源测试上传资源测试上传资源测试上传资源', NULL, '/files/2025/12/08/7e32a3b96dbe4a4491be4a0e717de59f.png', '/files/2025/12/08/cbd58d618231423e83948b3e07cc17c6.pdf', NULL, 931239, NULL, 3, 17, 1, 1, 1, '2025-12-08 22:36:43', NULL, '2025-12-08 22:36:12', '2025-12-08 22:36:42');
INSERT INTO `resource` VALUES (17, '测试资源1111', 2, '测试资源1111测试资源1111测试资源1111测试资源1111', NULL, '/files/2025/12/10/0874337cbaed4641857801757ea33f5f.png', '/files/2025/12/10/450233a60d3343b5ba0aaef123440b6a.pdf', 'document', 126901, '', 1, 9, 0, 1, 1, '2025-12-10 19:01:46', '管理员上传自动通过', '2025-12-10 19:01:46', '2025-12-10 19:01:46');
INSERT INTO `resource` VALUES (18, '测试资源', 1, '测试测试测试测试测试测试测试测试测试测试测试测试', '', '/files/2025/12/10/06089c9205604776ba55fc55323e9762.png', '/files/2025/12/10/5469462d590c4e28bfd022eb83e2280f.pdf', 'document', 21626866, '', 1, 13, 0, 1, 1, '2025-12-10 19:28:27', '管理员上传自动通过', '2025-12-10 19:28:27', '2025-12-10 19:28:27');
INSERT INTO `resource` VALUES (19, '测试资源上传', 2, '11111111111111111111111111111111111111111111111111111111', NULL, '/files/2025/12/10/cf9c7824a5e64415b061fae2c60d1f89.png', '/files/2025/12/10/b9c212ade86748d3ac73faaf6acea7a6.pdf', NULL, 322504, NULL, 1, 11, 0, 1, 1, '2025-12-10 20:22:11', NULL, '2025-12-10 20:21:59', '2025-12-10 20:22:10');
INSERT INTO `resource` VALUES (20, '文件上传测试', 3, '文件上传测试文件上传测试文件上传测试文件上传测试文件上传测试', NULL, '/files/2025/12/15/8de077fa097b43cb94abd5e8637bfa50.png', '/files/2025/12/15/222b1ad59c15413d9a6dafb53b40a6a9.pdf', 'document', 126901, '', 1, 5, 3, 1, 1, '2025-12-15 09:40:46', '管理员上传自动通过', '2025-12-15 09:40:46', '2025-12-15 09:40:46');
INSERT INTO `resource` VALUES (21, '测试上传资源', 3, '测试上传资源测试上传资源测试上传资源', NULL, '/files/2025/12/18/dfbaffd9f8ce4f33b44982512751797c.png', '/files/2025/12/18/addd7a1118c3497c9d7582039bd67c5a.mp4', NULL, 111097601, NULL, 1, 0, 0, 1, 1, '2025-12-18 11:16:59', NULL, '2025-12-18 11:16:40', '2025-12-18 11:16:59');

-- ----------------------------
-- Table structure for resource_category
-- ----------------------------
DROP TABLE IF EXISTS `resource_category`;
CREATE TABLE `resource_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类编码：PARTY_HISTORY-党史文献, HERO_STORY-英雄事迹, CURRENT_AFFAIRS-时政热点, VIDEO-视频资源',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类描述',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code` ASC) USING BTREE,
  INDEX `idx_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '资源分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of resource_category
-- ----------------------------
INSERT INTO `resource_category` VALUES (1, '党史文献', 'PARTY_HISTORY', '党的历史文献、重要会议决议等', 1, '2025-11-27 12:10:54');
INSERT INTO `resource_category` VALUES (2, '英雄事迹', 'HERO_STORY', '革命英雄、先进模范人物事迹', 2, '2025-11-27 12:10:54');
INSERT INTO `resource_category` VALUES (3, '时政热点', 'CURRENT_AFFAIRS', '时事政治、政策解读等', 3, '2025-11-27 12:10:54');
INSERT INTO `resource_category` VALUES (4, '视频资源', 'VIDEO', '红色教育视频、纪录片等', 4, '2025-11-27 12:10:54');

-- ----------------------------
-- Table structure for study_record
-- ----------------------------
DROP TABLE IF EXISTS `study_record`;
CREATE TABLE `study_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `study_time` int NULL DEFAULT 0 COMMENT '学习时长（分钟）',
  `progress` int NULL DEFAULT 0 COMMENT '学习进度（百分比0-100）',
  `is_completed` tinyint NULL DEFAULT 0 COMMENT '是否完成：0-未完成, 1-已完成',
  `complete_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `last_position` int NULL DEFAULT 0 COMMENT '最后学习位置',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_course`(`user_id` ASC, `course_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_is_completed`(`is_completed` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学习记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of study_record
-- ----------------------------
INSERT INTO `study_record` VALUES (1, 3, 1, 60, 100, 1, '2025-11-28 10:51:38', 3600, '2025-11-27 21:52:47', '2025-11-28 10:51:38');
INSERT INTO `study_record` VALUES (2, 1, 10, 17, 99, 0, NULL, 0, '2025-12-15 20:34:38', '2025-12-15 20:47:22');
INSERT INTO `study_record` VALUES (3, 1, 11, 0, 36, 0, NULL, 205, '2025-12-15 20:55:45', '2025-12-15 20:59:25');
INSERT INTO `study_record` VALUES (4, 2, 11, 13, 100, 1, '2025-12-17 15:09:34', 11435, '2025-12-17 15:07:49', '2025-12-17 15:12:51');
INSERT INTO `study_record` VALUES (5, 2, 12, 15, 78, 0, NULL, 8890, '2025-12-17 15:25:09', '2025-12-17 15:43:11');
INSERT INTO `study_record` VALUES (6, 3, 12, 1, 97, 0, NULL, 8844, '2025-12-17 15:43:58', '2025-12-17 15:51:09');
INSERT INTO `study_record` VALUES (7, 1, 13, 3, 73, 0, NULL, 8403, '2025-12-17 15:55:52', '2025-12-18 11:17:31');
INSERT INTO `study_record` VALUES (8, 3, 13, 1, 1, 0, NULL, 113, '2025-12-17 16:02:43', '2025-12-18 11:08:23');

-- ----------------------------
-- Table structure for system_config
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置键',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '配置值',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `config_key`(`config_key` ASC) USING BTREE,
  INDEX `idx_config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_config
-- ----------------------------
INSERT INTO `system_config` VALUES (1, 'site_name', '红色教育资源数字化管理平台', '网站名称', '2025-11-27 12:10:54', '2025-11-27 12:10:54');
INSERT INTO `system_config` VALUES (2, 'site_description', '传承红色基因，弘扬革命精神', '网站描述', '2025-11-27 12:10:54', '2025-11-27 12:10:54');
INSERT INTO `system_config` VALUES (3, 'file_upload_max_size', '524288000', '文件上传最大大小（字节），默认500MB', '2025-11-27 12:10:54', '2025-11-27 12:10:54');
INSERT INTO `system_config` VALUES (4, 'doc_upload_max_size', '52428800', '文档上传最大大小（字节），默认50MB', '2025-11-27 12:10:54', '2025-11-27 12:10:54');
INSERT INTO `system_config` VALUES (5, 'points_course_complete', '10', '完成课程奖励积分', '2025-11-27 12:10:54', '2025-11-27 12:10:54');
INSERT INTO `system_config` VALUES (6, 'points_exam_pass', '15', '考试及格奖励积分', '2025-11-27 12:10:54', '2025-11-27 12:10:54');
INSERT INTO `system_config` VALUES (7, 'points_exam_excellent', '30', '考试优秀奖励积分（90分以上）', '2025-11-27 12:10:54', '2025-11-27 12:10:54');
INSERT INTO `system_config` VALUES (8, 'points_exam_perfect', '50', '考试满分奖励积分', '2025-11-27 12:10:54', '2025-11-27 12:10:54');
INSERT INTO `system_config` VALUES (9, 'points_post_publish', '5', '发布帖子奖励积分', '2025-11-27 12:10:54', '2025-11-27 12:10:54');
INSERT INTO `system_config` VALUES (10, 'points_post_liked', '1', '帖子被点赞奖励积分', '2025-11-27 12:10:54', '2025-11-27 12:10:54');
INSERT INTO `system_config` VALUES (11, 'points_comment_publish', '2', '发表评论奖励积分', '2025-11-27 12:10:54', '2025-11-27 12:10:54');
INSERT INTO `system_config` VALUES (12, 'site.name', '红色教育资源数字化管理平台', '网站名称', '2025-11-29 13:01:35', '2025-11-29 13:01:35');
INSERT INTO `system_config` VALUES (13, 'site.logo', '/images/logo.png', '网站Logo URL', '2025-11-29 13:01:35', '2025-11-29 13:01:35');
INSERT INTO `system_config` VALUES (14, 'site.description', '传承红色基因，弘扬革命精神', '网站简介', '2025-11-29 13:01:35', '2025-11-29 13:01:35');
INSERT INTO `system_config` VALUES (15, 'site.keywords', '红色教育,党史学习,革命历史', '网站关键词', '2025-11-29 13:01:35', '2025-11-29 13:01:35');
INSERT INTO `system_config` VALUES (16, 'site.icp', '京ICP备xxxxxxxx号', 'ICP备案号', '2025-11-29 13:01:35', '2025-11-29 13:01:35');
INSERT INTO `system_config` VALUES (17, 'points.course_complete', '10', '完成课程奖励积分', '2025-11-29 13:01:35', '2025-11-29 13:01:35');
INSERT INTO `system_config` VALUES (18, 'points.exam_pass', '15', '考试及格奖励积分', '2025-11-29 13:01:35', '2025-11-29 13:01:35');
INSERT INTO `system_config` VALUES (19, 'points.exam_excellent', '30', '考试优秀奖励积分(90分以上)', '2025-11-29 13:01:35', '2025-11-29 13:01:35');
INSERT INTO `system_config` VALUES (20, 'points.exam_perfect', '50', '考试满分奖励积分', '2025-11-29 13:01:35', '2025-11-29 13:01:35');
INSERT INTO `system_config` VALUES (21, 'points.post_publish', '5', '发布帖子奖励积分', '2025-11-29 13:01:35', '2025-11-29 13:01:35');
INSERT INTO `system_config` VALUES (22, 'points.post_liked', '1', '帖子被点赞奖励积分(作者获得)', '2025-11-29 13:01:35', '2025-11-29 13:01:35');
INSERT INTO `system_config` VALUES (23, 'points.comment_publish', '2', '发表评论奖励积分', '2025-11-29 13:01:35', '2025-11-29 13:01:35');
INSERT INTO `system_config` VALUES (24, 'points.post_approved', '5', '帖子通过审核奖励积分', '2025-11-29 13:01:35', '2025-11-29 13:01:35');
INSERT INTO `system_config` VALUES (25, 'upload.doc_max_size', '100', '文档最大上传大小(MB)', '2025-11-29 13:01:35', '2025-11-29 13:01:35');
INSERT INTO `system_config` VALUES (26, 'upload.video_max_size', '5000', '视频最大上传大小(MB)', '2025-11-29 13:01:35', '2025-11-29 13:01:35');
INSERT INTO `system_config` VALUES (27, 'upload.image_max_size', '10', '图片最大上传大小(MB)', '2025-11-29 13:01:35', '2025-11-29 13:01:35');
INSERT INTO `system_config` VALUES (28, 'upload.allowed_doc_types', 'pdf,doc,docx,xls,xlsx,ppt,pptx,txt', '允许的文档类型', '2025-11-29 13:01:35', '2025-11-29 13:01:35');
INSERT INTO `system_config` VALUES (29, 'upload.allowed_video_types', 'mp4,avi,mov,wmv,flv,ts', '允许的视频类型', '2025-11-29 13:01:35', '2025-12-15 17:34:23');
INSERT INTO `system_config` VALUES (30, 'upload.allowed_image_types', 'jpg,jpeg,png,gif,bmp', '允许的图片类型', '2025-11-29 13:01:35', '2025-11-29 13:01:35');
INSERT INTO `system_config` VALUES (31, 'system.register_enabled', 'true', '是否开放用户注册', '2025-11-29 13:01:35', '2025-11-29 13:01:35');
INSERT INTO `system_config` VALUES (32, 'system.comment_need_audit', 'false', '评论是否需要审核', '2025-11-29 13:01:35', '2025-11-29 13:01:35');
INSERT INTO `system_config` VALUES (33, 'system.post_need_audit', 'true', '帖子是否需要审核', '2025-11-29 13:01:35', '2025-11-29 13:01:35');
INSERT INTO `system_config` VALUES (34, 'system.resource_need_audit', 'true', '资源是否需要审核', '2025-11-29 13:01:35', '2025-11-29 13:01:35');
INSERT INTO `system_config` VALUES (35, 'system.page_size', '10', '默认分页大小', '2025-11-29 13:01:35', '2025-11-29 13:01:35');
INSERT INTO `system_config` VALUES (36, 'system.session_timeout', '7200', '会话超时时间(秒)', '2025-11-29 13:01:35', '2025-11-29 13:01:35');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码（加密）',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `intro` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个人简介',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'USER' COMMENT '角色：USER-普通用户, CONTENT_ADMIN-内容管理员, SYS_ADMIN-系统管理员',
  `points` int NOT NULL DEFAULT 0 COMMENT '积分',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用, 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_email`(`email` ASC) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '$2a$10$62cB5m0xVKGVrfTRhFTax.7n5yRNbAl2k1JPkLJd.UaSGGxogFZfi', '系统管理员', 'admin@red-edu.com', NULL, '', NULL, 'SYS_ADMIN', 82, 1, '2025-11-27 17:51:33', '2025-11-27 19:10:59');
INSERT INTO `user` VALUES (2, 'content_admin', '$2a$10$10StK2EsepWfYcuI/x0NGuUMQTZzgjdgHbisYjnG4wEfrekfjEZL6', '王老师', 'content@red-edu.com', NULL, '', NULL, 'CONTENT_ADMIN', 10, 1, '2025-11-27 17:51:38', '2025-11-27 19:11:00');
INSERT INTO `user` VALUES (3, 'testuser', '$2a$10$NliPL8upkmOyjFNEQE8./ex3VGonXU0l3yUXo/W5I4j5aaRz9BOxG', '梁建威', 'test@red-edu.com', '13900139000', '/files/2025/12/08/4facc77484ca4874959ea7647e02b4f0.png', '热爱学习红色文化', 'USER', 310, 1, '2025-11-27 17:52:13', '2025-11-27 19:11:04');
INSERT INTO `user` VALUES (4, 'testuser2', '$2a$10$HlzDnVUZJo6RO0yU7e9aHes/Mrp.sTBsShx4zaf6K/DY4YOqbjo8a', 'testuser2', 'test2@red-edu.com', '13812345678', NULL, NULL, 'USER', 0, 1, '2025-11-27 17:52:22', '2025-11-27 19:11:31');
INSERT INTO `user` VALUES (5, 'testuser3', '$2a$10$T7.XydydSV6CpOKEhBicXOhdqRtn0WNMjquCh1w1i1EN6MHD0feh.', 'testuser3', 'test3@red-edu.com', '13898765432', NULL, NULL, 'USER', 0, 1, '2025-11-27 17:52:34', '2025-11-27 19:10:57');
INSERT INTO `user` VALUES (20, 'newuser004', '$2a$10$TVNVn4woxw7SgfIYw59oIeK0r4jb5Fo2JQAKOBxUhilHo2mbjh56S', 'newuser004', 'newuser004@example.com', NULL, NULL, NULL, 'USER', 0, 1, '2025-12-02 13:51:09', '2025-12-02 13:51:09');

-- ----------------------------
-- Table structure for wrong_question
-- ----------------------------
DROP TABLE IF EXISTS `wrong_question`;
CREATE TABLE `wrong_question`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `user_answer` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户答案',
  `exam_score_id` bigint NULL DEFAULT NULL COMMENT '成绩记录ID',
  `is_mastered` tinyint NULL DEFAULT 0 COMMENT '是否已掌握：0-未掌握, 1-已掌握',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  INDEX `idx_is_mastered`(`is_mastered` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 97 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '错题本表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wrong_question
-- ----------------------------
INSERT INTO `wrong_question` VALUES (3, 3, 9, 'true', 1, 0, '2025-12-02 16:58:05');
INSERT INTO `wrong_question` VALUES (7, 3, 9, 'true', 2, 0, '2025-12-02 17:17:24');
INSERT INTO `wrong_question` VALUES (11, 3, 9, 'true', 3, 0, '2025-12-02 18:46:50');
INSERT INTO `wrong_question` VALUES (15, 3, 9, 'true', 4, 0, '2025-12-02 18:50:21');
INSERT INTO `wrong_question` VALUES (19, 3, 9, 'true', 5, 0, '2025-12-02 18:59:43');
INSERT INTO `wrong_question` VALUES (23, 3, 9, 'true', 6, 0, '2025-12-02 19:03:14');
INSERT INTO `wrong_question` VALUES (27, 3, 9, 'true', 7, 0, '2025-12-02 19:04:45');
INSERT INTO `wrong_question` VALUES (29, 3, 1, NULL, 10, 0, '2025-12-03 13:09:29');
INSERT INTO `wrong_question` VALUES (30, 3, 2, NULL, 10, 1, '2025-12-03 13:09:29');
INSERT INTO `wrong_question` VALUES (31, 3, 3, NULL, 10, 1, '2025-12-03 13:09:29');
INSERT INTO `wrong_question` VALUES (34, 3, 9, NULL, 10, 0, '2025-12-03 13:09:29');
INSERT INTO `wrong_question` VALUES (36, 3, 3, 'A', 11, 1, '2025-12-03 13:12:20');
INSERT INTO `wrong_question` VALUES (39, 3, 6, NULL, 12, 1, '2025-12-03 13:13:06');
INSERT INTO `wrong_question` VALUES (40, 3, 7, NULL, 12, 0, '2025-12-03 13:13:06');
INSERT INTO `wrong_question` VALUES (41, 3, 8, NULL, 12, 0, '2025-12-03 13:13:06');
INSERT INTO `wrong_question` VALUES (42, 3, 9, NULL, 12, 0, '2025-12-03 13:13:06');
INSERT INTO `wrong_question` VALUES (44, 3, 1, NULL, 15, 0, '2025-12-03 19:12:09');
INSERT INTO `wrong_question` VALUES (45, 3, 2, NULL, 15, 0, '2025-12-03 19:12:09');
INSERT INTO `wrong_question` VALUES (46, 3, 3, NULL, 15, 0, '2025-12-03 19:12:09');
INSERT INTO `wrong_question` VALUES (47, 3, 4, NULL, 15, 0, '2025-12-03 19:12:09');
INSERT INTO `wrong_question` VALUES (48, 3, 5, NULL, 15, 0, '2025-12-03 19:12:09');
INSERT INTO `wrong_question` VALUES (49, 3, 6, NULL, 15, 0, '2025-12-03 19:12:09');
INSERT INTO `wrong_question` VALUES (50, 3, 7, NULL, 15, 0, '2025-12-03 19:12:09');
INSERT INTO `wrong_question` VALUES (51, 3, 8, NULL, 15, 0, '2025-12-03 19:12:09');
INSERT INTO `wrong_question` VALUES (52, 3, 9, NULL, 15, 0, '2025-12-03 19:12:09');
INSERT INTO `wrong_question` VALUES (54, 3, 1, NULL, 17, 0, '2025-12-04 14:44:29');
INSERT INTO `wrong_question` VALUES (55, 3, 2, NULL, 17, 0, '2025-12-04 14:44:29');
INSERT INTO `wrong_question` VALUES (56, 3, 3, NULL, 17, 0, '2025-12-04 14:44:29');
INSERT INTO `wrong_question` VALUES (57, 3, 4, NULL, 17, 0, '2025-12-04 14:44:29');
INSERT INTO `wrong_question` VALUES (58, 3, 5, NULL, 17, 0, '2025-12-04 14:44:29');
INSERT INTO `wrong_question` VALUES (59, 3, 6, NULL, 17, 0, '2025-12-04 14:44:29');
INSERT INTO `wrong_question` VALUES (60, 3, 7, NULL, 17, 0, '2025-12-04 14:44:29');
INSERT INTO `wrong_question` VALUES (61, 3, 8, NULL, 17, 0, '2025-12-04 14:44:29');
INSERT INTO `wrong_question` VALUES (62, 3, 9, NULL, 17, 0, '2025-12-04 14:44:29');
INSERT INTO `wrong_question` VALUES (63, 3, 10, NULL, 17, 0, '2025-12-04 14:44:29');
INSERT INTO `wrong_question` VALUES (66, 1, 16, NULL, 21, 1, '2025-12-08 21:24:59');
INSERT INTO `wrong_question` VALUES (67, 1, 19, NULL, 21, 1, '2025-12-08 21:24:59');
INSERT INTO `wrong_question` VALUES (68, 1, 20, NULL, 21, 0, '2025-12-08 21:24:59');
INSERT INTO `wrong_question` VALUES (69, 1, 13, 'C', 22, 0, '2025-12-08 21:34:08');
INSERT INTO `wrong_question` VALUES (70, 1, 16, NULL, 22, 1, '2025-12-08 21:34:08');
INSERT INTO `wrong_question` VALUES (71, 1, 18, NULL, 22, 1, '2025-12-08 21:34:08');
INSERT INTO `wrong_question` VALUES (72, 1, 19, NULL, 22, 1, '2025-12-08 21:34:08');
INSERT INTO `wrong_question` VALUES (73, 1, 20, NULL, 22, 0, '2025-12-08 21:34:08');
INSERT INTO `wrong_question` VALUES (74, 1, 16, NULL, 23, 1, '2025-12-08 21:34:35');
INSERT INTO `wrong_question` VALUES (75, 1, 18, NULL, 23, 1, '2025-12-08 21:34:35');
INSERT INTO `wrong_question` VALUES (76, 1, 19, NULL, 23, 1, '2025-12-08 21:34:35');
INSERT INTO `wrong_question` VALUES (77, 1, 20, NULL, 23, 0, '2025-12-08 21:34:35');
INSERT INTO `wrong_question` VALUES (78, 1, 16, NULL, 24, 1, '2025-12-08 21:41:53');
INSERT INTO `wrong_question` VALUES (79, 1, 18, NULL, 24, 1, '2025-12-08 21:41:53');
INSERT INTO `wrong_question` VALUES (80, 1, 19, NULL, 24, 1, '2025-12-08 21:41:53');
INSERT INTO `wrong_question` VALUES (81, 1, 20, NULL, 24, 0, '2025-12-08 21:41:53');
INSERT INTO `wrong_question` VALUES (82, 1, 18, NULL, 25, 1, '2025-12-08 21:43:20');
INSERT INTO `wrong_question` VALUES (83, 1, 19, NULL, 25, 1, '2025-12-08 21:43:20');
INSERT INTO `wrong_question` VALUES (84, 1, 20, NULL, 25, 0, '2025-12-08 21:43:20');
INSERT INTO `wrong_question` VALUES (85, 3, 17, NULL, 26, 0, '2025-12-08 21:45:07');
INSERT INTO `wrong_question` VALUES (86, 3, 18, NULL, 26, 0, '2025-12-08 21:45:07');
INSERT INTO `wrong_question` VALUES (87, 3, 19, NULL, 26, 0, '2025-12-08 21:45:07');
INSERT INTO `wrong_question` VALUES (88, 3, 20, NULL, 26, 0, '2025-12-08 21:45:07');
INSERT INTO `wrong_question` VALUES (89, 1, 18, NULL, 27, 1, '2025-12-08 21:53:17');
INSERT INTO `wrong_question` VALUES (90, 1, 19, NULL, 27, 1, '2025-12-08 21:53:17');
INSERT INTO `wrong_question` VALUES (91, 1, 20, NULL, 27, 0, '2025-12-08 21:53:17');
INSERT INTO `wrong_question` VALUES (92, 1, 14, NULL, 28, 0, '2025-12-08 21:53:46');
INSERT INTO `wrong_question` VALUES (93, 1, 8, 'B', 34, 0, '2025-12-18 11:18:14');
INSERT INTO `wrong_question` VALUES (94, 1, 13, 'A', 34, 0, '2025-12-18 11:18:14');
INSERT INTO `wrong_question` VALUES (95, 1, 14, 'B', 34, 0, '2025-12-18 11:18:14');
INSERT INTO `wrong_question` VALUES (96, 1, 19, 'A', 34, 1, '2025-12-18 11:18:14');

SET FOREIGN_KEY_CHECKS = 1;
