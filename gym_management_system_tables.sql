-- MySQL建表语句：健身房管理系统数据库
-- 创建时间：2023

-- 创建数据库
CREATE DATABASE IF NOT EXISTS gym_management_system 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 使用该数据库
USE gym_management_system;


-- 创建用户表
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
  `role` VARCHAR(20) NOT NULL COMMENT '角色：member（会员）、trainer（教练）、admin（管理员）',
  `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态：active（活跃）、disabled（禁用）',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `idx_phone` (`phone`),
  INDEX `idx_role` (`role`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建课程表
CREATE TABLE `course` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `name` VARCHAR(100) NOT NULL COMMENT '课程名称',
  `schedule_time` DATETIME NOT NULL COMMENT '上课时间',
  `trainer_id` BIGINT NOT NULL COMMENT '教练ID（关联user表）',
  `max_capacity` INT NOT NULL COMMENT '最大容量',
  `current_count` INT DEFAULT 0 COMMENT '当前报名人数',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_trainer_id` (`trainer_id`),
  INDEX `idx_schedule_time` (`schedule_time`),
  INDEX `idx_current_count` (`current_count`),
  CONSTRAINT `fk_course_trainer` FOREIGN KEY (`trainer_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- 创建预约表
CREATE TABLE `booking` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID（关联user表）',
  `course_id` BIGINT NOT NULL COMMENT '课程ID（关联course表）',
  `booking_time` DATETIME NOT NULL COMMENT '预约时间',
  `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态：active（活跃）、cancelled（已取消）',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `idx_user_course` (`user_id`, `course_id`, `status`),
  INDEX `idx_course_id` (`course_id`),
  INDEX `idx_booking_time` (`booking_time`),
  INDEX `idx_status` (`status`),
  CONSTRAINT `fk_booking_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_booking_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约表';

-- 创建会员卡表
CREATE TABLE `membership_card` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '会员卡ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID（关联user表）',
  `card_number` VARCHAR(50) NOT NULL COMMENT '卡号',
  `card_type` VARCHAR(50) NOT NULL COMMENT '卡类型：年卡、月卡、次卡等',
  `start_date` DATE NOT NULL COMMENT '开始日期',
  `end_date` DATE NOT NULL COMMENT '结束日期',
  `remaining_count` INT DEFAULT 0 COMMENT '剩余次数（针对次卡）',
  `total_count` INT DEFAULT 0 COMMENT '总次数（针对次卡）',
  `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态：active（活跃）、expired（过期）、cancelled（已取消）',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `idx_card_number` (`card_number`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_end_date` (`end_date`),
  CONSTRAINT `fk_membership_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员卡表';

-- 创建会员信息表
CREATE TABLE `member_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '会员信息ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID（关联user表）',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `gender` VARCHAR(10) COMMENT '性别：男、女',
  `birth_date` DATE COMMENT '出生日期',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号（与user表同步）',
  `card_issue_date` DATE COMMENT '办卡日期',
  `height` DECIMAL(5,2) COMMENT '身高（cm）',
  `weight` DECIMAL(5,2) COMMENT '体重（kg）',
  `email` VARCHAR(100) COMMENT '电子邮箱',
  `address` VARCHAR(255) COMMENT '地址',
  `emergency_contact` VARCHAR(50) COMMENT '紧急联系人',
  `emergency_phone` VARCHAR(20) COMMENT '紧急联系电话',
  `health_condition` TEXT COMMENT '健康状况',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `idx_user_id` (`user_id`),
  UNIQUE INDEX `idx_phone` (`phone`),
  CONSTRAINT `fk_member_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员信息表';

-- 插入一些初始数据
-- 1. 插入默认管理员用户（密码使用BCrypt加密：123456）
INSERT INTO `user` (`phone`, `password`, `role`, `status`) VALUES 
('13800138000', '$2a$10$dXpG4pZkZJmZmZmZmZmZmefYkZx3Zx3Zx3Zx3Zx3Zx3Zx3Zx3Zx3Zx3', 'admin', 'active'),
('13900139000', '$2a$10$dXpG4pZkZJmZmZmZmZmZmefYkZx3Zx3Zx3Zx3Zx3Zx3Zx3Zx3Zx3Zx3', 'trainer', 'active');

-- 2. 插入一些默认课程
INSERT INTO `course` (`name`, `schedule_time`, `trainer_id`, `max_capacity`, `current_count`) VALUES 
('瑜伽基础', '2023-12-01 19:00:00', 2, 20, 15),
('力量训练', '2023-12-02 18:00:00', 2, 15, 10),
('有氧健身', '2023-12-03 20:00:00', 2, 25, 18);

-- 创建教练信息表
CREATE TABLE `coach_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '教练信息ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID（关联user表）',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `gender` VARCHAR(10) COMMENT '性别：男、女',
  `birth_date` DATE COMMENT '出生日期',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号（与user表同步）',
  `specialty` VARCHAR(255) COMMENT '专长领域',
  `certification` VARCHAR(255) COMMENT '专业证书',
  `experience_years` INT DEFAULT 0 COMMENT '经验年限',
  `introduction` TEXT COMMENT '自我介绍',
  `education_background` VARCHAR(255) COMMENT '教育背景',
  `contact_email` VARCHAR(100) COMMENT '联系邮箱',
  `avatar` VARCHAR(255) COMMENT '头像URL',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `idx_user_id` (`user_id`),
  UNIQUE INDEX `idx_phone` (`phone`),
  INDEX `idx_specialty` (`specialty`),
  CONSTRAINT `fk_coach_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教练信息表';

-- 3. 插入默认教练信息
INSERT INTO `coach_info` (`user_id`, `name`, `gender`, `phone`, `specialty`, `experience_years`, `introduction`) VALUES 
(2, '王教练', '男', '13900139000', '力量训练、有氧健身', 5, '拥有5年健身教练经验，擅长制定个性化训练计划。');