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
     * @return 登录页面视图名称
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 处理登录请求
     * 注意：此方法实际由Spring Security处理，这里保留接口结构但无需实现逻辑
     */
    @PostMapping("/login")
    public String doLogin() {
        // Spring Security会自动处理登录逻辑
        // 此方法实际上可能不会被调用
        return "redirect:/login";
    }

    /**
     * 显示注册页面
     * @return 注册页面视图名称
     */
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    /**
     * 处理注册请求
     * @param phone 手机号
     * @param password 密码
     * @param redirectAttributes 重定向属性，用于传递错误消息
     * @return 注册成功重定向到登录页，失败重定向到注册页
     */
    @PostMapping("/register")
    public String doRegister(
            @RequestParam String phone,
            @RequestParam String password,
            RedirectAttributes redirectAttributes) {
        
        // 调用UserService进行注册
        boolean success = userService.register(phone, password);
        
        if (success) {
            // 注册成功，重定向到登录页并附带成功消息
            return "redirect:/login?success=注册成功";
        } else {
            // 注册失败，添加错误消息并重定向到注册页
            redirectAttributes.addFlashAttribute("error", "手机号已被注册");
            return "redirect:/register";
        }
    }

    /**
     * 处理登出请求
     * 注意：实际登出逻辑由Spring Security处理，这里主要负责重定向
     * @return 重定向到登录页
     */
    @GetMapping("/logout")
    public String logout() {
        // Spring Security会处理会话清除
        return "redirect:/login";
    }
}