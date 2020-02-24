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

    @Update("update persistent_logins set username = #{newUsername} where username = #{oldUsername}")
    void updatePersistentLogins(@Param("oldUsername") String oldUsername, @Param("newUsername") String newUsername);

    @Select("select count(*) from persistent_logins where username = #{username}")
    int selectPersistentLogins(@Param("username") String username);

    @Select("select * from user where phone = #{phone}")
    User findUserByPhone(@Param("phone") String phone);

    @Select("select * from user where username = #{username}")
    User findUserByUsername(@Param("username") String username);

    @Select("select * from user where id = #{userId}")
    User findUserByUserId(@Param("userId") int userId);

    @Select("select id, phone, username, true_name, gender, email, birthday, recently_landed from user order by id desc")
    List<User> findAllUser();

    @Select("select COUNT(id) from article where user_id = #{userId}")
    int countArticleByUser(@Param("userId") int userId);

    @Select("select id, username, personal_brief, avatar_img_url from user where username like '%${keyWords}%' order by id desc")
    List<User> searchByKeyWordsByUsername(@Param("keyWords") String keyWords);

    @Select("select COUNT(id) from user where username like '%${keyWords}%'")
    int countSearchByKeyWordsByUsername(@Param("keyWords") String keyWords);

    List<User> findAllUserToXML(@Param("phoneSearch") String phoneSearch, @Param("usernameSearch") String usernameSearch,
                                @Param("genderSearch") String genderSearch, @Param("firstDate") String firstDate,
                                @Param("lastDate") String lastDate);

    int countAllUserToXML(@Param("phoneSearch") String phoneSearch, @Param("usernameSearch") String usernameSearch,
                          @Param("genderSearch") String genderSearch, @Param("firstDate") String firstDate,
                          @Param("lastDate") String lastDate);
}
