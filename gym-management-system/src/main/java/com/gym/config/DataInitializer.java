package com.gym.config;

import com.gym.entity.User;
import com.gym.mapper.UserMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 启动时初始化默认用户（用于测试登录）
 */
@Configuration
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 默认管理员
        String adminPhone = "13800138000";
        if (userMapper.findByPhone(adminPhone) == null) {
            User admin = new User();
            admin.setPhone(adminPhone);
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRole("admin");
            userMapper.insertUser(admin);
        }

        // 默认教练
        String trainerPhone = "13900139000";
        if (userMapper.findByPhone(trainerPhone) == null) {
            User trainer = new User();
            trainer.setPhone(trainerPhone);
            trainer.setPassword(passwordEncoder.encode("123456"));
            trainer.setRole("trainer");
            userMapper.insertUser(trainer);
        }
    }
}
