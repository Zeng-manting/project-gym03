package com.gym.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvc配置类，用于配置静态资源映射等
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 配置静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置头像文件的访问路径（包括子目录）
        registry.addResourceHandler("/avatar/**")
                .addResourceLocations("file:d:/ZSCzy/AI/gym03/gym-management-system/avatar/")
                .setCachePeriod(3600);
        
        // 配置默认的静态资源位置（classpath:/static/）
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
    }
}
