package com.cwz.blog.defaultblog.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.constant.CodeType;
import com.cwz.blog.defaultblog.entity.Role;
import com.cwz.blog.defaultblog.mapper.RoleMapper;
import com.cwz.blog.defaultblog.service.RoleService;
import com.cwz.blog.defaultblog.service.UserService;
import com.cwz.blog.defaultblog.utils.DataMap;
import com.cwz.blog.defaultblog.utils.StringUtil;
import com.cwz.blog.defaultblog.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author: 陈文振
 * @date: 2019/12/26
 * @description: 角色业务操作实现类
 */
@Service("roleService")
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public DataMap findAllRoleAndUserNameAndUserNum() {
        List<Role> roles = roleMapper.selectAll();

        JSONObject roleJson;
        JSONArray roleJsonArray = new JSONArray();
        JSONObject returnJson = new JSONObject();

        for (Role role : roles) {
            roleJson =  new JSONObject();

            roleJson.put("roleName", role.getRoleName());
            roleJson.put("createDate", TimeUtil.getFormatDateForSix(role.getCreateDate()));
            roleJson.put("description", role.getDescription());
            roleJson.put("userNum", roleMapper.countUserIdByRoleId(role.getId()));

            List<Object> userIds = roleMapper.findUserIdByRoleId(role.getId());
            String[] usernames = new String[userIds.size()];
            for (int i = 0; i < userIds.size(); i++) {
                usernames[i] = userService.findUsernameById((Integer) userIds.get(i));
            }
            roleJson.put("usernames", usernames);
            roleJsonArray.add(roleJson);
        }
        returnJson.put("msg", "获得所有的角色以及该角色的用户总数和用户名");
        returnJson.put("result", roleJsonArray);
        return DataMap.success().setData(returnJson);
    }

    @Override
    public int countRoleNum() {
        return roleMapper.selectCount(null);
    }

    @Override
    public Role roleIsExistByRoleName(String roleName) {
        Example example = new Example(Role.class);
        example.createCriteria().andEqualTo("roleName", roleName);
        return roleMapper.selectOneByExample(example);
    }

    @Override
    public DataMap updateRole(Role role, int type) {
        role.setRoleName(role.getRoleName().trim());

        if (role.getRoleName().length() > StringUtil.ROLE_NANE_MAX_LENGTH ||
                Objects.equals(role.getRoleName(), StringUtil.BLANK)) {
            return DataMap.fail(CodeType.ROLE_FORMAT_ERROR);
        }

        Role roleIsExist = roleIsExistByRoleName(role.getRoleName());
        LocalDateTime localDateTime = LocalDateTime.now();
        logger.info("角色库中是否存在该角色（大于等于1为存在，0为不存在）：" + JSON.toJSONString(roleIsExist));

        Example example = new Example(Role.class);
        example.createCriteria().andEqualTo("roleName", role.getRoleName());

        if (type == 1) {
            if (roleIsExist == null) {
                role.setCreateDate(localDateTime);
                roleMapper.insert(role);
                // 把插入的角色 id返回
                return DataMap.success(CodeType.ADD_ROLE_SUCCESS).setData(role.getId());
            } else {
                return DataMap.success(CodeType.ROLE_EXIST).setData(roleIsExist.getId());
            }
        } else if (type == 2) {
            if (roleIsExist != null) {
                // 找出 角色 对应的id
                int userNum = roleMapper.countUserIdByRoleId(roleIsExist.getId());
                if (userNum > 0) {
                    return DataMap.fail(CodeType.ROLE_HAS_USER);
                }
                roleMapper.deleteByExample(example);
                return DataMap.success(CodeType.DELETE_ROLE_SUCCESS);

            } else {
                return DataMap.fail(CodeType.ROLE_NOT_EXIST);
            }
        } else if (type == 3) {
            if (roleIsExist != null) {
                roleMapper.updateByExampleSelective(role, example);
                return DataMap.success(CodeType.UPDATE_ROLE_SUCCESS);
            } else {
                return DataMap.fail(CodeType.ROLE_NOT_EXIST);
            }
        }
        return null;
    }
}
