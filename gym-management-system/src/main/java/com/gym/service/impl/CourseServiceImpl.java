package com.gym.service.impl;

import com.gym.entity.Course;
import com.gym.mapper.CourseMapper;
import com.gym.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程服务实现类
 * 实现课程相关的业务逻辑
 */
@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    /**
     * 获取所有可用课程
     * 调用mapper的selectAvailableCourses方法
     */
    @Override
    public List<Course> getAvailableCourses() {
        return courseMapper.selectAvailableCourses();
    }

    /**
     * 创建新课程
     * 设置初始currentCount为0，然后插入数据库
     */
    @Override
    public void createCourse(String name, LocalDateTime scheduleTime, Long trainerId, Integer maxCapacity) {
        // 创建课程对象
        Course course = new Course();
        course.setName(name);
        course.setScheduleTime(scheduleTime);
        course.setTrainerId(trainerId);
        course.setMaxCapacity(maxCapacity);
        // 设置初始报名人数为0
        course.setCurrentCount(0);
        
        // 插入课程到数据库
        courseMapper.insert(course);
    }

    /**
     * 检查课程是否已满
     * 查询课程信息，判断currentCount是否大于等于maxCapacity
     */
    @Override
    public boolean isFull(Long courseId) {
        // 根据ID查询课程
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            return true; // 课程不存在，视为已满
        }
        
        // 判断当前报名人数是否大于等于最大容量
        return course.getCurrentCount() >= course.getMaxCapacity();
    }

    /**
     * 获取教练的所有课程
     * 调用mapper的selectByTrainerId方法
     */
    @Override
    public List<Course> getMyCourses(Long trainerId) {
        return courseMapper.selectByTrainerId(trainerId);
    }
}