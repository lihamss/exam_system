-- 在线考试系统 - 模拟数据
-- 执行前请先执行 schema.sql 建表，并确保应用已启动过一次（创建 admin 等初始用户）
USE exam_system;

-- ========== 题目数据 ==========
INSERT INTO `question` (`type`, `content`, `option_a`, `option_b`, `option_c`, `option_d`, `correct_answer`, `score`, `difficulty`, `subject`) VALUES
('single', 'Java 中 String 是可变类型吗？', '是', '否', '取决于 JVM', '取决于版本', 'B', 5, 'easy', 'Java'),
('single', '以下哪个不是 Java 的基本数据类型？', 'int', 'float', 'String', 'boolean', 'C', 5, 'easy', 'Java'),
('single', 'HTTP 状态码 404 表示？', '服务器错误', '未找到', '禁止访问', '重定向', 'B', 5, 'easy', '计算机网络'),
('multiple', '以下属于关系型数据库的有？', 'MySQL', 'Redis', 'PostgreSQL', 'MongoDB', 'A,C', 10, 'medium', '数据库'),
('multiple', 'Vue 3 的响应式 API 包括？', 'ref', 'reactive', 'computed', 'watch', 'A,B,C,D', 10, 'medium', '前端'),
('judge', 'Spring Boot 内嵌了 Tomcat 服务器。', NULL, NULL, NULL, NULL, 'T', 5, 'easy', 'Java'),
('judge', 'MySQL 的 InnoDB 引擎不支持事务。', NULL, NULL, NULL, NULL, 'F', 5, 'easy', '数据库'),
('fill', 'Spring Boot 的启动类通常使用注解 _____ 标注。', NULL, NULL, NULL, NULL, '@SpringBootApplication', 5, 'medium', 'Java'),
('fill', 'SQL 中用于查询的语句关键字是 _____。', NULL, NULL, NULL, NULL, 'SELECT', 5, 'easy', '数据库'),
('short', '简述 RESTful API 的设计原则。', NULL, NULL, NULL, NULL, NULL, 15, 'hard', '软件工程');

-- ========== 试卷数据 ==========
INSERT INTO `paper` (`name`, `total_score`, `duration`, `create_time`) VALUES
('Java 基础测试', 25, 30, NOW()),
('全栈开发综合测试', 50, 60, NOW());

-- ========== 试卷-题目关联（Java 基础：题 1,2,6,8；全栈：题 1-7） ==========
INSERT INTO `paper_question` (`paper_id`, `question_id`, `score`) VALUES
(1, 1, 5), (1, 2, 5), (1, 6, 5), (1, 8, 10),
(2, 1, 5), (2, 2, 5), (2, 3, 5), (2, 4, 10), (2, 5, 10), (2, 6, 5), (2, 7, 5);

-- 更新试卷总分
UPDATE `paper` SET `total_score` = 25 WHERE `id` = 1;
UPDATE `paper` SET `total_score` = 45 WHERE `id` = 2;

-- ========== 考试数据 ==========
-- 考试1：已发布，当前可参加（开始时间已过，结束时间未到）
INSERT INTO `exam` (`paper_id`, `name`, `start_time`, `end_time`, `status`, `shuffle`, `show_answer`, `allow_retake`) VALUES
(1, 'Java 基础摸底考试', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 7 DAY), 'published', 1, 1, 0),
(2, '全栈开发期中考试', DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_ADD(NOW(), INTERVAL 5 DAY), 'published', 1, 1, 1),
(1, 'Java 基础补考', DATE_ADD(NOW(), INTERVAL 3 DAY), DATE_ADD(NOW(), INTERVAL 4 DAY), 'draft', 1, 1, 0);

-- ========== 系统公告 ==========
INSERT INTO `notice` (`title`, `content`, `create_time`) VALUES
('系统上线通知', '在线考试系统已正式上线，欢迎各位师生使用。如有问题请联系管理员。', NOW()),
('Java 基础考试安排', 'Java 基础摸底考试将于本周开放，请同学们提前做好准备。', NOW()),
('成绩查询说明', '考试提交后，客观题将自动评分，主观题由教师批改后可查看成绩。', NOW());
