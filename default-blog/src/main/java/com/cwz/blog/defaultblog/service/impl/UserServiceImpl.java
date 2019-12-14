package com.cwz.blog.defaultblog.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.entity.User;
import com.cwz.blog.defaultblog.mapper.UserMapper;
import com.cwz.blog.defaultblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Objects;

/**
 * @author: 陈文振
 * @date: 2019/12/11
 * @description: user表接口具体业务逻辑
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findUserByPhone(String phone) {
        Example userExample = new Example(User.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("phone", phone);
        return userMapper.selectOneByExample(userExample);
    }

    @Override
    public String findUsernameById(int id) {
        Example userExample = new Example(User.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("id", id);
        return userMapper.selectOneByExample(userExample).getUsername();
    }

    @Override
    public String insert(User user) {
        user.setUsername(user.getUsername().trim().replaceAll(" ", ""));
        String username = user.getUsername();

        if (username.length() > 35 || Objects.equals("", username)) {
            return "4";
        }
        if (userIsExist(user.getPhone())) {
            return "1";
        }
        if (Objects.equals("male", user.getGender())) {
            user.setAvatarImgUrl("");
        }
        return null;
    }

    @Override
    public int findUserIdByPhone(String phone) {
        return 0;
    }

    @Override
    public int updatePasswordByPhone(String phone, String password) {
        return 0;
    }

    @Override
    public String findPhoneByUsername(String username) {
        return null;
    }

    @Override
    public int findIdByUsername(String username) {
        return 0;
    }

    @Override
    public User findUsernameByPhone(String phone) {
        return null;
    }

    @Override
    public int updateRecentlyLanded(String username, String recentlyLanded) {
        return 0;
    }

    @Override
    public boolean usernameIsExist(String username) {
        return false;
    }

    @Override
    public boolean isSuperAdmin(String phone) {
        return false;
    }

    @Override
    public int updateAvatarImgUrlById(String avatarImgUrl, int id) {
        return 0;
    }

    @Override
    public JSONObject getUserPersonalInfoByUsername(String username) {
        return null;
    }

    @Override
    public JSONObject savePersonalData(User user, String username) {
        return null;
    }

    @Override
    public String getHeadPortraitUrlByUserId(int userId) {
        return null;
    }

    @Override
    public int countUserNum() {
        return 0;
    }

    /**
     * @description: 通过手机号判断用户是否存在
     * @author: 陈文振
     * @date: 2019/12/11
     * @param phone: 手机号
     * @return: true--存在  false--不存在
     */
    private boolean userIsExist(String phone) {
        Example userExample = new Example(User.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("phone", phone);
        User user = userMapper.selectOneByExample(userExample);
        return user != null;
    }
}
