package com.gym.controller;

import com.gym.entity.User;
import com.gym.service.UserService;
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

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
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
}
