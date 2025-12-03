package com.gym.service.impl;

import com.gym.entity.Course;
import com.gym.entity.User;
import com.gym.mapper.BookingMapper;
import com.gym.mapper.CourseMapper;
import com.gym.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * 课程服务实现类
 * 实现课程相关的业务逻辑
 */
@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private BookingMapper bookingMapper;

    /**
     * 获取所有可用课程
     * 调用mapper的selectAvailableCourses方法
     */
    @Override
    public List<Course> getAvailableCourses() {
        return courseMapper.selectAvailableCourses();
    }
    
    /**
     * 根据ID获取课程
     */
    @Override
    public Course getCourseById(Long id) {
        return courseMapper.selectById(id);
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
    
    /**
     * 通过课程ID获取该课程的所有预约会员
     * 通过booking表JOIN user表查询，将结果转换为User对象列表
     */
    @Override
    public List<User> getMembersByCourseId(Long courseId) {
        // 调用BookingMapper查询会员信息
        List<Map<String, Object>> memberMaps = bookingMapper.selectCourseMembers(courseId);
        List<User> members = new ArrayList<>();
        
        // 将查询结果转换为User对象
        for (Map<String, Object> map : memberMaps) {
            User user = new User();
            user.setId(Long.valueOf(map.get("user_id").toString()));
            user.setPhone(map.get("phone").toString());
            members.add(user);
        }
        
        return members;
    }
    
    /**
     * 获取课程总数
     */
    @Override
    public int countCourses() {
        return courseMapper.countCourses();
    }
    
    /**
     * 更新课程信息
     * 实现CourseService接口的updateCourse方法
     */
    @Override
    public void updateCourse(Long id, String name, LocalDateTime scheduleTime, Long trainerId, Integer maxCapacity) {
        // 首先验证课程是否存在
        Course existingCourse = courseMapper.selectById(id);
        if (existingCourse == null) {
            throw new RuntimeException("课程不存在，ID: " + id);
        }
        
        // 创建课程对象，设置需要更新的字段
        Course course = new Course();
        course.setId(id);
        course.setName(name);
        course.setScheduleTime(scheduleTime);
        course.setTrainerId(trainerId);
        course.setMaxCapacity(maxCapacity);
        // 保留原有的当前报名人数
        course.setCurrentCount(existingCourse.getCurrentCount());
        // 设置更新时间
        course.setUpdatedAt(LocalDateTime.now());
        
        // 调用mapper更新课程并检查结果
        int rowsAffected = courseMapper.updateCourse(course);
        if (rowsAffected == 0) {
            throw new RuntimeException("更新课程失败，未找到匹配的课程记录，ID: " + id);
        }
    }
}