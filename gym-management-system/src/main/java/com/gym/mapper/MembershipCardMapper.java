package com.gym.mapper;

import com.gym.entity.MembershipCard;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 会员卡Mapper接口
 * 使用@Mapper注解标记，MyBatis会自动扫描并生成实现类
 */
@Mapper
public interface MembershipCardMapper {

    /**
     * 查询所有会员卡
     * @return 会员卡列表
     */
    @Select("SELECT * FROM membership_card")
    List<MembershipCard> findAll();

    /**
     * 根据ID查询会员卡
     * @param id 会员卡ID
     * @return 会员卡对象
     */
    @Select("SELECT * FROM membership_card WHERE id = #{id}")
    MembershipCard findById(Long id);

    /**
     * 插入会员卡信息
     * @param card 会员卡对象
     */
    @Insert("INSERT INTO membership_card (name, price, validity_days, description, status, create_time, update_time) " +
            "VALUES (#{name}, #{price}, #{validityDays}, #{description}, #{status}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(MembershipCard card);

    /**
     * 更新会员卡信息
     * @param card 会员卡对象
     */
    @Update("UPDATE membership_card SET name = #{name}, price = #{price}, validity_days = #{validityDays}, " +
            "description = #{description}, status = #{status}, update_time = #{updateTime} WHERE id = #{id}")
    void update(MembershipCard card);

    /**
     * 删除会员卡
     * @param id 会员卡ID
     */
    @Delete("DELETE FROM membership_card WHERE id = #{id}")
    void delete(Long id);

    /**
     * 查询激活状态的会员卡
     * @return 激活状态的会员卡列表
     */
    @Select("SELECT * FROM membership_card WHERE status = 'active'")
    List<MembershipCard> findActiveCards();
}