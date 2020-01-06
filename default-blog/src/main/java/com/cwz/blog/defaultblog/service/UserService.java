package com.cwz.blog.defaultblog.service;

import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.entity.User;
import com.cwz.blog.defaultblog.utils.DataMap;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author: 陈文振
 * @date: 2019/12/11
 * @description: user业务操作
 */
public interface UserService {

    /**
     * @description: 通过手机号查找注册用户
     * @author: 陈文振
     * @date: 2019/12/11
     * @param phone: 手机号
     * @return: 用户
     */
    User findUserByPhone(String phone);

    /**
     * @description: 通过用户名查找用户
     * @author: 陈文振
     * @date: 2019/12/18
     * @param username: 用户名
     * @return:
     */
    User findUserByUsername(String username);

    /**
     * @description: 通过用户Id查找用户
     * @author: 陈文振
     * @date: 2019/12/18
     * @param userId: 用户Id
     * @return:
     */
    User findUserByUserId(int userId);

    /**
     * @description: 通过id查找用户名
     * @author: 陈文振
     * @date: 2019/12/11
     * @param id:
     * @return:
     */
    String findUsernameById(int id);

    /**
     * @description: 注册用户
     * @author: 陈文振
     * @date: 2019/12/11
     * @param user: 用户
     * @return: "1"--用户存在，插入失败，"2"--用户不存在，插入成功，"3"--用户名太长，插入失败
     */
    @Transactional
    DataMap insert(User user);

    /**
     * @description: 通过手机号查找用户id
     * @author: 陈文振
     * @date: 2019/12/11
     * @param phone: 手机号
     * @return: 用户id
     */
    int findUserIdByPhone(String phone);

    /**
     * @description: 通过手机号修改密码
     * @author: 陈文振
     * @date: 2019/12/11
     * @param phone: 手机号
     * @param password: 密码
     * @return:
     */
    int updatePasswordByPhone(String phone, String password);

    /**
     * @description: 通过用户名获得手机号
     * @author: 陈文振
     * @date: 2019/12/11
     * @param username: 用户名
     * @return: 手机号
     */
    String findPhoneByUsername(String username);

    /**
     * @description: 通过用户名查找id
     * @author: 陈文振
     * @date: 2019/12/11
     * @param username: 用户名
     * @return: 用户id
     */
    int findIdByUsername(String username);

    /**
     * @description: 通过手机号查找用户名
     * @author: 陈文振
     * @date: 2019/12/11
     * @param phone: 手机号
     * @return: 用户名
     */
    String findUsernameByPhone(String phone);

    /**
     * @description: 更新最近登录时间
     * @author: 陈文振
     * @param username: 用户名
     * @param recentlyLanded: 最近登录时间
     * @return:
     */
    int updateRecentlyLanded(String username, LocalDateTime recentlyLanded);

    /**
     * @description: 判断用户名是否存在
     * @author: 陈文振
     * @date: 2019/12/11
     * @param username: 用户名
     * @return: true--存在  false--不存在
     */
    boolean usernameIsExist(String username);

    /**
     * @description: 通过手机号判断是否为普通用户（有权限写博客）
     * @author: 陈文振
     * @date: 2019/12/11
     * @param phone: 手机号
     * @return: true--普通用户  false--非普通用户
     */
    boolean isRoleUser(String phone);

    /**
     * @description: 更改头像
     * @author: 陈文振
     * @date: 2019/12/11
     * @param avatarImgUrl: 头像地址
     * @return:
     */
    @Transactional
    int updateAvatarImgUrlById(String avatarImgUrl, int id);

    /**
     * @description: 获得头像url
     * @author: 陈文振
     * @date: 2019/12/17
     * @return:
     */
    DataMap getHeadPortraitUrl(int id);

    /**
     * @description: 获得用户个人信息
     * @author: 陈文振
     * @date: 2019/12/11
     * @param username: 用户名
     * @return:
     */
    DataMap getUserPersonalInfoByUsername(String username);

    /**
     * @description: 保存用户个人信息
     * @author: 陈文振
     * @date: 2019/12/11
     * @param user: 个人信息
     * @param username: 当前更改的用户
     * @return:
     */
    DataMap savePersonalData(User user, String username);

    /**
     * @description: 获得用户头像的地址
     * @author: 陈文振
     * @date: 2019/12/11
     * @param userId: 用户id
     * @return: 头像的url
     */
    String getHeadPortraitUrlByUserId(int userId);

    /**
     * @description: 统计总用户量
     * @author: 陈文振
     * @date: 2019/12/11
     * @return:
     */
    int countUserNum();
}
