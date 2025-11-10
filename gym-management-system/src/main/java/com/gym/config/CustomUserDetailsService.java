package com.gym.config;

import com.gym.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 自定义用户详情服务
 * 实现Spring Security的UserDetailsService接口
 * 用于认证过程中加载用户信息
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    /**
     * 实现Spring Security的UserDetailsService接口
     * 注意：这里的username参数实际上是我们系统中的手机号
     * 
     * @param phone 手机号
     * @return Spring Security的UserDetails对象
     * @throws UsernameNotFoundException 当用户不存在时抛出
     */
    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        return userService.loadUserByPhone(phone);
    }
}