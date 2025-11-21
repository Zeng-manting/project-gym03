package com.gym.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 会员卡实体类
 * 对应数据库membership_card表
 */
@Data
public class MembershipCard {
    
    /**
     * 会员卡ID
     */
    private Long id;
    
    /**
     * 会员卡名称
     */
    private String name;
    
    /**
     * 会员卡价格
     */
    private Double price;
    
    /**
     * 会员卡有效期（天数）
     */
    private Integer validityDays;
    
    /**
     * 会员卡描述
     */
    private String description;
    
    /**
     * 会员卡状态
     * 'active': 激活
     * 'inactive': 未激活
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}