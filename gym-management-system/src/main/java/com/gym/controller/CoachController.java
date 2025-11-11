package com.gym.controller;

import com.gym.entity.Course;
import com.gym.entity.User;
import com.gym.service.BookingService;
import com.gym.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 教练控制器
 * 处理教练相关的HTTP请求
 */
@Controller
@RequestMapping("/coach")
public class CoachController {

    @Autowired
    private CourseService courseService;
    
    @Autowired
    private BookingService bookingService;

    /**
     * 显示教练首页
     * @return 教练首页视图名称
     */
    @GetMapping("")
    @PreAuthorize("hasRole('TRAINER')")
    public String index() {
        return "coach/index";
    }

    /**
     * 查看教练负责的所有课程
     * @param model 模型对象，用于传递数据到视图
     * @param session HTTP会话，用于获取当前登录用户信息
     * @return 课程列表页面
     */
    @GetMapping("courses")
    @PreAuthorize("hasRole('TRAINER')")
    public String viewCourses(Model model, HttpSession session) {
        // 从会话中获取当前登录的教练用户
        User user = (User) session.getAttribute("user");
        // 获取当前教练的所有课程
        List<Course> courses = courseService.getMyCourses(user.getId());
        // 添加课程列表到模型中
        model.addAttribute("courses", courses);
        return "coach/courses";
    }

    /**
     * 查看指定课程的所有预约会员
     * @param courseId 课程ID
     * @param model 模型对象，用于传递数据到视图
     * @return 课程会员列表页面
     */
    @GetMapping("course/{courseId}/members")
    @PreAuthorize("hasRole('TRAINER')")
    public String viewCourseMembers(@PathVariable Long courseId, Model model) {
        // 查询该课程的所有预约会员信息
        List<Map<String, Object>> members = bookingService.getCourseMembers(courseId);
        // 添加会员列表到模型中
        model.addAttribute("members", members);
        model.addAttribute("courseId", courseId);
        return "coach/course-members";
    }
}