package com.cwz.blog.defaultblog.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: 陈文振
 * @date: 2019/12/10
 * @description: redis业务逻辑
 */
@Service("redisToService")
public class RedisToService {

    @Autowired
    private StringRedisServiceImpl stringRedisService;

    @Autowired
    private HashRedisServiceImpl hashRedisService;


}
