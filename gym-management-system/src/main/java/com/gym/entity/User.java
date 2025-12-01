package com.gym.entity;

import lombok.Data;
import java.util.Date;

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
     * 用户名称
     */
    private String name;
    
    /**
     * 性别
     */
    private String gender;
    
    /**
     * 出生日期
     */
    private Date birthDate;
    
    /**
     * 注册日期
     */
    private Date registrationDate;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 地址
     */
    private String address;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 紧急联系人
     */
    private String emergencyContact;
    
    /**
     * 紧急联系电话
     */
    private String emergencyPhone;
    
    /**
     * 用户状态
     * 'active': 活跃
     * 'disabled': 禁用
     */
    private String status = "active";
    
    /**
     * 创建时间
     */
    private Date createdAt;
    
    /**
     * 更新时间
     */
    private Date updatedAt;
    
    /**
     * 年龄（非数据库字段，用于显示）
     */
    private Integer age;
    
    /**
     * 会员卡类型（非数据库字段，用于显示）
     */
    private String cardType;
    
    /**
     * 有效期至（非数据库字段，用于显示）
     */
    private Date expireDate;
}