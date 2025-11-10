package com.gym;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 健身房管理系统应用入口类
 */
@SpringBootApplication // 自动启用组件扫描（扫描当前包及其子包）
public class GymManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(GymManagementApplication.class, args);
    }

}