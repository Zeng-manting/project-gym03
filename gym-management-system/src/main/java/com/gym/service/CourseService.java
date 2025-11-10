package com.gym.service;

import com.gym.entity.Course;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程服务接口
 * 提供课程相关的业务逻辑操作
 */
public interface CourseService {

    /**
     * 获取所有可用课程
     * 可用课程指：当前报名人数小于最大容量且上课时间未过期
     * @return 可用课程列表
     */
    List<Course> getAvailableCourses();

    /**
     * 创建新课程
     * @param name 课程名称
     * @param scheduleTime 上课时间
     * @param trainerId 教练ID
     * @param maxCapacity 最大容量
     */
    void createCourse(String name, LocalDateTime scheduleTime, Long trainerId, Integer maxCapacity);

    /**
     * 检查课程是否已满
     * @param courseId 课程ID
     * @return 是否已满
     */
    boolean isFull(Long courseId);

    /**
     * 获取教练的所有课程
     * @param trainerId 教练ID
     * @return 教练的课程列表
     */
    List<Course> getMyCourses(Long trainerId);
}