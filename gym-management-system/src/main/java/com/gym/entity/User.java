package com.gym.entity;

import lombok.Data;

/**
 * 用户实体类
 * 对应数据库user表
 */
@Data
public class User {
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 手机号（唯一）
     */
    private String phone;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 角色类型
     * 'member': 会员
     * 'trainer': 教练
     * 'admin': 管理员
     */
    private String role;
}