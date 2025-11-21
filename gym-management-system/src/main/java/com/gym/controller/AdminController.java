package com.gym.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 管理员控制器
 * 处理管理员相关的请求
 */
@Controller
public class AdminController {

    /**
     * 管理员首页
     * @return admin/index视图
     */
    @GetMapping("/admin")
    public String adminIndex() {
        return "admin/index";
    }
}