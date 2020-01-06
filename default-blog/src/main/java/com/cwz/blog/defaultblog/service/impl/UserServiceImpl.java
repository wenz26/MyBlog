package com.cwz.blog.defaultblog.service.impl;

import com.cwz.blog.defaultblog.constant.CodeType;
import com.cwz.blog.defaultblog.constant.RoleConstant;
import com.cwz.blog.defaultblog.entity.User;
import com.cwz.blog.defaultblog.mapper.UserMapper;
import com.cwz.blog.defaultblog.service.UserService;
import com.cwz.blog.defaultblog.utils.DataMap;
import com.cwz.blog.defaultblog.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.List;
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
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findUserByPhone(String phone) {
        Example userExample = new Example(User.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("phone", phone);
        return userMapper.selectOneByExample(userExample);
    }

    @Override
    public User findUserByUsername(String username) {
        Example userExample = new Example(User.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("username", username);
        return userMapper.selectOneByExample(userExample);
    }

    @Override
    public User findUserByUserId(int userId) {
        Example userExample = new Example(User.class);
        /*userExample.selectProperties("username", "avatarImgUrl");*/
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("id", userId);
        return userMapper.selectOneByExample(userExample);
    }


    @Override
    public String findUsernameById(int id) {
        return findUserByUserId(id).getUsername();
    }

    @Override
    public DataMap insert(User user) {
        user.setUsername(user.getUsername().trim().replaceAll(" ", StringUtil.BLANK));
        String username = user.getUsername();

        if (username.length() > StringUtil.USERNAME_MAX_LENGTH || Objects.equals(StringUtil.BLANK, username)) {
            return DataMap.fail(CodeType.USERNAME_FORMAT_ERROR);
        }
        if (userIsExist(user.getPhone())) {
            return DataMap.fail(CodeType.PHONE_EXIST);
        }
        if (Objects.equals("male", user.getGender())) {
            user.setAvatarImgUrl("https://oss.czodly.top/blog/image/user/noLogin_male.jpg?x-oss-process=style/default");
        } else {
            user.setAvatarImgUrl("https://oss.czodly.top/blog/image/user/noLogin_female.jpg?x-oss-process=style/default");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insert(user);

        // 获得插入后的userId
        Integer userId = user.getId();
        insertRole(userId, RoleConstant.ROLE_USER);
        return DataMap.success();
    }

    @Override
    public int findUserIdByPhone(String phone) {
        return findUserByPhone(phone).getId();
    }

    @Override
    public int updatePasswordByPhone(String phone, String password) {
        int updateInt = userMapper.updatePassword(phone, password);
        // 密码修改成功后注销当前用户
        SecurityContextHolder.getContext().setAuthentication(null);
        return updateInt;
    }

    @Override
    public String findPhoneByUsername(String username) {
        return findUserByUsername(username).getPhone();
    }

    @Override
    public int findIdByUsername(String username) {
        return findUserByUsername(username).getId();
    }

    @Override
    public String findUsernameByPhone(String phone) {
        return findUserByPhone(phone).getUsername();
    }

    @Override
    public int updateRecentlyLanded(String username, LocalDateTime recentlyLanded) {
        return userMapper.updateRecentlyLanded(username, recentlyLanded);
    }

    @Override
    public boolean usernameIsExist(String username) {
        User user = findUserByUsername(username);
        return user != null;
    }

    @Override
    public boolean isRoleUser(String phone) {
        int userId = findUserIdByPhone(phone);

        List<Object> roleIds = userMapper.findRoleIdByUserId(userId);
        for (Object roleId : roleIds) {
            if ((Integer) roleId == 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int updateAvatarImgUrlById(String avatarImgUrl, int id) {
        return userMapper.updateAvatarImgUrlById(avatarImgUrl, id);
    }

    @Override
    public DataMap getHeadPortraitUrl(int id) {
        return DataMap.success().setData(findUserByUserId(id).getAvatarImgUrl());
    }

    @Override
    public DataMap getUserPersonalInfoByUsername(String username) {
        return DataMap.success().setData(findUserByUsername(username));
    }

    @Override
    public DataMap savePersonalData(User user, String username) {
        user.setUsername(user.getUsername().trim().replaceAll(" ", StringUtil.BLANK));
        String newName = user.getUsername();
        if (newName.length() > StringUtil.USERNAME_MAX_LENGTH) {
            return DataMap.fail(CodeType.USERNAME_TOO_LANG);
        } else if (Objects.equals(newName, StringUtil.BLANK)) {
            return DataMap.fail(CodeType.USERNAME_BLANK);
        }

        int status;
        //改了昵称 （newName是user里修改的，username是原本存在Principal里的）
        if (!Objects.equals(newName, username)) {
            if (usernameIsExist(newName)) {
                return DataMap.fail(CodeType.USERNAME_EXIST);
            }
            status = CodeType.HAS_MODIFY_USERNAME.getCode();
            // 注销当前登录用户
            SecurityContextHolder.getContext().setAuthentication(null);
        } else {
            // 没改昵称
            status = CodeType.NOT_MODIFY_USERNAME.getCode();
        }

        Example example = new Example(User.class);
        example.createCriteria().andEqualTo("username", username);
        userMapper.updateByExampleSelective(user, example);
        return DataMap.success(status);
    }

    @Override
    public String getHeadPortraitUrlByUserId(int userId) {
        return findUserByUserId(userId).getAvatarImgUrl();
    }

    @Override
    public int countUserNum() {
        return userMapper.selectCount(null);
    }

    /**
     * @description: 增加用户权限
     * @author: 陈文振
     * @date: 2019/12/18
     * @param userId: 用户id
     * @param roleId: 权限id
     * @return:
     */
    private void insertRole(int userId, int roleId) {
        userMapper.saveRole(userId, roleId);
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
