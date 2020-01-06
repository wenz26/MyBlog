package com.cwz.blog.defaultblog.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @author: 陈文振
 * @date: 2019/12/9
 * @description: Hash类型redis操作
 */
@Service("hashRedisService")
public class HashRedisServiceImpl implements RedisService{

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * @description: 单个设置hash键值对
     * @author: 陈文振
     * @date: 2019/12/10
     * @param key: key参数是命名空间的意思
     * @param field: 相当于哈希表中的 key值
     * @param hv: 相当于哈希表中的对应 key的 value值
     * @return:
     */
    public void put(String key, Object field, Object hv) {
        HashOperations<String, Object, Object> vo = redisTemplate.opsForHash();
        vo.put(key, field, hv);
    }

    /**
     * @description: 通过实体类反射方式设置key和value
     * @author: 陈文振
     * @date: 2019/12/10
     * @param key: key值
     * @param value: 要设置的value值
     * @param entityClass: 要设置的value值的Class
     * @return:
     */
    public void put(String key, Object value, Class<?> entityClass){
        HashOperations<String, Object, Object> vo = redisTemplate.opsForHash();
        // 通过反射获得对象属性
        // 获得某个类的所有声明的字段，即包括public、private和proteced，但是不包括父类的申明字段。
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            // 当字段修饰符为private时，要加上这个设置才能获取到
            field.setAccessible(true);
            // 保存对象属性和值到对应key值下
            try {
                vo.put(key, field.getName(), field.get(value));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @description: 仅当存在field时才进行设置
     * @author: 陈文振
     * @date: 2019/12/10
     * @return: 不存在--true   存在--false
     */
    public Boolean hashPutIfAbsent(String key, Object hashKey, Object value) {
        //  如果传入key对应的value已经存在，就返回存在的value，不进行替换。如果不存在，就添加key和value，返回true
        return redisTemplate.opsForHash().putIfAbsent(key, hashKey, value);
    }

    /**
     * @description: 获得对应key值下的属性field的值
     * @author: 陈文振
     * @date: 2019/12/10
     * @param key: key值
     * @param field: 要获得值的属性
     * @return: 如果存在则返回值，否则返回null
     */
    public Object get(String key, Object field) {
        HashOperations<String, Object, Object> vo = redisTemplate.opsForHash();
        return vo.get(key, field);
    }

    /**
     * @description: 获得key的所有值
     * @author: 陈文振
     * @date: 2019/12/10
     * @return: 一个Map集合
     */
    public Object getAllFieldAndValue(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * @description: 通过实体类删除指定key下的所有字段
     * @author: 陈文振
     * @date: 2019/12/10
     * @return:
     */
    public void hashDelete(String key, Class<?> entityClass){
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            redisTemplate.opsForHash().delete(key, field.getName());
        }
    }

    /**
     * @description: 删除指定key下的指定field
     * @author: 陈文振
     * @date: 2019/12/10
     * @return:
     */
    public void  hashDelete(String key, Object field){
        redisTemplate.opsForHash().delete(key, field);
    }

    /**
     * @description: 删除指定key下的所有内容
     * @author: 陈文振
     * @date: 2020/1/2
     * @param key
     * @return: void
     */
    public void  deleteByKey(String key){
        redisTemplate.opsForHash().getOperations().delete(key);
    }

    /**
     * @description: key的指定字段的整数值加上增量increment
     * 如：获得的值为 200 可对他进行增量运算
     * @author: 陈文振
     * @date: 2019/12/10
     * @param key: key值
     * @param field: 要获得值的属性
     * @param increment: 增量
     * @return:
     */
    public Long hashIncrement(String key, Object field, long increment) {
        return redisTemplate.opsForHash().increment(key, field, increment);
    }

    /**
     * @description: 判断key下的field是否存在
     * @author: 陈文振
     * @date: 2019/12/10
     * @return:
     */
    public boolean hasHashKey(String key, Object field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    /**
     * @description: 判断key是否存在
     * @author: 陈文振
     * @date: 2019/12/10
     * @return:
     */
    @Override
    public Boolean hasKey(String key) {
        return redisTemplate.opsForHash().getOperations().hasKey(key);
    }
}
