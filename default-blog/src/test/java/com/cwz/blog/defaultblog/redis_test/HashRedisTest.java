package com.cwz.blog.defaultblog.redis_test;

import com.alibaba.fastjson.JSON;
import com.cwz.blog.defaultblog.redis.HashRedisServiceImpl;
import com.cwz.blog.defaultblog.security.code.sms.SmsCode;
import com.cwz.blog.defaultblog.utils.StringUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;


@SpringBootTest
class HashRedisTest {

    @Autowired
    private HashRedisServiceImpl hashRedisService;

    @Test
    void redisTest() {
        LinkedHashMap map = (LinkedHashMap) hashRedisService.getAllFieldAndValue("visit");
        System.out.println(JSON.toJSONString(map));
        String statisticsName;
        for (Object e : map.keySet()) {
            statisticsName = String.valueOf(e);
            System.out.println(statisticsName);
            System.out.println(map.get(statisticsName));
            System.out.println(map.get(statisticsName).getClass());
        }
    }

    @Test
    void codeRedisTest(){
        SmsCode code = (SmsCode) hashRedisService.get(StringUtil.PREFIX_IMAGE_CODE, "2020-01-03T23:45:30.996");
        System.out.println(code);
        System.out.println(code.isExpired());
    }
}
