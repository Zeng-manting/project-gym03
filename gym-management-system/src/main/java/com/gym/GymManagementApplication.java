package com.gym;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 健身房管理系统应用入口类
 */
@SpringBootApplication
@MapperScan("com.gym.mapper") // ← 添加这一行！
public class GymManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(GymManagementApplication.class, args);
    }
}