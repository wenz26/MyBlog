package com.cwz.blog.defaultblog.service_test;

import com.cwz.blog.defaultblog.entity.User;
import com.cwz.blog.defaultblog.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void userTest(){
        User userByPhone = userService.findUserByPhone("13532830478");
        String usernameById = userService.findUsernameById(1);
        System.out.println(userByPhone.toString());
        System.out.println(usernameById);
    }
}
