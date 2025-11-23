package com.gym.mapper;

import com.gym.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户Mapper接口
 * 使用@Mapper注解标记，MyBatis会自动扫描并生成实现类
 */
@Mapper
public interface UserMapper {

    /**
     * 根据手机号查找用户
     * @param phone 手机号
     * @return 用户对象
     */
    @Select("SELECT * FROM user WHERE phone = #{phone}")
    User findByPhone(String phone);

    /**
     * 插入用户信息
     * @param user 用户对象
     */
    @Insert("INSERT INTO user (phone, password, role) VALUES (#{phone}, #{password}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertUser(User user);

    /**
     * 查询所有教练用户
     * @return 教练用户列表
     */
    @Select("SELECT * FROM user WHERE role = 'trainer'")
    List<User> findTrainers();

    /**
     * 更新用户状态
     * @param id 用户ID
     * @param status 状态值
     */
    @Update("UPDATE user SET status = #{status} WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 根据关键词模糊搜索会员
     * @param keyword 搜索关键词
     * @return 符合条件的会员列表
     */
    @Select("SELECT * FROM user WHERE role = 'member' AND phone LIKE CONCAT('%', #{keyword}, '%')")
    List<User> searchMembersByKeyword(String keyword);
    
    /**
     * 获取会员总数
     * @return 会员数量
     */
    @Select("SELECT COUNT(*) FROM user WHERE role = 'member'")
    int countMembers();
    
    /**
     * 获取教练总数
     * @return 教练数量
     */
    @Select("SELECT COUNT(*) FROM user WHERE role = 'trainer'")
    int countTrainers();
}