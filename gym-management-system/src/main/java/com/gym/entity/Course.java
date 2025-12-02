package com.gym.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 课程实体类
 * 对应数据库course表
 */
@Data
public class Course {
    
    /**
     * 课程ID
     */
    private Long id;
    
    /**
     * 课程名称
     */
    private String name;
    
    /**
     * 上课时间
     */
    private LocalDateTime scheduleTime;
    
    /**
     * 教练ID
     */
    private Long trainerId;
    
    /**
     * 最大容量
     */
    private Integer maxCapacity;
    
    /**
     * 当前报名人数
     */
    private Integer currentCount;
    
    /**
     * 教练姓名（非数据库字段，用于显示）
     */
    private String coachName;
}