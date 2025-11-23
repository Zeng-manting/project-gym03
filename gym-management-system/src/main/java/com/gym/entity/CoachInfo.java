package com.gym.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 教练详细信息实体类
 * 对应数据库coach_info表
 */
@Data
public class CoachInfo {
    /**
     * 教练信息ID
     */
    private Long id;
    
    /**
     * 用户ID（关联user表）
     */
    private Long userId;
    
    /**
     * 关联的用户信息
     */
    private User user;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 性别：男、女
     */
    private String gender;
    
    /**
     * 出生日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    
    /**
     * 手机号（与user表同步）
     */
    private String phone;
    
    /**
     * 专业特长
     */
    private String specialty;
    
    /**
     * 教练证书
     */
    private String certification;
    
    /**
     * 工作年限
     */
    private Integer experienceYears;
    
    /**
     * 个人简介
     */
    private String introduction;
    
    /**
     * 教育背景
     */
    private String educationBackground;
    
    /**
     * 联系邮箱
     */
    private String contactEmail;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 状态：active/inactive
     */
    private String status = "active";
}