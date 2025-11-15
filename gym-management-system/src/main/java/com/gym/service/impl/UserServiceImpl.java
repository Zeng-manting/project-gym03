package com.gym.service.impl;

import com.gym.entity.User;
import com.gym.mapper.UserMapper;
import com.gym.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // ← 新增导入
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户服务实现类
 * 实现用户相关的业务逻辑，并支持 Spring Security 认证
 */
@Service
public class UserServiceImpl implements UserService, UserDetailsService { // ← 实现 UserDetailsService

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 用户注册实现
     */
    @Override
    public boolean register(String phone, String password) {
        User existingUser = userMapper.findByPhone(phone);
        if (existingUser != null) {
            return false;
        }

        User user = new User();
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("member"); // 默认角色为小写 "member"

        userMapper.insertUser(user);
        return true;
    }

    /**
     * 根据手机号加载用户详情（供内部使用）
     */
    @Override
    public UserDetails loadUserByPhone(String phone) {
        User user = userMapper.findByPhone(phone);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + phone);
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        // 将数据库中的 role（如 "member"）转为 "ROLE_MEMBER"
        String role = user.getRole() != null ? user.getRole().toUpperCase() : "MEMBER";
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

        return new org.springframework.security.core.userdetails.User(
                user.getPhone(),
                user.getPassword(), // 必须是 BCrypt 加密后的
                authorities
        );
    }

    // ✅ 关键：实现 UserDetailsService 接口的方法
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Spring Security 表单提交的 "username" 字段实际是手机号
        return loadUserByPhone(username);
    }

    /**
     * 搜索会员
     */
    @Override
    public List<User> searchMembers(String keyword) {
        return userMapper.searchMembersByKeyword(keyword);
    }

    /**
     * 禁用用户
     */
    @Override
    public void disableUser(Long userId) {
        userMapper.updateStatus(userId, "disabled");
    }
}