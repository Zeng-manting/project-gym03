package com.gym.controller;

import com.gym.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 课程控制器
 * 处理课程相关的HTTP请求
 */
@Controller
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * 创建新课程
     * @param name 课程名称
     * @param scheduleTime 排课时间
     * @param trainerId 教练ID
     * @param maxCapacity 最大容量
     * @param attributes 重定向属性
     * @return 重定向到课程管理页面
     */
    @PostMapping("/admin/courses")
    @PreAuthorize("hasRole('ADMIN')")
    public String createCourse(@RequestParam("name") String name,
                              @RequestParam("scheduleTime") String scheduleTime,
                              @RequestParam("trainerId") Long trainerId,
                              @RequestParam("maxCapacity") Integer maxCapacity,
                              RedirectAttributes attributes) {
        try {
            // 解析时间字符串为LocalDateTime
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(scheduleTime, formatter);
            
            // 调用服务创建课程
            courseService.createCourse(name, dateTime, trainerId, maxCapacity);
            
            // 添加成功消息
            attributes.addFlashAttribute("message", "课程创建成功");
        } catch (Exception e) {
            // 添加错误消息
            attributes.addFlashAttribute("error", "课程创建失败：" + e.getMessage());
        }
        
        // 重定向到课程管理页面
        return "redirect:/admin/courses";
    }
}