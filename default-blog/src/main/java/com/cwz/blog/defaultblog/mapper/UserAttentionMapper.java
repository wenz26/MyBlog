package com.cwz.blog.defaultblog.mapper;

import com.cwz.blog.defaultblog.entity.User;
import com.cwz.blog.defaultblog.entity.UserAttention;
import com.cwz.blog.defaultblog.mapper.bean.BeanMapper;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserAttentionMapper extends BeanMapper<UserAttention> {

    @Select("select attention_date from user_attention where user_id = #{userId} and attention_user_id = #{personId}")
    UserAttention isAttention(@Param("personId") int personId, @Param("userId") int userId);

    @Insert("insert into user_attention(user_id, attention_user_id, attention_date) values (#{userId}, #{attentionId}, #{attentionDate})")
    int insertUserAttention(@Param("userId") int userId, @Param("attentionId") int attentionId, @Param("attentionDate") LocalDateTime attentionDate);

    List<UserAttention> getUserAttentionByUserId(@Param("userId") int userId, @Param("inquireName") String inquireName);

    int countUserAttentionByUserId(@Param("userId") int userId, @Param("inquireName") String inquireName);

    List<UserAttention> getUserAttentionByAttentionId(@Param("attentionId") int attentionId, @Param("inquireName") String inquireName);

    int countUserAttentionByAttentionId(@Param("attentionId") int attentionId, @Param("inquireName") String inquireName);

    @Delete("delete from user_attention where id = #{attentionId}")
    void deleteUserAttention(@Param("attentionId") int attentionId);

    @Select("select id, username, avatar_img_url from user where id = #{userId}")
    User findUserInfoByAttention(@Param("userId") int userId);

    @Select("select COUNT(id) from user_attention where user_id = #{userId}")
    int countAttention(@Param("userId") int userId);

    @Select("select COUNT(id) from user_attention where attention_user_id = #{userId}")
    int countFan(@Param("userId") int userId);
}
