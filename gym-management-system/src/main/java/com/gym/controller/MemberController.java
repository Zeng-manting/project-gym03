package com.gym.controller;

import com.gym.dto.BookingDTO;
import com.gym.entity.Course;
import com.gym.entity.User;
import com.gym.service.BookingService;
import com.gym.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 会员控制器
 * 处理会员相关的HTTP请求
 */
@Controller
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private BookingService bookingService;

    /**
     * 会员首页重定向到课程列表
     * @return 重定向到课程列表页面
     */
    @GetMapping("")
    @PreAuthorize("hasRole('MEMBER')")
    public String redirectToCourses() {
        return "redirect:/member/courses";
    }

    /**
     * 显示会员首页
     * @return 会员首页视图名称
     */
    @GetMapping("index")
    @PreAuthorize("hasRole('MEMBER')")
    public String index() {
        return "member/index";
    }

    /**
     * 查看可用课程列表
     * @param model 模型对象，用于传递数据到视图
     * @return 课程列表页面
     */
    @GetMapping("courses")
    @PreAuthorize("hasRole('MEMBER')")
    public String viewCourses(Model model) {
        List<Course> courses = courseService.getAvailableCourses();
        model.addAttribute("courses", courses);
        return "member/courses";
    }

    /**
     * 预约课程
     * @param courseId 课程ID
     * @param session HTTP会话，用于获取当前登录用户信息
     * @param redirectAttributes 重定向属性，用于传递消息
     * @return 重定向到课程列表页面
     */
    @PostMapping("book/{courseId}")
    @PreAuthorize("hasRole('MEMBER')")
    public String bookCourse(@PathVariable Long courseId, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            User user = (User) session.getAttribute("user");
            bookingService.bookCourse(user.getId(), courseId);
            redirectAttributes.addFlashAttribute("success", "预约成功！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/member/courses";
    }

    /**
     * 查看我的预约列表
     * @param model 模型对象，用于传递数据到视图
     * @param session HTTP会话，用于获取当前登录用户信息
     * @return 预约列表页面
     */
    @GetMapping("bookings")
    @PreAuthorize("hasRole('MEMBER')")
    public String viewBookings(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<BookingDTO> bookings = bookingService.getMyBookings(user.getId());
        model.addAttribute("bookings", bookings);
        return "member/bookings";
    }

    /**
     * 取消预约
     * @param bookingId 预约ID
     * @param redirectAttributes 重定向属性，用于传递消息
     * @return 重定向到预约列表页面
     */
    @PostMapping("cancel/{bookingId}")
    @PreAuthorize("hasRole('MEMBER')")
    public String cancelBooking(@PathVariable Long bookingId, RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancelBooking(bookingId);
            redirectAttributes.addFlashAttribute("success", "取消预约成功！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/member/bookings";
    }
}