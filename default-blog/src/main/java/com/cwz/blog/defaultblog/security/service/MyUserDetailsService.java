package com.cwz.blog.defaultblog.security.service;

import com.cwz.blog.defaultblog.entity.Role;
import com.cwz.blog.defaultblog.entity.User;
import com.cwz.blog.defaultblog.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: 陈文振
 * @date: 2020/1/2
 * @description: UserDetails 对用户进行认证处理
 */
@Component("userDetailsService")
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {

        User user = userMapper.getUsernameAndRolesByPhone(phone);
        //logger.info("获取的用户名及其权限为：" + JSON.toJSONString(user));

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 用户的角色
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Role role : user.getRole()) {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        }

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }


}
