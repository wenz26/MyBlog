package com.cwz.blog.defaultblog.service_test;

import com.alibaba.fastjson.JSON;
import com.cwz.blog.defaultblog.entity.User;
import com.cwz.blog.defaultblog.mapper.UserMapper;
import com.cwz.blog.defaultblog.service.UserService;
import com.cwz.blog.defaultblog.utils.DataMap;
import com.cwz.blog.defaultblog.utils.TimeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    @Test
    void userTest(){

        /*User user = new User();
        user.setPhone("13532830478");
        user.setUsername("陈文振");
        user.setPassword("123");
        user.setGender("male");
        user.setTrueName("陈文振");
        user.setBirthday(TimeUtil.getParseDateForThree("1999-06-14"));
        user.setEmail("948003247@qq.com");
        userService.insert(user);*/

        User user = new User();
        user.setPhone("13431287411");
        user.setUsername("李莉");
        user.setPassword("123");
        user.setGender("female");
        user.setTrueName("李莉");
        user.setBirthday(TimeUtil.getParseDateForThree("1999-06-14"));
        user.setEmail("842189278@qq.com");
        userService.insert(user);
    }

    @Test
    void parseTest(){
        LocalDate localDate = LocalDate.now();
        System.out.println(localDate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parse = LocalDate.parse("2019-10-10", formatter);
        System.out.println(parse);
    }

    @Test
    void getUserInfo() {
        //User user = userService.findUserByPhone("13532830478");
        //User user = userService.findUserByUserId(1);
        /*User user = userService.findUserByUsername("陈文振");
        System.out.println(user.getBirthday());
        System.out.println(JSON.toJSONString(user));*/

        /*LocalDateTime localDateTime = LocalDateTime.now();
        User user = new User();
        user.setRecentlyLanded(localDateTime);
        userService.updatePasswordByPhone("13532830478", user);*/

        /*boolean superAdmin = userService.isSuperAdmin("13532830478");
        System.out.println(superAdmin);*/

        User user = new User();
        user.setUsername("陈文振");
        user.setEmail("948003247@qq.com");
        DataMap dataMap = userService.savePersonalData(user, "cwz");
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void getUserAndRoles(){
        User user = userMapper.getUsernameAndRolesByPhone("13532830478");
        System.out.println(JSON.toJSONString(user));
    }

}
