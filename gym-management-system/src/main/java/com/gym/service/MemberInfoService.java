package com.gym.service;

import com.gym.entity.MemberInfo;

/**
 * 会员信息服务接口
 */
public interface MemberInfoService {

    /**
     * 根据用户ID获取会员信息
     * @param userId 用户ID
     * @return 会员信息对象
     */
    MemberInfo getMemberInfoByUserId(Long userId);

    /**
     * 根据手机号获取会员信息
     * @param phone 手机号
     * @return 会员信息对象
     */
    MemberInfo getMemberInfoByPhone(String phone);

    /**
     * 更新会员信息
     * @param memberInfo 会员信息对象
     * @return 更新后的会员信息对象
     */
    MemberInfo updateMemberInfo(MemberInfo memberInfo);

    /**
     * 删除会员信息
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteMemberInfo(Long userId);

    /**
     * 判断会员信息是否存在
     * @param userId 用户ID
     * @return 是否存在
     */
    boolean existsMemberInfo(Long userId);

    /**
     * 初始化会员信息（注册时自动创建）
     * @param userId 用户ID
     * @param phone 手机号
     * @return 创建的会员信息对象
     */
    MemberInfo initMemberInfo(Long userId, String phone);

    /**
     * 新增会员信息
     * @param memberInfo 会员信息对象
     * @return 新增的会员信息对象
     */
    MemberInfo addMemberInfo(MemberInfo memberInfo);
    
    /**
     * 根据手机号获取用户ID
     * @param phone 手机号
     * @return 用户ID
     */
    Long getUserIdByPhone(String phone);
}