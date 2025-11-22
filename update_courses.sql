-- 选择数据库
USE gym_management_system;

-- 更新现有课程的时间为未来日期
UPDATE course SET schedule_time = DATE_ADD(NOW(), INTERVAL 1 DAY) WHERE id = 1;
UPDATE course SET schedule_time = DATE_ADD(NOW(), INTERVAL 2 DAY) WHERE id = 2;
UPDATE course SET schedule_time = DATE_ADD(NOW(), INTERVAL 3 DAY) WHERE id = 3;

-- 重置报名人数
UPDATE course SET current_count = 0 WHERE 1 = 1;

-- 添加更多未来课程
INSERT INTO course (name, schedule_time, trainer_id, max_capacity, current_count) VALUES
('Yoga Advanced', DATE_ADD(NOW(), INTERVAL 4 DAY), 2, 15, 0),
('Spin Class', DATE_ADD(NOW(), INTERVAL 5 DAY), 2, 20, 0),
('Pilates', DATE_ADD(NOW(), INTERVAL 7 DAY), 2, 12, 0);

-- 查看更新后的课程
SELECT * FROM course WHERE schedule_time > NOW();