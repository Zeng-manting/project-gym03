package com.gym.mapper;

import com.gym.entity.CoachInfo;
import org.apache.ibatis.annotations.*;

@Mapper
public interface CoachInfoMapper {
    @Insert("INSERT INTO coach_info (user_id, name, gender, birth_date, phone, specialty, certification, introduction, avatar) VALUES (#{userId}, #{name}, #{gender}, #{birthDate}, #{phone}, #{specialty}, #{certification}, #{introduction}, #{avatar})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(CoachInfo coachInfo);
    
    @Select("SELECT * FROM coach_info WHERE user_id = #{userId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "user.id", column = "user_id"),
        @Result(property = "name", column = "name"),
        @Result(property = "gender", column = "gender"),
        @Result(property = "birthDate", column = "birth_date"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "specialty", column = "specialty"),
        @Result(property = "certification", column = "certification"),
        @Result(property = "experienceYears", column = "experience_years"),
        @Result(property = "introduction", column = "introduction"),
        @Result(property = "educationBackground", column = "education_background"),
        @Result(property = "avatar", column = "avatar"),

        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    CoachInfo findByUserId(Long userId);
    
    @Update("UPDATE coach_info SET name = #{name}, gender = #{gender}, birth_date = #{birthDate}, phone = #{phone}, specialty = #{specialty}, certification = #{certification}, introduction = #{introduction}, avatar = #{avatar} WHERE user_id = #{userId}")
    void update(CoachInfo coachInfo);
    
    @Delete("DELETE FROM coach_info WHERE user_id = #{userId}")
    void deleteByUserId(Long userId);
    
    @Select("SELECT * FROM coach_info WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "user.id", column = "user_id"),
        @Result(property = "name", column = "name"),
        @Result(property = "gender", column = "gender"),
        @Result(property = "birthDate", column = "birth_date"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "specialty", column = "specialty"),
        @Result(property = "certification", column = "certification"),
        @Result(property = "experienceYears", column = "experience_years"),
        @Result(property = "introduction", column = "introduction"),
        @Result(property = "educationBackground", column = "education_background"),

        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    CoachInfo findById(Long id);
}