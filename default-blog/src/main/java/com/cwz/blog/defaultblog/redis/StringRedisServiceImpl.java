package com.cwz.blog.defaultblog.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

/**
 * @author: 陈文振
 * @date: 2019/12/10
 * @description: String类型redis操作
 */
@Service("stringRedisService")
public class StringRedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * @description: 设置key和value，超时时间为-1
     * @author: 陈文振
     * @date: 2019/12/10
     * @return:
     */
    public void set(String key, Object value) {
        ValueOperations<String, Object> vo = redisTemplate.opsForValue();
        vo.set(key, value);
    }

    /**
     * @description: 设置带有超时时间(秒)的key和value
     * @author: 陈文振
     * @date: 2019/12/10
     * @param timeout: 超时时间
     * @return:
     */
    public void set(String key, Object value, long timeout) {
        ValueOperations<String, Object> vo = redisTemplate.opsForValue();
        vo.set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * @description: 获得key的值
     * @author: 陈文振
     * @date: 2019/12/10
     * @return:
     */
    public Object get(String key) {
        ValueOperations<String, Object> vo = redisTemplate.opsForValue();
        return vo.get(key);
    }

    /**
     * @description: 获得key超时时间
     * @author: 陈文振
     * @date: 2019/12/10
     * @return: 超时时间
     */
    public long getExpire(String key){
        return redisTemplate.getExpire(key);
    }

    /**
     * @description: 设置key的超时时间(秒)
     * @author: 陈文振
     * @date: 2019/12/10
     * @return:
     */
    public Boolean expire(String key, long timeout) {
        return redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * @description: 删除key-value
     * @author: 陈文振
     * @date: 2019/12/10
     * @return:
     */
    public Boolean remove(String key){
        return redisTemplate.delete(key);
    }

    /**
     * @description: key的指定字段的整数值加上增量increment
     * @author: 陈文振
     * @date: 2019/12/10
     * @return:
     */
    public Long stringIncrement(String key, long increment){
        return redisTemplate.opsForValue().increment(key, increment);
    }

    /**
     * @description: 判断key是否存在
     * @author: 陈文振
     * @date: 2019/12/10
     * @return:
     */
    @Override
    public Boolean hasKey(String key) {
        return redisTemplate.opsForValue().getOperations().hasKey(key);
    }
}
