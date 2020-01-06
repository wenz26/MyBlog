package com.cwz.blog.defaultblog.mapper;

import com.cwz.blog.defaultblog.entity.User;
import com.cwz.blog.defaultblog.mapper.bean.BeanMapper;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper extends BeanMapper<User> {

    @Insert("insert into user_role(user_id, role_id) values (#{userId}, #{roleId})")
    void saveRole(@Param("userId") int userId, @Param("roleId") int roleId);

    @Select("select role_id from user_role where user_id = #{userId}")
    List<Object> findRoleIdByUserId(@Param("userId") int userId);

    User getUsernameAndRolesByPhone(@Param("phone") String phone);

    @Update("update user set password = #{password} where phone = #{phone}")
    int updatePassword(@Param("phone") String phone, @Param("password") String password);

    @Update("update user set recently_landed = #{recentlyLanded} where username = #{username}")
    int updateRecentlyLanded(@Param("username") String username, @Param("recentlyLanded") LocalDateTime recentlyLanded);

    @Update("update user set avatar_img_url = #{avatarImgUrl} where id = #{id}")
    int updateAvatarImgUrlById(@Param("avatarImgUrl") String avatarImgUrl, @Param("id") int id);
}
