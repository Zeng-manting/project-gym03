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
    
    /**
     * 用户状态
     * 'active': 活跃
     * 'disabled': 禁用
     */
    private String status = "active";
    
    /**
     * 会员详细信息（一对一关联）
     * 仅对会员角色有效
     */
    private MemberInfo memberInfo;
}