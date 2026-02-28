-- 在线考试系统数据库脚本 MySQL
CREATE DATABASE IF NOT EXISTS exam_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE exam_system;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL,
  `password` varchar(128) NOT NULL,
  `role` varchar(20) NOT NULL DEFAULT 'student',
  `real_name` varchar(64) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 题目表 type: single/multiple/judge/fill/short
CREATE TABLE IF NOT EXISTS `question` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(20) NOT NULL,
  `content` text NOT NULL,
  `option_a` varchar(512) DEFAULT NULL,
  `option_b` varchar(512) DEFAULT NULL,
  `option_c` varchar(512) DEFAULT NULL,
  `option_d` varchar(512) DEFAULT NULL,
  `correct_answer` varchar(512) DEFAULT NULL,
  `score` int DEFAULT 5,
  `difficulty` varchar(20) DEFAULT 'medium',
  `subject` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 试卷表
CREATE TABLE IF NOT EXISTS `paper` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `total_score` int NOT NULL DEFAULT 0,
  `duration` int NOT NULL DEFAULT 60,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 试卷题目关联表
CREATE TABLE IF NOT EXISTS `paper_question` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `paper_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `score` int NOT NULL DEFAULT 5,
  PRIMARY KEY (`id`),
  KEY `idx_paper_id` (`paper_id`),
  KEY `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 考试表 status: draft/published/ended
CREATE TABLE IF NOT EXISTS `exam` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `paper_id` bigint NOT NULL,
  `name` varchar(128) NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `status` varchar(20) DEFAULT 'draft',
  `shuffle` tinyint(1) DEFAULT 1,
  `show_answer` tinyint(1) DEFAULT 1,
  `allow_retake` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_paper_id` (`paper_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 考试记录表
CREATE TABLE IF NOT EXISTS `exam_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `exam_id` bigint NOT NULL,
  `total_score` int DEFAULT NULL,
  `start_time` datetime NOT NULL,
  `submit_time` datetime DEFAULT NULL,
  `grade_status` varchar(20) DEFAULT 'submitted',
  PRIMARY KEY (`id`),
  KEY `idx_user_exam` (`user_id`,`exam_id`),
  KEY `idx_exam_id` (`exam_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 答题记录表
CREATE TABLE IF NOT EXISTS `answer_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `exam_record_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `user_answer` text,
  `score` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_exam_record_id` (`exam_record_id`),
  KEY `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 系统公告表
CREATE TABLE IF NOT EXISTS `notice` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(128) NOT NULL,
  `content` text,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 初始管理员需在应用启动时通过 DataInit 创建（用户名 admin，密码 admin123）
