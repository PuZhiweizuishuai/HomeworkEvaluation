/*
 Navicat Premium Data Transfer

 Source Server         : homework
 Source Server Type    : MySQL
 Source Server Version : 80023
 Source Host           : 192.168.43.131:3306
 Source Schema         : homework_evaluation

 Target Server Type    : MySQL
 Target Server Version : 80023
 File Encoding         : 65001

 Date: 10/03/2021 23:35:39
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for advertisement
-- ----------------------------
DROP TABLE IF EXISTS `advertisement`;
CREATE TABLE `advertisement`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '广告标题',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '点击后要链接到的地址',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '显示的图片地址',
  `create_time` bigint NOT NULL COMMENT '创建时间',
  `create_user` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建的用户',
  `start_time` bigint NOT NULL COMMENT '开始投放时间',
  `end_time` bigint NOT NULL COMMENT '结束投放时间',
  `update_time` bigint NOT NULL COMMENT '更新时间',
  `type` int NOT NULL COMMENT '类型，显示位置【\r\n0 首页顶部大图，\r\n1 课程页顶部大图\r\n2 首页广告\r\n3 课程页广告\r\n】',
  `view_count` bigint NOT NULL DEFAULT 0 COMMENT '点击次数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '帖子标题',
  `tag_id` int NOT NULL DEFAULT 0 COMMENT '分区ID',
  `tag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '帖子标签，英文状态逗号分隔',
  `author_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '帖子作者 id',
  `comment_count` bigint NOT NULL DEFAULT 0 COMMENT '帖子回帖计数',
  `view_count` bigint NOT NULL DEFAULT 0 COMMENT '帖子浏览计数',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '帖子正文内容',
  `perma_link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '帖子访问路径',
  `create_time` bigint NOT NULL COMMENT '帖子创建时间',
  `update_time` bigint NOT NULL COMMENT '帖子更新时间',
  `latest_comment_time` bigint NULL DEFAULT NULL COMMENT '帖子最新回帖时间',
  `latest_comment_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '帖子最新回帖者用户名',
  `commentable` int NOT NULL DEFAULT 1 COMMENT '帖子是否可回帖[0 不行， 1 可以]',
  `status` int NOT NULL COMMENT '0：正常，1：锁定，   2删除',
  `type` int NOT NULL COMMENT '0：普通帖子，1：课程讨论贴，2，问答贴， 3，想法\r\n4 课程评分',
  `course_id` bigint NULL DEFAULT NULL COMMENT '课程ID',
  `like_count` bigint NOT NULL DEFAULT 0 COMMENT '帖子点赞计数',
  `bad_count` bigint NOT NULL DEFAULT 0 COMMENT '帖子点踩计数',
  `collect_count` bigint NOT NULL DEFAULT 0 COMMENT '帖子收藏计数',
  `ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '发帖 IP',
  `ua` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'User-Agent',
  `articleStick` bigint NOT NULL COMMENT '帖子置顶时间',
  `anonymous` int NOT NULL DEFAULT 0 COMMENT '0：公开，1：匿名',
  `perfect` int NOT NULL DEFAULT 0 COMMENT '0：非精品，1：精品',
  `q_a_offer_point` int NULL DEFAULT NULL COMMENT '问答悬赏积分（仅作用于问答帖）',
  `top_img_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '帖子首图地址',
  `author_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '作者名',
  `course_rating` double NULL DEFAULT NULL COMMENT '课程评分',
  `at_user` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_article_by_user_id`(`author_id`) USING BTREE,
  INDEX `find_article_by_course_id`(`course_id`) USING BTREE,
  INDEX `find_article_by_type`(`type`) USING BTREE,
  INDEX `find_article_by_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '帖子表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for article_tag
-- ----------------------------
DROP TABLE IF EXISTS `article_tag`;
CREATE TABLE `article_tag`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标',
  `descript` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '简单描述',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `catelog_id` int NOT NULL COMMENT '0 父分类， 其它数字为该数字下的子 子分类',
  `type` int NOT NULL DEFAULT 0 COMMENT '0 话题， 1 分类',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for bulletin
-- ----------------------------
DROP TABLE IF EXISTS `bulletin`;
CREATE TABLE `bulletin`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发布者ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '正文',
  `curriculum_id` bigint NOT NULL COMMENT '课程ID',
  `create_time` bigint NOT NULL COMMENT '发布时间',
  `update_time` bigint NOT NULL COMMENT '更新时间',
  `status` int NOT NULL DEFAULT 0 COMMENT '更新状态[0 正常， 1 删除]',
  `ua` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发布设备',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发布IP',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_bulletin_by_user_id`(`user_id`) USING BTREE,
  INDEX `find_bulletin_by_class_Id`(`curriculum_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程公告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '回帖主键ID',
  `article_id` bigint NOT NULL COMMENT '帖子ID',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '回复正文',
  `author_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '作者ID',
  `type` int NOT NULL COMMENT '0 一级回复， 1 回复评论',
  `comment_id` bigint NULL DEFAULT NULL COMMENT '回复的帖子ID',
  `status` int NOT NULL DEFAULT 0 COMMENT '状态 0 正常， 1 删除',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'IP地址',
  `ua` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '回复设备UA',
  `like_count` bigint NOT NULL DEFAULT 0 COMMENT '喜欢数',
  `bad_count` bigint NOT NULL DEFAULT 0 COMMENT '不喜欢数',
  `q_a_offered` int NULL DEFAULT 0 COMMENT '回复是否被采纳，用于问答贴，0 未采纳， 1被采纳',
  `create_time` bigint NOT NULL COMMENT '创建时间',
  `update_time` bigint NOT NULL COMMENT '修改时间',
  `comment_count` bigint NOT NULL DEFAULT 0 COMMENT '评论数',
  `father_id` bigint NOT NULL COMMENT '父级评论ID，如果没有父级评论，默认为帖子ID',
  `rating` double NULL DEFAULT NULL COMMENT '作业打分或者课程评价',
  `at_user` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '@用户',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_comment_by_article_id`(`article_id`) USING BTREE,
  INDEX `find_comment_by_user_id`(`author_id`) USING BTREE,
  INDEX `find_comment_by_comment_id`(`comment_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 55 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for course_tag
-- ----------------------------
DROP TABLE IF EXISTS `course_tag`;
CREATE TABLE `course_tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_major` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程专业',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序，数字越大越靠前',
  `descript` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标',
  `catelog_id` bigint NOT NULL DEFAULT 0 COMMENT '所属分类层级 【0 父分类， 其它数字为该数字下的子 子分类】',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_course_major_index`(`course_major`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程分类' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for courseware
-- ----------------------------
DROP TABLE IF EXISTS `courseware`;
CREATE TABLE `courseware`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课件标题',
  `text` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '详细介绍',
  `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '上传的课件',
  `file_type` int NULL DEFAULT NULL COMMENT '课件类型，控制预览与下载',
  `caret_time` bigint NOT NULL COMMENT '创建时间',
  `update_time` bigint NOT NULL COMMENT '更新时间',
  `level` int NOT NULL COMMENT '分级【0 父分级， 1 子分级】',
  `create_teacher` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建老师',
  `father_id` bigint NOT NULL DEFAULT 0 COMMENT '父分级',
  `sort` int NOT NULL DEFAULT 1 COMMENT '排序',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '原文件名',
  `status` int NULL DEFAULT NULL COMMENT '文件转换状态',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_courseware_by_course_id`(`course_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for curriculum
-- ----------------------------
DROP TABLE IF EXISTS `curriculum`;
CREATE TABLE `curriculum`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程D',
  `create_teacher` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建的老师',
  `teacher_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '老师名',
  `curriculum_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程名',
  `class_number` int NOT NULL DEFAULT 0 COMMENT '课程号 如 1 班',
  `opening_time` bigint NOT NULL COMMENT '开课时间',
  `close_time` bigint NOT NULL COMMENT '结课时间',
  `create_time` bigint NOT NULL COMMENT '创建时间',
  `curriculum_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程描述',
  `curriculum_image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程图片',
  `student_number` int NOT NULL DEFAULT 0 COMMENT '学生人数',
  `access_method` int NOT NULL DEFAULT 0 COMMENT '【0 邀请进入】【1 输入密码进入】【3 公开】',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程密码，需要密码进入时使用',
  `join_time_limit` int NOT NULL DEFAULT 0 COMMENT '【0 不限制加入时间，结课前均可加入， 1 限制加入时间】',
  `join_time` bigint NULL DEFAULT NULL COMMENT '限制加入时间\r\n超过加入时间后只能邀请进入，或者延长时间',
  `update_time` bigint NOT NULL COMMENT '更新时间',
  `simple_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '简短的介绍',
  `course_tag` bigint NULL DEFAULT NULL COMMENT '课程分类',
  `score` double NOT NULL DEFAULT 0 COMMENT '初始0分，不显示',
  `father_course_tag` bigint NULL DEFAULT NULL COMMENT '父级分类',
  `rating_user_number` int NOT NULL DEFAULT 0 COMMENT '评价人数',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_teacher_create_class_index`(`create_teacher`) USING BTREE COMMENT '查找该老师创建的课程',
  INDEX `find_curriculum_name_index`(`curriculum_name`) USING BTREE COMMENT '通过课程名查找课程',
  INDEX `find_teacher_by_name_index`(`teacher_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for danmaku
-- ----------------------------
DROP TABLE IF EXISTS `danmaku`;
CREATE TABLE `danmaku`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `courseware_id` bigint NOT NULL COMMENT '视频ID',
  `color` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '弹幕颜色',
  `text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '弹幕内容',
  `time` double NOT NULL COMMENT '时间',
  `type` int NULL DEFAULT NULL COMMENT '类型',
  `author` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '作者',
  `color_dec` bigint NOT NULL COMMENT '十进制颜色数据',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_danmaku_by_courseware_id`(`courseware_id`) USING BTREE,
  INDEX `find_danmaku_by_userID`(`courseware_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '弹幕表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for history
-- ----------------------------
DROP TABLE IF EXISTS `history`;
CREATE TABLE `history`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `courseware_id` bigint NOT NULL COMMENT '上次查看的课件ID',
  `student_id` bigint NOT NULL COMMENT '查看的学生',
  `create_time` bigint NOT NULL COMMENT '创建时间',
  `update_time` bigint NOT NULL COMMENT '更新时间',
  `progress_bar` double NULL DEFAULT NULL COMMENT '如果是视频的话，视频进度',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for homework
-- ----------------------------
DROP TABLE IF EXISTS `homework`;
CREATE TABLE `homework`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '作业ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '作业标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '作业要求',
  `file_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '附件url，使用英文逗号分割',
  `open_time` bigint NOT NULL COMMENT '开始时间',
  `close_time` bigint NOT NULL COMMENT '截止时间',
  `create_time` bigint NOT NULL COMMENT '创建时间',
  `create_teacher` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建老师',
  `class_number` bigint NOT NULL COMMENT '所属班级',
  `submit_count` int NOT NULL DEFAULT 0 COMMENT '当前提交人数',
  `type` int NOT NULL DEFAULT 0 COMMENT '作业类型， 【0 普通作业，在时间区间完成即可， 1 测验，进入后需要在限时时间内完成， 2，考试，】',
  `limit_time` int NULL DEFAULT NULL COMMENT '如果时考试，测验开始几分钟后不能进入',
  `time` int NULL DEFAULT NULL COMMENT '如果时测验，则测验时间。分钟',
  `status` int NOT NULL COMMENT '状态【0未开始，1提交中， 2互评中】',
  `score` int NOT NULL DEFAULT 0 COMMENT '总分',
  `source_type` int NOT NULL DEFAULT 0 COMMENT '多选给分策略，【0 给一半， 1 不给分】',
  `evaluation` int NOT NULL DEFAULT 0 COMMENT '是否开启评价\r\n0 关闭\r\n1 开启',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_homework_by_class_index`(`class_number`) USING BTREE,
  INDEX `find_homeword_teacher_index`(`create_teacher`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '作业表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for homework_with_questions
-- ----------------------------
DROP TABLE IF EXISTS `homework_with_questions`;
CREATE TABLE `homework_with_questions`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `homework_id` bigint NOT NULL COMMENT '作业ID',
  `question_id` bigint NOT NULL COMMENT '问题ID',
  `score` int NOT NULL COMMENT '分数',
  `create_teacher` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_homework_question_index`(`homework_id`) USING BTREE,
  INDEX `find_question_use_homework_index`(`question_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '问题-作业关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for invite_code
-- ----------------------------
DROP TABLE IF EXISTS `invite_code`;
CREATE TABLE `invite_code`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邀请码',
  `generator_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '生成者ID',
  `expire_time` bigint NOT NULL COMMENT '过期时间',
  `create_time` bigint NOT NULL COMMENT '创建时间',
  `use_count` bigint NOT NULL DEFAULT 1 COMMENT '使用次数，即当前邀请码可使用次数，默认为 1',
  `link_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '生成的邀请链接',
  `status` int NULL DEFAULT 0 COMMENT '状态  0：可使用，1 停用',
  `memo` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `class_number` bigint NULL DEFAULT NULL COMMENT '对应的班级',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户邀请码时，对应的角色',
  `type` int NOT NULL COMMENT '邀请码类型 【0 课程邀请码】【1 用户邀请码】',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_invitecode_index`(`code`) USING BTREE COMMENT '查找邀请码',
  INDEX `find_invitecode_teacher_index`(`generator_id`) USING BTREE COMMENT '查找当前教师生成的邀请码'
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '邀请码' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for invite_code_use_log
-- ----------------------------
DROP TABLE IF EXISTS `invite_code_use_log`;
CREATE TABLE `invite_code_use_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `invitecode_id` bigint NOT NULL COMMENT '邀请码ID',
  `student_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '使用者ID',
  `use_time` bigint NOT NULL COMMENT '使用时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_use_invitecode_use_index`(`invitecode_id`) USING BTREE COMMENT '邀请码使用列表'
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '邀请码使用列表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `notifier` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知发送人ID',
  `receiver` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知接收人ID',
  `outer_id` bigint NOT NULL COMMENT '外部ID，如主帖子ID,课程ID',
  `comment_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评论内容',
  `comment_id` bigint NULL DEFAULT NULL COMMENT '评论目标ID',
  `type` int NOT NULL,
  `create_time` bigint NOT NULL,
  `status` int NOT NULL DEFAULT 0 COMMENT '【0 未读， 1 已读】',
  `text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '简单的信息描述',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '跳转链接',
  `notifier_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '通知人名',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_notification_by_notifier`(`notifier`) USING BTREE,
  INDEX `find_notification_by_receiver`(`receiver`) USING BTREE,
  INDEX `find_notification_by_outer_id`(`outer_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 164 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for questions
-- ----------------------------
DROP TABLE IF EXISTS `questions`;
CREATE TABLE `questions`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `question` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题目',
  `type` int NOT NULL COMMENT '类型 【0 单选  1 多选  2 填空  3 问答,4 判断】',
  `answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '答案，保存为json对象，方便判断',
  `other_answer` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '问答题和判断题答案',
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '选择题选项保存保存为json对象，方便判断',
  `tips` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '提示',
  `share_status` int NOT NULL DEFAULT 0 COMMENT '是否分享 【0 私有  1 其它老师可见 2 所有人可见】',
  `create_teacher` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建老师',
  `create_time` bigint NOT NULL COMMENT '创建时间',
  `submit_count` bigint NOT NULL DEFAULT 0 COMMENT '提交次数',
  `right_count` bigint NOT NULL DEFAULT 0 COMMENT '正确次数',
  `difficulty` int NULL DEFAULT NULL COMMENT '难度',
  `tag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_question_type_index`(`type`) USING BTREE,
  INDEX `find_question_teacher_index`(`create_teacher`) USING BTREE,
  INDEX `find_question_share_index`(`share_status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '问题表，需要与作业表关联' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for students_curriculum
-- ----------------------------
DROP TABLE IF EXISTS `students_curriculum`;
CREATE TABLE `students_curriculum`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生学号',
  `curriculum_id` bigint NOT NULL COMMENT '学生加入的课程ID',
  `create_time` bigint NOT NULL COMMENT '加入时间',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '在课程内的角色',
  `student_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生名',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_student_curriculum_index`(`student_id`) USING BTREE COMMENT '查找学生选择的课程',
  INDEX `find_curriculum_student_index`(`curriculum_id`) USING BTREE COMMENT '查找选择该课程的学生列表',
  INDEX `find_curriculum_user_role_index`(`role`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学生-课程关系列表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for submit_homework_status
-- ----------------------------
DROP TABLE IF EXISTS `submit_homework_status`;
CREATE TABLE `submit_homework_status`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID',
  `homework_id` bigint NOT NULL COMMENT '作业Id',
  `status` int NOT NULL COMMENT '状态\r\nHOMEWORK_ERROR(-1, \"作业不符合要求，被打回。\"),\r\n    NOT_SUBMITTED(0, \"暂未提交\"),\r\n    TEMPORARY_STORAGE(1, \"暂时保存，但未提交\"),\r\n    SUBMIT(2, \"已经提交，但老师没有批改\"),\r\n    CORRECTION(3, \"老师批改完成\")',
  `create_time` bigint NOT NULL COMMENT '创建时间',
  `update_time` bigint NOT NULL COMMENT '更新时间',
  `score` double NULL DEFAULT 0 COMMENT '分数',
  `teacher_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '教师评价',
  `student_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生姓名冗余存储',
  `submit_time` bigint NULL DEFAULT NULL COMMENT '提交时间',
  `comment_count` int NOT NULL DEFAULT 0 COMMENT '评价数',
  `like_count` int NOT NULL DEFAULT 0 COMMENT '喜欢数',
  `rating` double NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_user_homework_by_user_id`(`user_id`) USING BTREE,
  INDEX `find_user_homework_by_homework_id`(`homework_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 44 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户作业提交状态' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for submit_questions
-- ----------------------------
DROP TABLE IF EXISTS `submit_questions`;
CREATE TABLE `submit_questions`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID',
  `answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '选择答案',
  `other_answer` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '其它答案',
  `question_id` bigint NOT NULL COMMENT '问题ID',
  `homework_id` bigint NOT NULL COMMENT '作业ID',
  `create_time` bigint NOT NULL COMMENT '创建时间',
  `update_time` bigint NOT NULL COMMENT '更新时间',
  `score` double NOT NULL COMMENT '本题得分',
  `teacher_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '教师点评',
  `type` int NOT NULL COMMENT '作业类型',
  `max_score` int NOT NULL COMMENT '此题分值',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_question_by_user_id`(`user_id`) USING BTREE,
  INDEX `find_question_by_question_id`(`question_id`) USING BTREE,
  INDEX `find_question_by_homework_id`(`homework_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 155 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户提交的答案保存' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键ID，学号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机',
  `sex` int NOT NULL COMMENT '【0 男，1 女】',
  `user_avatar_URL` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '头像地址',
  `user_qq` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'qq',
  `user_wechat` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信',
  `user_Intro` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户简介',
  `create_time` bigint NOT NULL COMMENT '加入时间',
  `latest_login_time` bigint NULL DEFAULT NULL COMMENT '最近一次登陆时间',
  `latest_login_IP` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最近一次登陆ip',
  `article_count` bigint NOT NULL DEFAULT 0 COMMENT '发帖计数',
  `comment_count` bigint NOT NULL DEFAULT 0 COMMENT '回帖计数',
  `like_count` bigint NOT NULL DEFAULT 0 COMMENT '获赞计数',
  `follower_count` bigint NOT NULL DEFAULT 0 COMMENT '关注者数量',
  `fans_count` bigint NOT NULL DEFAULT 0 COMMENT '粉丝数量',
  `user_qq_status` int NOT NULL DEFAULT 0 COMMENT '是否公开qq【1 公开  0 不公开】',
  `user_wechat_status` int NOT NULL DEFAULT 0 COMMENT '是否公开微信 【1 公开  0 不公开】',
  `user_email_status` int NOT NULL DEFAULT 0 COMMENT '是否公开邮箱 【1 公开  0 不公开】',
  `user_phone_status` int NOT NULL DEFAULT 0 COMMENT '是否公开手机 【1 公开  0 不公开】',
  `school` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学校',
  `major` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专业',
  `grade` bigint NULL DEFAULT NULL COMMENT '年级',
  `curriculum_count` int NOT NULL DEFAULT 0 COMMENT '课程数',
  `status` int NOT NULL DEFAULT 0 COMMENT '【0 正常， 1 禁言, 2 锁定】',
  `start_lock_time` bigint NULL DEFAULT NULL COMMENT '禁言或锁定开始时间',
  `lock_time` bigint NULL DEFAULT NULL COMMENT '禁言或锁定时间,单位毫秒',
  `birthday` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '生日 yyyy-MM-dd',
  `first_login_status` int NOT NULL DEFAULT 0 COMMENT '是否是首次登陆 【0 是 1 不是】',
  `reset_password_status` int NOT NULL DEFAULT 0 COMMENT '是否被重置密码 【0 不是 1 是】',
  `title` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '教师职称',
  `top_img_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '/images/top.png' COMMENT '首页顶部大图',
  PRIMARY KEY (`user_id`) USING BTREE,
  INDEX `find_by_email_index`(`email`) USING BTREE COMMENT '使用邮件登陆查找用户',
  INDEX `find_by_phone_number_index`(`phone_number`) USING BTREE COMMENT '使用手机号查找登陆用户',
  INDEX `find_by_username_index`(`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_login_log
-- ----------------------------
DROP TABLE IF EXISTS `user_login_log`;
CREATE TABLE `user_login_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID',
  `login_time` bigint NOT NULL COMMENT '登陆时间',
  `login_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登陆IP',
  `login_UA` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'User-Agent 浏览器信息',
  `login_city` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登陆城市',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_login_user_by_id_index`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 52 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户登陆记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学号',
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'ROLE_加权限 如\r\n【 ROLE_ADMIN ： 管理员】\r\n【ROLE_TEACHER : 老师】\r\n【ROLE_ASSISTANT ： 助教】\r\n【ROLE_STUDENT : 学生】',
  `create_time` bigint NOT NULL COMMENT '创建时间',
  `operator` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'system_create' COMMENT '创建人，默认初始状态为system',
  `class_number` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '助教权限所在的班级\r\n只在role值为ROLE_ASSISTANT时使用,一串数字，中间用英文逗号分隔',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `find_role_by_userID_index`(`user_id`) USING BTREE COMMENT '查找当前用户拥有的权限列表',
  INDEX `find_role_operator_index`(`operator`) USING BTREE COMMENT '查找用户创建的权限'
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;


INSERT INTO `course_tag` VALUES (1, '计算机', 0, NULL, NULL, 0);
INSERT INTO `course_tag` VALUES (2, '外语', 0, NULL, NULL, 0);
INSERT INTO `course_tag` VALUES (3, '理学', 0, NULL, NULL, 0);
INSERT INTO `course_tag` VALUES (4, '工学', 0, NULL, NULL, 0);
INSERT INTO `course_tag` VALUES (5, '经济管理', 0, NULL, NULL, 0);
INSERT INTO `course_tag` VALUES (6, '心理学', 0, NULL, NULL, 0);
INSERT INTO `course_tag` VALUES (7, '文史哲', 0, NULL, NULL, 0);
INSERT INTO `course_tag` VALUES (8, '艺术设计', 0, NULL, NULL, 0);
INSERT INTO `course_tag` VALUES (9, '医药卫生', 0, NULL, NULL, 0);
INSERT INTO `course_tag` VALUES (10, '教育教学', 0, NULL, NULL, 0);
INSERT INTO `course_tag` VALUES (11, '法学', 0, NULL, NULL, 0);
INSERT INTO `course_tag` VALUES (12, '农林园艺', 0, NULL, NULL, 0);
INSERT INTO `course_tag` VALUES (13, '体育运动', 0, NULL, NULL, 0);
INSERT INTO `course_tag` VALUES (14, '音乐舞蹈', 0, NULL, NULL, 0);
INSERT INTO `course_tag` VALUES (15, '养生保健', 0, NULL, NULL, 0);
INSERT INTO `course_tag` VALUES (17, '前沿技术', 0, NULL, NULL, 1);
INSERT INTO `course_tag` VALUES (18, '程序设计与开发', 0, NULL, NULL, 1);
INSERT INTO `course_tag` VALUES (19, '计算机基础应用', 0, NULL, NULL, 1);
INSERT INTO `course_tag` VALUES (20, '软件工程', 0, NULL, NULL, 1);
INSERT INTO `course_tag` VALUES (21, '网络与安全技术', 0, NULL, NULL, 1);
INSERT INTO `course_tag` VALUES (22, '软硬件系统及原理', 0, NULL, NULL, 1);
INSERT INTO `course_tag` VALUES (23, '2022考研计算机', 0, NULL, NULL, 1);
INSERT INTO `course_tag` VALUES (24, '听力/口语', 0, NULL, NULL, 2);


INSERT INTO `article_tag` VALUES (1, '开发语言', NULL, NULL, 0, 0, 0);
INSERT INTO `article_tag` VALUES (2, '平台框架', NULL, NULL, 0, 0, 0);
INSERT INTO `article_tag` VALUES (3, '服务器', NULL, NULL, 0, 0, 0);
INSERT INTO `article_tag` VALUES (4, '数据库缓存', NULL, NULL, 0, 0, 0);
INSERT INTO `article_tag` VALUES (5, '开发工具', NULL, NULL, 0, 0, 0);
INSERT INTO `article_tag` VALUES (6, '系统设备', NULL, NULL, 0, 0, 0);
INSERT INTO `article_tag` VALUES (7, '休闲灌水', NULL, NULL, 0, 0, 0);
INSERT INTO `article_tag` VALUES (8, 'Java', NULL, NULL, 0, 1, 0);
INSERT INTO `article_tag` VALUES (9, 'Python', NULL, NULL, 0, 1, 0);
INSERT INTO `article_tag` VALUES (10, 'HTML', NULL, NULL, 0, 1, 0);
INSERT INTO `article_tag` VALUES (11, 'CSS', NULL, NULL, 0, 1, 0);
INSERT INTO `article_tag` VALUES (12, 'JavaScript', NULL, NULL, 0, 1, 0);
INSERT INTO `article_tag` VALUES (13, 'C/C++', NULL, NULL, 0, 1, 0);
INSERT INTO `article_tag` VALUES (14, 'C#', NULL, NULL, 0, 1, 0);
INSERT INTO `article_tag` VALUES (15, 'Spring', NULL, NULL, 0, 2, 0);
INSERT INTO `article_tag` VALUES (16, 'Linux', NULL, NULL, 0, 3, 0);
INSERT INTO `article_tag` VALUES (17, 'Nginx', NULL, NULL, 0, 3, 0);
INSERT INTO `article_tag` VALUES (18, 'Docker', NULL, NULL, 0, 3, 0);
INSERT INTO `article_tag` VALUES (19, '\r\nDjango', NULL, NULL, 0, 2, 0);
INSERT INTO `article_tag` VALUES (20, 'Mysql', NULL, NULL, 0, 4, 0);
INSERT INTO `article_tag` VALUES (21, 'Redis', NULL, NULL, 0, 4, 0);
INSERT INTO `article_tag` VALUES (22, 'Windows', NULL, NULL, 0, 6, 0);
INSERT INTO `article_tag` VALUES (23, 'Android', NULL, NULL, 0, 6, 0);
INSERT INTO `article_tag` VALUES (24, 'iOS', NULL, NULL, 0, 6, 0);
INSERT INTO `article_tag` VALUES (25, 'Linux', NULL, NULL, 0, 6, 0);
INSERT INTO `article_tag` VALUES (26, '\r\nGit', NULL, NULL, 0, 5, 0);
INSERT INTO `article_tag` VALUES (27, 'VS Code', NULL, NULL, 0, 5, 0);
INSERT INTO `article_tag` VALUES (28, '\r\nVisual Studio', NULL, NULL, 0, 5, 0);
INSERT INTO `article_tag` VALUES (29, '\r\nVim', NULL, NULL, 0, 5, 0);
INSERT INTO `article_tag` VALUES (30, '\r\nIntelliJ IDEA', NULL, NULL, 0, 5, 0);
INSERT INTO `article_tag` VALUES (31, '\r\nEclipse', NULL, NULL, 0, 5, 0);
INSERT INTO `article_tag` VALUES (32, '娱乐八卦', NULL, NULL, 0, 7, 0);
INSERT INTO `article_tag` VALUES (33, '闲聊', NULL, NULL, 0, 7, 0);