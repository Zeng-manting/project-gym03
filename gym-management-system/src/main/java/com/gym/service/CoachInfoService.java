package com.gym.service;

import com.gym.entity.CoachInfo;

public interface CoachInfoService {
    /**
     * 根据用户ID获取教练信息
     */
    CoachInfo getCoachInfoByUserId(Long userId);
    
    /**
     * 保存或更新教练信息
     */
    void saveOrUpdateCoachInfo(CoachInfo coachInfo);
    
    /**
     * 创建新的教练信息
     */
    CoachInfo createCoachInfo(CoachInfo coachInfo);
    
    /**
     * 更新教练密码
     */
    boolean updatePassword(Long userId, String currentPassword, String newPassword);
}