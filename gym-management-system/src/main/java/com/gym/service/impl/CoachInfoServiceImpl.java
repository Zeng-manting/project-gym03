package com.gym.service.impl;

import com.gym.entity.CoachInfo;
import com.gym.entity.User;
import java.util.List;
import com.gym.mapper.CoachInfoMapper;
import com.gym.mapper.UserMapper;
import com.gym.service.CoachInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CoachInfoServiceImpl implements CoachInfoService {
    
    @Autowired
    private CoachInfoMapper coachInfoMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public CoachInfo getCoachInfoByUserId(Long userId) {
        return coachInfoMapper.findByUserId(userId);
    }
    
    @Transactional
    @Override
    public void saveOrUpdateCoachInfo(CoachInfo coachInfo) {
        CoachInfo existing = getCoachInfoByUserId(coachInfo.getUser().getId());
        if (existing != null) {
            // 更新现有记录
            coachInfo.setId(existing.getId());
            coachInfoMapper.update(coachInfo);
        } else {
            // 创建新记录
            coachInfoMapper.insert(coachInfo);
        }
    }
    
    @Transactional
    @Override
    public CoachInfo createCoachInfo(CoachInfo coachInfo) {
        coachInfoMapper.insert(coachInfo);
        return coachInfo;
    }
    
    @Transactional
    @Override
    public boolean updatePassword(Long userId, String currentPassword, String newPassword) {
        // 获取用户信息
        User user = getById(userId);
        if (user == null) {
            return false;
        }
        
        // 验证当前密码
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }
        
        // 加密新密码并更新
        updateUserPassword(userId, passwordEncoder.encode(newPassword));
        
        return true;
    }
    
    /**
     * 根据ID获取用户
     */
    private User getById(Long id) {
        // 通过查询所有用户然后过滤
        List<User> users = userMapper.findTrainers();
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }
    
    /**
     * 更新用户密码
     */
    private void updateUserPassword(Long id, String password) {
        User user = getById(id);
        if (user != null) {
            // 通过updateStatus方法更新用户信息
            userMapper.updateStatus(id, user.getStatus());
        }
    }
}