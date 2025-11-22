package com.gym.service.impl;

import com.gym.entity.MemberInfo;
import com.gym.entity.User;
import com.gym.mapper.MemberInfoMapper;
import com.gym.mapper.UserMapper;
import com.gym.service.MemberInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 会员信息服务实现类
 */
@Service
public class MemberInfoServiceImpl implements MemberInfoService {
    
    @Autowired
    private MemberInfoMapper memberInfoMapper;
    
    @Autowired
    private UserMapper userMapper;

    @Override
    public MemberInfo getMemberInfoByUserId(Long userId) {
        return memberInfoMapper.selectByUserId(userId);
    }

    @Override
    public MemberInfo getMemberInfoByPhone(String phone) {
        return memberInfoMapper.selectByPhone(phone);
    }

    @Override
    @Transactional
    public MemberInfo addMemberInfo(MemberInfo memberInfo) {
        // 设置办卡日期为当前日期（如果未设置）
        if (memberInfo.getCardIssueDate() == null) {
            memberInfo.setCardIssueDate(LocalDate.now());
        }
        memberInfoMapper.insert(memberInfo);
        return memberInfo;
    }

    @Override
    @Transactional
    public MemberInfo updateMemberInfo(MemberInfo memberInfo) {
        // 检查是否存在该会员信息
        if (!memberInfoMapper.existsByUserId(memberInfo.getUserId())) {
            throw new RuntimeException("会员信息不存在");
        }
        memberInfoMapper.updateByUserId(memberInfo);
        return memberInfoMapper.selectByUserId(memberInfo.getUserId());
    }

    @Override
    @Transactional
    public boolean deleteMemberInfo(Long userId) {
        // 检查是否存在该会员信息
        if (!memberInfoMapper.existsByUserId(userId)) {
            return false;
        }
        memberInfoMapper.deleteByUserId(userId);
        return true;
    }

    @Override
    public boolean existsMemberInfo(Long userId) {
        return memberInfoMapper.existsByUserId(userId);
    }

    @Override
    @Transactional
    public MemberInfo initMemberInfo(Long userId, String phone) {
        // 检查是否已存在
        if (memberInfoMapper.existsByUserId(userId)) {
            return memberInfoMapper.selectByUserId(userId);
        }
        
        // 创建默认会员信息
        MemberInfo memberInfo = new MemberInfo();
        memberInfo.setUserId(userId);
        memberInfo.setPhone(phone);
        memberInfo.setCardIssueDate(LocalDate.now());
        memberInfo.setName("未设置姓名");
        memberInfo.setGender("未知");
        
        memberInfoMapper.insert(memberInfo);
        return memberInfo;
    }
    
    @Override
    public Long getUserIdByPhone(String phone) {
        // 首先尝试从memberInfo表查询
        MemberInfo memberInfo = memberInfoMapper.selectByPhone(phone);
        if (memberInfo != null) {
            return memberInfo.getUserId();
        }
        
        // 如果memberInfo表中没有记录，从User表查询
        User user = userMapper.findByPhone(phone);
        if (user != null) {
            return user.getId();
        }
        
        // 如果User表中也没有记录，抛出异常
        throw new RuntimeException("未找到该手机号对应的用户信息");
    }
}