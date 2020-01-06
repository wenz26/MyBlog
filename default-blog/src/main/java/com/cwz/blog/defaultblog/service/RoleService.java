package com.cwz.blog.defaultblog.service;

import com.cwz.blog.defaultblog.entity.Role;
import com.cwz.blog.defaultblog.utils.DataMap;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: 陈文振
 * @date: 2019/12/26
 * @description: 角色业务操作
 */
public interface RoleService {

    /**
     * @description: 获得所有的角色以及该角色的用户总数和用户名
     * @author: 陈文振
     * @date: 2019/12/26
     * @param
     * @return: com.cwz.blog.defaultblog.utils.DataMap
     */
    DataMap findAllRoleAndUserNameAndUserNum();

    /**
     * @description: 获得角色数目
     * @author: 陈文振
     * @date: 2019/12/24
     * @return:
     */
    int countRoleNum();

    /**
     * @description: 通过角色名查看角色是否存在
     * @author: 陈文振
     * @date: 2019/12/24
     * @param roleName: 角色名
     * @return:
     */
    Role roleIsExistByRoleName(String roleName);

    /**
     * @description: 更新 角色
     * @author: 陈文振
     * @date: 2019/12/18
     * @param role: 角色主类
     * @param type: 1--增加角色(系统默认只支持3种角色（不过我就固定是3个角色不能去添加）)
     *              2--删除角色      3--修改角色（角色名不能修改，只能修改角色具体描述）
     * @return:
     */
    @Transactional
    DataMap updateRole(Role role, int type);
}
