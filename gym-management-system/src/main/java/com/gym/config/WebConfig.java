package com.gym.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 配置视图控制器，实现简单的路径重定向和转发
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 注册视图控制器
     * 用于配置简单的URL到视图的映射，无需编写Controller类
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 根路径重定向到登录页面
        registry.addViewController("/").setViewName("redirect:/login");
        
        // 会员首页转发
        registry.addViewController("/member").setViewName("forward:/member/index");
        
        // 教练首页转发
        registry.addViewController("/coach").setViewName("forward:/coach/index");
        
        // 管理员首页转发
        registry.addViewController("/admin").setViewName("forward:/admin/index");
    }
}