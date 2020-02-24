package com.cwz.blog.defaultblog.service.impl;

import com.alibaba.fastjson.JSON;
import com.cwz.blog.defaultblog.service.UserAttentionService;
import com.cwz.blog.defaultblog.utils.DataMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserAttentionServiceImplTest {
    @Autowired
    private UserAttentionService userAttentionService;

    @Test
    void isAttention() {
    }

    @Test
    void insertUserAttention() {
    }

    @Test
    void deleteUserAttention() {
    }

    @Test
    void getUserUserAttention() {
        DataMap dataMap = userAttentionService.getUserUserAttention("陈文振", null, 1,
                8, 1);
        System.out.println(JSON.toJSONString(dataMap));
    }
}
