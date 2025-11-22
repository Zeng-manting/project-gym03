USE gym_management_system;

-- 直接插入测试会员用户
INSERT INTO user (phone, password, role) VALUES ('13800138000', '$2a$10$qJ077KHUc0aL7h5V3d7eQO1vT7vGtD3Z7tNQ7mF7qP7rK7yU7wE9W', 'member');

-- 查询添加的用户
SELECT * FROM user WHERE phone = '13800138000';