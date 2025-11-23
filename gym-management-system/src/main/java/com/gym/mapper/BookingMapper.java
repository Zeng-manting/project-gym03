package com.gym.mapper;

import com.gym.entity.Booking;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 预约Mapper接口
 * 使用@Mapper注解标记，MyBatis会自动扫描并生成实现类
 */
@Mapper
public interface BookingMapper {

    /**
     * 插入新预约记录
     * @param booking 预约对象
     */
    @Insert("INSERT INTO booking (user_id, course_id, booking_time) VALUES (#{userId}, #{courseId}, #{bookingTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Booking booking);

    /**
     * 根据用户ID查询预约记录
     * @param userId 用户ID
     * @return 预约记录列表
     */
    @Select("SELECT * FROM booking WHERE user_id = #{userId}")
    List<Booking> selectByUserId(Long userId);

    /**
     * 根据ID删除预约记录
     * @param id 预约记录ID
     */
    @Delete("DELETE FROM booking WHERE id = #{id}")
    void deleteById(Long id);

    /**
     * 检查指定用户是否已预约指定课程
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 是否已预约
     */
    @Select("SELECT COUNT(*) > 0 FROM booking WHERE user_id = #{userId} AND course_id = #{courseId}")
    boolean existsByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);
    
    /**
     * 查询用户的预约记录，关联课程信息
     * @param userId 用户ID
     * @return 预约记录列表
     */
    @Select("SELECT b.id, b.course_id, c.name as course_name, c.schedule_time, c.trainer_id, b.booking_time " +
            "FROM booking b JOIN course c ON b.course_id = c.id WHERE b.user_id = #{userId} ORDER BY c.schedule_time ASC")
    List<Map<String, Object>> selectUserBookingsWithCourseInfo(Long userId);
    
    /**
     * 根据ID查询预约记录
     * @param id 预约ID
     * @return 预约记录
     */
    @Select("SELECT * FROM booking WHERE id = #{id}")
    Booking selectById(Long id);
    
    /**
     * 查询指定课程的所有预约会员信息
     * JOIN user表获取会员详情
     * @param courseId 课程ID
     * @return 预约会员信息列表
     */
    @Select("SELECT b.id as booking_id, u.id as user_id, u.phone, b.booking_time " +
            "FROM booking b JOIN user u ON b.user_id = u.id WHERE b.course_id = #{courseId}")
    List<Map<String, Object>> selectCourseMembers(Long courseId);
}