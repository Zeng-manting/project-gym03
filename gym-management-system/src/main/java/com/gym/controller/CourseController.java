package com.gym.controller;

import com.gym.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
            // 解析时间字符串为LocalDateTime（处理前端datetime-local格式：yyyy-MM-dd'T'HH:mm）
            LocalDateTime dateTime = LocalDateTime.parse(scheduleTime);
            
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
    
    /**
     * 显示编辑课程页面
     * @param id 课程ID
     * @param model 模型
     * @return 编辑课程页面
     */
    @GetMapping("/admin/courses/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditCourse(@PathVariable("id") Long id, Model model) {
        model.addAttribute("course", courseService.getCourseById(id));
        model.addAttribute("pageTitle", "编辑课程");
        return "admin/course-edit";
    }
    
    /**
     * 更新课程信息
     * @param id 课程ID
     * @param name 课程名称
     * @param scheduleTime 排课时间
     * @param trainerId 教练ID
     * @param maxCapacity 最大容量
     * @param attributes 重定向属性
     * @return 重定向到课程列表页面
     */
    @PostMapping("/admin/courses/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateCourse(@RequestParam("id") Long id,
                              @RequestParam("name") String name,
                              @RequestParam("startTime") String scheduleTime,
                              @RequestParam("trainerId") Long trainerId,
                              @RequestParam("maxCapacity") Integer maxCapacity,
                              RedirectAttributes attributes) {
        try {
            // 解析时间字符串为LocalDateTime（处理前端datetime-local格式：yyyy-MM-dd'T'HH:mm）
            LocalDateTime dateTime = LocalDateTime.parse(scheduleTime);
            
            // 调用服务更新课程
            courseService.updateCourse(id, name, dateTime, trainerId, maxCapacity);
            
            // 添加成功消息
            attributes.addFlashAttribute("message", "课程更新成功");
        } catch (Exception e) {
            // 添加错误消息
            attributes.addFlashAttribute("error", "课程更新失败：" + e.getMessage());
        }
        
        // 重定向到课程管理页面
        return "redirect:/admin/courses";
    }
}