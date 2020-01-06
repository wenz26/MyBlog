package com.cwz.blog.defaultblog.service_test;

import com.alibaba.fastjson.JSON;
import com.cwz.blog.defaultblog.entity.Role;
import com.cwz.blog.defaultblog.service.RoleService;
import com.cwz.blog.defaultblog.utils.DataMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RoleServiceTest {

    @Autowired
    private RoleService roleService;

    @Test
    void findAllRoleAndUserNameAndUserNum() {
        DataMap dataMap = roleService.findAllRoleAndUserNameAndUserNum();
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void countRoleNum() {
        int i = roleService.countRoleNum();
        System.out.println(i);
    }

    @Test
    void roleIsExistByRoleName() {
    }

    @Test
    void updateRole() {
        Role role = new Role();
        role.setRoleName("ROLE_USERa");
        role.setDescription("这是个测试test");

        DataMap dataMap = roleService.updateRole(role, 2);
        System.out.println(JSON.toJSONString(dataMap));
    }
}
