package com.gym.service;

import com.gym.dto.BookingDTO;
import java.util.List;

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
}