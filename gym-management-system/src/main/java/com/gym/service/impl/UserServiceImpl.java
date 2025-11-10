package com.gym.service.impl;

import com.gym.entity.User;
import com.gym.exception.BusinessException;
import com.gym.mapper.UserMapper;
import com.gym.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用户服务实现类
 * 实现用户相关的业务逻辑
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 用户注册实现
     * 1. 检查手机号是否已存在
     * 2. 对密码进行加密
     * 3. 创建用户记录
     */
    @Override
    public boolean register(String phone, String password) {
        // 检查手机号是否已存在
        User existingUser = userMapper.findByPhone(phone);
        if (existingUser != null) {
            return false;
        }

        // 创建新用户
        User user = new User();
        user.setPhone(phone);
        // 密码加密
        user.setPassword(passwordEncoder.encode(password));
        // 默认注册为会员
        user.setRole("member");

        // 插入用户信息
        userMapper.insertUser(user);
        return true;
    }

    /**
     * 根据手机号加载用户详情（Spring Security使用）
     */
    @Override
    public UserDetails loadUserByPhone(String phone) {
        User user = userMapper.findByPhone(phone);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 将用户角色转换为Spring Security的权限
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()));

        // 返回Spring Security的User对象
        return new org.springframework.security.core.userdetails.User(
                user.getPhone(),
                user.getPassword(),
                authorities
        );
    }

    /**
     * 搜索会员
     * 根据关键词模糊查询手机号
     */
    @Override
    public List<User> searchMembers(String keyword) {
        // 实现SQL LIKE查询，模糊匹配手机号
        return userMapper.searchMembersByKeyword(keyword);
    }

    /**
     * 禁用用户
     * 通过更新用户状态实现
     */
    @Override
    public void disableUser(Long userId) {
        userMapper.updateStatus(userId, "disabled");
    }
}