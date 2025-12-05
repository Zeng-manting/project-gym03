package com.gym.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置头像文件的静态资源映射
        registry.addResourceHandler("/avatar/member/**")
                .addResourceLocations("file:d:/ZSCzy/AI/gym03/gym-management-system/avatar/member/");
    }
}
