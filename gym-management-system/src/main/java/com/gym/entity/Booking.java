package com.gym.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 预约实体类
 * 对应数据库booking表
 */
@Data
public class Booking {
    
    /**
     * 预约ID
     */
    private Long id;
    
    /**
     * 用户ID（外键，关联user表）
     */
    private Long userId;
    
    /**
     * 课程ID（外键，关联course表）
     */
    private Long courseId;
    
    /**
     * 预约时间
     */
    private LocalDateTime bookingTime;
}