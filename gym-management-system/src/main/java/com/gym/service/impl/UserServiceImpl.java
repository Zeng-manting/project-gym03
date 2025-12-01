package com.gym.service.impl;

import com.gym.entity.MemberInfo;
import com.gym.entity.User;
import com.gym.mapper.UserMapper;
import com.gym.mapper.CoachInfoMapper;
import com.gym.entity.CoachInfo;
import com.gym.service.MemberInfoService;
import com.gym.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // ← 新增导入
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * 用户服务实现类
 * 实现用户相关的业务逻辑，并支持 Spring Security 认证
 */
@Service
public class UserServiceImpl implements UserService, UserDetailsService { // ← 实现 UserDetailsService

    private final UserMapper userMapper;
    private final CoachInfoMapper coachInfoMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, CoachInfoMapper coachInfoMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.coachInfoMapper = coachInfoMapper;
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
    @Autowired
    private MemberInfoService memberInfoService;
    
    @Override
    public List<User> searchMembers(String keyword) {
        List<User> members = userMapper.searchMembersByKeyword(keyword);
        // 为每个会员加载对应的详细信息
        for (User user : members) {
            MemberInfo memberInfo = memberInfoService.getMemberInfoByUserId(user.getId());
            if (memberInfo != null) {
                // 将MemberInfo中的数据设置到User对象中，以便前端显示
                user.setName(memberInfo.getName() != null ? memberInfo.getName() : "未设置");
                user.setGender(memberInfo.getGender() != null ? memberInfo.getGender() : "未知");
                // 计算年龄
                if (memberInfo.getBirthDate() != null) {
                    LocalDate birthDate = memberInfo.getBirthDate();
                    LocalDate now = LocalDate.now();
                    int age = now.getYear() - birthDate.getYear();
                    if (birthDate.getMonthValue() > now.getMonthValue() || 
                        (birthDate.getMonthValue() == now.getMonthValue() && birthDate.getDayOfMonth() > now.getDayOfMonth())) {
                        age--;
                    }
                    user.setAge(age);
                }
                // 设置会员卡类型和有效期（这些信息可能需要从其他服务获取）
                // 这里暂时设置为默认值，实际应用中可能需要从会员卡服务获取
                user.setCardType("标准卡");
                if (memberInfo.getCardIssueDate() != null) {
                    LocalDate issueDate = memberInfo.getCardIssueDate();
                    user.setExpireDate(Date.from(issueDate.plusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                }
            }
        }
        return members;
    }

    /**
     * 禁用用户
     */
    @Override
    public void disableUser(Long userId) {
        userMapper.updateStatus(userId, "disabled");
    }
    
    /**
     * 根据手机号查找用户
     */
    @Override
    public User findByPhone(String phone) {
        return userMapper.findByPhone(phone);
    }
    
    /**
     * 创建教练用户
     */
    @Override
    public void createTrainer(String phone, String password) {
        User user = new User();
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("trainer"); // 设置角色为教练
        userMapper.insertUser(user);
    }
    
    /**
     * 获取所有教练用户
     */
    @Override
    public List<User> findTrainers() {
        List<User> trainers = userMapper.findTrainers();
        
        // 为每个教练用户添加额外信息
        for (User user : trainers) {
            // 从coach_info表中获取教练的真实信息
            CoachInfo coachInfo = coachInfoMapper.findByUserId(user.getId());
            if (coachInfo != null) {
                // 设置教练的真实名称和性别
                if (coachInfo.getName() != null) {
                    user.setName(coachInfo.getName());
                }
                if (coachInfo.getGender() != null) {
                    user.setGender(coachInfo.getGender());
                }
            }
        }
        
        return trainers;
    }
    
    /**
     * 获取会员总数
     */
    @Override
    public int countMembers() {
        return userMapper.countMembers();
    }
    
    /**
     * 获取教练总数
     */
    @Override
    public int countTrainers() {
        return userMapper.countTrainers();
    }
    
    /**
     * 根据ID获取会员信息
     */
    @Override
    public User getMemberById(Long id) {
        return userMapper.selectById(id);
    }
    
    /**
     * 添加会员
     */
    @Override
    public void addMember(User user) {
        User existingUser = userMapper.findByPhone(user.getPhone());
        if (existingUser != null) {
            throw new RuntimeException("手机号已存在");
        }
        user.setRole("member");
        user.setStatus("active");
        userMapper.insert(user);
    }
    
    /**
     * 更新会员信息
     */
    @Override
    public void updateMember(User user) {
        // 检查手机号是否被其他用户使用
        User existingUser = userMapper.findByPhone(user.getPhone());
        if (existingUser != null && !existingUser.getId().equals(user.getId())) {
            throw new RuntimeException("手机号已存在");
        }
        userMapper.updateById(user);
    }
    
    /**
     * 删除会员
     */
    @Override
    public void deleteMember(Long id) {
        userMapper.deleteById(id);
    }
}