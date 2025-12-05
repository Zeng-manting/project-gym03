package com.gym.mapper;

import com.gym.entity.MemberInfo;
import org.apache.ibatis.annotations.*;

/**
 * 会员信息Mapper接口
 * 使用@Mapper注解标记，MyBatis会自动扫描并生成实现类
 */
@Mapper
public interface MemberInfoMapper {

    /**
     * 根据用户ID查询会员信息
     * @param userId 用户ID
     * @return 会员信息对象
     */
    @Select("SELECT * FROM member_info WHERE user_id = #{userId}")
    MemberInfo selectByUserId(Long userId);

    /**
     * 根据手机号查询会员信息
     * @param phone 手机号
     * @return 会员信息对象
     */
    @Select("SELECT * FROM member_info WHERE phone = #{phone}")
    MemberInfo selectByPhone(String phone);

    /**
     * 根据ID查询会员信息
     * @param id 会员信息ID
     * @return 会员信息对象
     */
    @Select("SELECT * FROM member_info WHERE id = #{id}")
    MemberInfo selectById(Long id);

    /**
     * 插入会员信息
     * @param memberInfo 会员信息对象
     */
    @Insert("INSERT INTO member_info (user_id, name, gender, birth_date, phone, card_issue_date, " +
            "height, weight, email, address, emergency_contact, emergency_phone, health_condition, avatar) " +
            "VALUES (#{userId}, #{name}, #{gender}, #{birthDate}, #{phone}, #{cardIssueDate}, " +
            "#{height}, #{weight}, #{email}, #{address}, #{emergencyContact}, #{emergencyPhone}, #{healthCondition}, #{avatar})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(MemberInfo memberInfo);

    /**
     * 更新会员信息
     * @param memberInfo 会员信息对象
     */
    @Update("UPDATE member_info SET name = #{name}, gender = #{gender}, birth_date = #{birthDate}, " +
            "phone = #{phone}, card_issue_date = #{cardIssueDate}, height = #{height}, weight = #{weight}, " +
            "email = #{email}, address = #{address}, emergency_contact = #{emergencyContact}, " +
            "emergency_phone = #{emergencyPhone}, health_condition = #{healthCondition}, avatar = #{avatar} " +
            "WHERE user_id = #{userId}")
    void updateByUserId(MemberInfo memberInfo);

    /**
     * 删除会员信息
     * @param userId 用户ID
     */
    @Delete("DELETE FROM member_info WHERE user_id = #{userId}")
    void deleteByUserId(Long userId);

    /**
     * 判断会员信息是否存在
     * @param userId 用户ID
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) FROM member_info WHERE user_id = #{userId}")
    boolean existsByUserId(Long userId);
}