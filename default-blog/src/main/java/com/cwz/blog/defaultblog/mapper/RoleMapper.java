package com.cwz.blog.defaultblog.mapper;

import com.cwz.blog.defaultblog.entity.Role;
import com.cwz.blog.defaultblog.mapper.bean.BeanMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper extends BeanMapper<Role> {

    @Select("select user_id from user_role where role_id = #{roleId}")
    List<Object> findUserIdByRoleId(@Param("roleId") int roleId);

    @Select("select COUNT(*) from user_role where role_id = #{roleId}")
    int countUserIdByRoleId(@Param("roleId") int roleId);
}
