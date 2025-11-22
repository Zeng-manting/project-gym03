package com.gym.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 会员详细信息实体类
 * 对应数据库member_info表
 */
@Data
public class MemberInfo {
    
    /**
     * 会员信息ID
     */
    private Long id;
    
    /**
     * 用户ID（关联user表）
     */
    private Long userId;
    
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
     * 办卡日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate cardIssueDate;
    
    /**
     * 身高（cm）
     */
    private BigDecimal height;
    
    /**
     * 体重（kg）
     */
    private BigDecimal weight;
    
    /**
     * 电子邮箱
     */
    private String email;
    
    /**
     * 地址
     */
    private String address;
    
    /**
     * 紧急联系人
     */
    private String emergencyContact;
    
    /**
     * 紧急联系电话
     */
    private String emergencyPhone;
    
    /**
     * 健康状况
     */
    private String healthCondition;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 关联的用户信息
     */
    private User user;
}