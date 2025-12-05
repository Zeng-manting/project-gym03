package com.gym.service;

import com.gym.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

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
     * 支持多条件搜索：姓名、手机号、状态、卡类型
     * @param params 查询参数Map
     * @return 匹配的会员列表
     */
    List<User> searchMembers(Map<String, Object> params);

    /**
     * 禁用用户
     * @param userId 用户ID
     */
    void disableUser(Long userId);
    
    /**
     * 根据手机号查找用户
     * @param phone 手机号
     * @return 用户对象，如果不存在则返回null
     */
    User findByPhone(String phone);
    
    /**
     * 创建教练用户
     * @param phone 手机号
     * @param password 密码
     */
    void createTrainer(String phone, String password);
    
    /**
     * 获取所有教练用户
     * @return 教练用户列表
     */
    List<User> findTrainers();
    
    /**
     * 获取会员总数
     * @return 会员数量
     */
    int countMembers();
    
    /**
     * 获取教练总数
     * @return 教练数量
     */
    int countTrainers();
    
    /**
     * 根据ID获取会员
     * @param id 会员ID
     * @return 会员对象，如果不存在则返回null
     */
    User getMemberById(Long id);
    
    /**
     * 添加会员
     * @param user 会员对象
     */
    void addMember(User user);
    
    /**
     * 更新会员信息
     * @param user 会员对象
     */
    void updateMember(User user);
    
    /**
     * 删除会员
     * @param id 会员ID
     */
    void deleteMember(Long id);
}