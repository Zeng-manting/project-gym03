package com.gym.service.impl;

import com.gym.dto.BookingDTO;
import com.gym.entity.Booking;
import com.gym.entity.Course;
import com.gym.mapper.BookingMapper;
import com.gym.mapper.CourseMapper;
import com.gym.service.BookingService;
import com.gym.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 预约服务实现类
 * 实现课程预约相关的业务逻辑
 */
@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseService courseService;

    /**
     * 预约课程实现
     * 1. 检查课程是否已满
     * 2. 检查是否已经预约过该课程
     * 3. 创建预约记录
     * 4. 更新课程当前报名人数
     */
    @Override
    @Transactional
    public void bookCourse(Long userId, Long courseId) {
        // 检查课程是否已满
        if (courseService.isFull(courseId)) {
            throw new RuntimeException("课程已满，无法预约");
        }

        // 检查是否已经预约过该课程（防重复预约）
        if (bookingMapper.existsByUserIdAndCourseId(userId, courseId)) {
            throw new RuntimeException("您已经预约过该课程");
        }

        // 创建预约记录
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setCourseId(courseId);
        booking.setBookingTime(LocalDateTime.now());
        
        // 插入预约记录
        bookingMapper.insert(booking);

        // 更新课程当前报名人数（+1）
        Course course = courseMapper.selectById(courseId);
        if (course != null) {
            int newCount = course.getCurrentCount() + 1;
            courseMapper.updateCurrentCount(courseId, newCount);
        }
    }

    /**
     * 获取用户的所有预约实现
     * 联表查询预约记录和课程信息，返回DTO对象
     */
    @Override
    public List<BookingDTO> getMyBookings(Long userId) {
        // 联表查询用户的预约记录和课程信息
        List<Map<String, Object>> bookingMaps = bookingMapper.selectUserBookingsWithCourseInfo(userId);
        List<BookingDTO> bookingDTOs = new ArrayList<>();

        // 将查询结果转换为DTO对象
        for (Map<String, Object> map : bookingMaps) {
            BookingDTO dto = new BookingDTO();
            dto.setId(Long.valueOf(map.get("id").toString()));
            dto.setCourseId(Long.valueOf(map.get("course_id").toString()));
            dto.setCourseName(map.get("course_name").toString());
            dto.setScheduleTime((LocalDateTime) map.get("schedule_time"));
            dto.setTrainerId(Long.valueOf(map.get("trainer_id").toString()));
            dto.setBookingTime((LocalDateTime) map.get("booking_time"));
            bookingDTOs.add(dto);
        }

        return bookingDTOs;
    }

    /**
     * 取消预约实现
     * 1. 先查询预约记录获取课程ID
     * 2. 删除预约记录
     * 3. 更新课程当前报名人数（-1）
     */
    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        // 根据ID查询预约记录
        Booking booking = bookingMapper.selectById(bookingId);
        if (booking == null) {
            throw new RuntimeException("预约记录不存在");
        }
        
        Long courseId = booking.getCourseId();

        // 删除预约记录
        bookingMapper.deleteById(bookingId);

        // 更新课程当前报名人数（-1）
        Course course = courseMapper.selectById(courseId);
        if (course != null) {
            int newCount = Math.max(0, course.getCurrentCount() - 1); // 确保人数不为负数
            courseMapper.updateCurrentCount(courseId, newCount);
        }
    }
}