package com.gym.controller;

import com.gym.entity.User;
import com.gym.service.UserService;
import com.gym.service.CourseService;
import com.gym.service.MembershipCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 管理员控制器
 * 处理管理员相关的HTTP请求
 */
@Controller
public class AdminController {

    private final UserService userService;
    private final CourseService courseService;
    private final MembershipCardService membershipCardService;

    @Autowired
    public AdminController(UserService userService, CourseService courseService, MembershipCardService membershipCardService) {
        this.userService = userService;
        this.courseService = courseService;
        this.membershipCardService = membershipCardService;
    }

    /**
     * 显示管理员首页
     * @return 管理员首页视图名称
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String index() {
        return "admin/index";
    }

    /**
     * 显示会员列表页面
     * @param keyword 搜索关键字
     * @param model 模型对象
     * @return 会员列表视图名称
     */
    @GetMapping("/admin/members")
    @PreAuthorize("hasRole('ADMIN')")
    public String listMembers(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword, Model model) {
        List<User> members = userService.searchMembers(keyword);
        model.addAttribute("members", members);
        model.addAttribute("keyword", keyword);
        return "admin/members";
    }

    /**
     * 禁用会员账号
     * @param id 会员ID
     * @param attributes 重定向属性
     * @return 重定向到会员列表页面
     */
    @PostMapping("/admin/members/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public String disableMember(@PathVariable("id") Long id, RedirectAttributes attributes) {
        userService.disableUser(id);
        attributes.addFlashAttribute("message", "会员账号已成功禁用");
        return "redirect:/admin/members";
    }

    /**
     * 创建教练用户
     * @param phone 手机号
     * @param password 密码
     * @param attributes 重定向属性
     * @return 重定向到教练列表页面
     */
    @PostMapping("/admin/coaches")
    @PreAuthorize("hasRole('ADMIN')")
    public String createCoach(@RequestParam("phone") String phone, 
                             @RequestParam("password") String password, 
                             RedirectAttributes attributes) {
        // 检查手机号是否已存在
        User existingUser = userService.findByPhone(phone);
        if (existingUser != null) {
            attributes.addFlashAttribute("error", "手机号已被注册");
            return "redirect:/admin/coaches";
        }

        // 创建教练用户
        userService.createTrainer(phone, password);
        attributes.addFlashAttribute("message", "教练账号创建成功");
        return "redirect:/admin/coaches";
    }
    
    /**
     * 逻辑删除教练（更新状态为禁用）
     * @param id 教练ID
     * @param attributes 重定向属性
     * @return 重定向到教练列表页面
     */
    @DeleteMapping("/admin/coaches/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCoach(@PathVariable("id") Long id, RedirectAttributes attributes) {
        userService.disableUser(id);
        attributes.addFlashAttribute("message", "教练账号已成功禁用");
        return "redirect:/admin/coaches";
    }
    
    /**
     * 显示课程管理页面
     * @param model 模型对象
     * @return 课程管理视图名称
     */
    @GetMapping("/admin/courses")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCourses(Model model) {
        // 获取所有教练列表
        List<User> trainers = userService.findTrainers();
        // 获取所有可用课程
        List<com.gym.entity.Course> courses = courseService.getAvailableCourses();
        model.addAttribute("trainers", trainers);
        model.addAttribute("courses", courses);
        return "admin/courses";
    }
    

}
