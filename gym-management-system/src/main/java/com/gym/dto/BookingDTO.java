package com.gym.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 预约信息DTO（数据传输对象）
 * 用于封装预约相关的展示数据
 */
@Data
public class BookingDTO {

    /**
     * 预约记录ID
     */
    private Long id;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 上课时间
     */
    private LocalDateTime scheduleTime;

    /**
     * 教练ID
     */
    private Long trainerId;
    
    /**
     * 教练姓名
     */
    private String trainerName;

    /**
     * 预约时间
     */
    private LocalDateTime bookingTime;
}