package com.cwz.blog.defaultblog.redis_test;

import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class RedisTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void redisKeyValueTest(){
        redisTemplate.opsForValue().set("aaa", 123);
        redisTemplate.opsForValue().set("真伪在", "452");
        redisTemplate.opsForValue().set("156","陈文振");
        User user = new User();
        user.setAvatarImgUrl("asdfddsff");
        user.setUsername("陈文振");
        user.setPassword("156232213");
        user.setId(5644546);
        user.setPhone("1561321321");
        redisTemplate.opsForValue().set("user01", user);
        System.out.println(redisTemplate.opsForValue().get("user01"));

        User user1 = (User) redisTemplate.opsForValue().get("user01");
        System.out.println(user1);
        System.out.println(JSONObject.toJSONString(user1));
    }
}
