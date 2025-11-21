package com.gym.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 管理员控制器
 * 处理管理员相关的HTTP请求
 */
@Controller
public class AdminController {

    /**
     * 显示管理员首页
     * @return 管理员首页视图名称
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String index() {
        return "admin/index";
    }
}