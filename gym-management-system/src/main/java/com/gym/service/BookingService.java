package com.gym.service;

import com.gym.dto.BookingDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 预约服务接口
 * 提供课程预约相关的业务逻辑操作
 */
public interface BookingService {

    /**
     * 预约课程
     * @param userId 用户ID
     * @param courseId 课程ID
     */
    void bookCourse(Long userId, Long courseId);

    /**
     * 获取用户的所有预约
     * @param userId 用户ID
     * @return 预约信息DTO列表
     */
    List<BookingDTO> getMyBookings(Long userId);

    /**
     * 取消预约
     * @param bookingId 预约记录ID
     */
    void cancelBooking(Long bookingId);
    
    /**
     * 查询指定课程的所有预约会员
     * @param courseId 课程ID
     * @return 预约会员信息列表
     */
    List<Map<String, Object>> getCourseMembers(Long courseId);
    
    /**
     * 计算教练今日的预约数量
     * @param trainerId 教练ID
     * @param date 指定日期
     * @return 今日预约数量
     */
    int countTodayBookingsByTrainerId(Long trainerId, LocalDate date);
    
    /**
     * 计算预约过该教练课程的唯一学员数量
     * @param trainerId 教练ID
     * @return 学员数量
     */
    int countUniqueStudentsByTrainerId(Long trainerId);
}