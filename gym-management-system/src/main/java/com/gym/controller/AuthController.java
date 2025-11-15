package com.gym.controller;

import com.gym.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 认证控制器
 * 处理用户登录、注册和登出相关请求
 */
@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 显示登录页面
     */
    @GetMapping("/login")
    public String login() {
        return "login"; // 返回 templates/login.html
    }

    // ✅ 删除了 @PostMapping("/login") 方法！让 Spring Security 全权处理

    /**
     * 显示注册页面
     */
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    /**
     * 处理注册请求
     */
    @PostMapping("/register")
    public String doRegister(
            @RequestParam String phone,
            @RequestParam String password,
            RedirectAttributes redirectAttributes) {
        
        boolean success = userService.register(phone, password);
        
        if (success) {
            return "redirect:/login?success=注册成功";
        } else {
            redirectAttributes.addFlashAttribute("error", "手机号已被注册");
            return "redirect:/register";
        }
    }

    /**
     * 处理登出请求（可选，Spring Security 默认处理 /logout）
     */
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }
}