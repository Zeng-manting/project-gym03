package com.gym.mapper;

import com.gym.entity.Course;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 课程Mapper接口
 * 使用@Mapper注解标记，MyBatis会自动扫描并生成实现类
 */
@Mapper
public interface CourseMapper {

    /**
     * 查询所有可用的课程
     * 可用课程指：当前报名人数小于最大容量且上课时间未过期
     * @return 可用课程列表
     */
    @Select("SELECT * FROM course WHERE current_count < max_capacity AND schedule_time > NOW()")
    List<Course> selectAvailableCourses();

    /**
     * 根据ID查询课程
     * @param id 课程ID
     * @return 课程对象
     */
    @Select("SELECT * FROM course WHERE id = #{id}")
    Course selectById(Long id);

    /**
     * 更新课程当前报名人数
     * @param id 课程ID
     * @param currentCount 当前报名人数
     */
    @Update("UPDATE course SET current_count = #{currentCount} WHERE id = #{id}")
    void updateCurrentCount(@Param("id") Long id, @Param("currentCount") int currentCount);

    /**
     * 插入新课程
     * @param course 课程对象
     */
    @Insert("INSERT INTO course (name, schedule_time, trainer_id, max_capacity, current_count) " +
            "VALUES (#{name}, #{scheduleTime}, #{trainerId}, #{maxCapacity}, #{currentCount})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Course course);

    /**
     * 根据教练ID查询课程
     * @param trainerId 教练ID
     * @return 课程列表
     */
    @Select("SELECT * FROM course WHERE trainer_id = #{trainerId}")
    List<Course> selectByTrainerId(Long trainerId);
}