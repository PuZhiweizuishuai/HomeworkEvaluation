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

 Date: 09/03/2021 22:17:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
-- Records of article_tag
-- ----------------------------
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

SET FOREIGN_KEY_CHECKS = 1;
