package com.gym.service;

import com.gym.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * 用户服务接口
 * 提供用户相关的业务逻辑操作
 */
public interface UserService {

    /**
     * 用户注册
     * @param phone 手机号
     * @param password 密码
     * @return 是否注册成功
     * @throws com.gym.exception.BusinessException 当手机号已存在时抛出业务异常
     */
    boolean register(String phone, String password);

    /**
     * 根据手机号加载用户详情
     * 供Spring Security使用
     * @param phone 手机号
     * @return Spring Security的UserDetails对象
     */
    UserDetails loadUserByPhone(String phone);

    /**
     * 搜索会员
     * 模糊查询手机号或姓名
     * @param keyword 搜索关键字
     * @return 匹配的会员列表
     */
    List<User> searchMembers(String keyword);

    /**
     * 禁用用户
     * @param userId 用户ID
     */
    void disableUser(Long userId);
}