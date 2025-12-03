package com.gym.mapper;

import com.gym.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

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
    @Insert("INSERT INTO user (phone, password, role, status) " +
            "VALUES (#{phone}, #{password}, #{role}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertUser(User user);
    
    /**
     * 插入用户信息（兼容方法）
     * @param user 用户对象
     */
    default void insert(User user) {
        insertUser(user);
    }

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户对象
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(Long id);
    
    /**
     * 根据ID更新用户信息
     * @param user 用户对象
     */
    @Update({"UPDATE user SET phone = #{phone}, status = #{status} WHERE id = #{id}"})
    void updateById(User user);
    
    /**
     * 根据ID删除用户
     * @param id 用户ID
     */
    @Delete("DELETE FROM user WHERE id = #{id}")
    void deleteById(Long id);

    /**
     * 查询所有教练用户
     * @return 教练用户列表
     */
    @Select("SELECT * FROM user WHERE role = 'trainer'")
    List<User> findTrainers();
    
    /**
     * 获取所有教练
     * @return 教练列表
     */
    default List<User> getAllTrainers() {
        return findTrainers();
    }

    /**
     * 更新用户状态
     * @param id 用户ID
     * @param status 状态值
     */
    @Update("UPDATE user SET status = #{status} WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") String status);
    
    /**
     * 禁用用户
     * @param id 用户ID
     */
    default void disableUser(Long id) {
        updateStatus(id, "disabled");
    }

    /**
     * 根据关键词模糊搜索会员
     * @param keyword 搜索关键词
     * @return 符合条件的会员列表
     */
    @Select("SELECT * FROM user WHERE role = 'member' AND phone LIKE CONCAT('%', #{keyword}, '%')")
    List<User> searchMembersByKeyword(String keyword);
    
    /**
     * 搜索会员（多条件）
     * @param params 查询参数
     * @return 符合条件的会员列表
     */
    @Select({"<script>",
            "SELECT * FROM user WHERE role = 'member'",
            "<if test='name != null and name != \"\"'> AND name LIKE CONCAT('%', #{name}, '%')</if>",
            "<if test='phone != null and phone != \"\"'> AND phone LIKE CONCAT('%', #{phone}, '%')</if>",
            "<if test='status != null and status != \"\"'> AND status = #{status}</if>",
            "<if test='cardType != null and cardType != \"\"'> AND card_type = #{cardType}</if>",
            " ORDER BY create_time DESC",
            "</script>"
    })
    List<User> searchMembers(Map<String, Object> params);
    
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
    
    /**
     * 获取课程总数
     * @return 课程数量
     */
    @Select("SELECT COUNT(*) FROM course")
    int countCourses();
    
    /**
     * 获取会员卡总数
     * @return 会员卡数量
     */
    @Select("SELECT COUNT(*) FROM membership_card")
    int countCards();
}