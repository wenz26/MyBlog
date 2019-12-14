package com.cwz.blog.defaultblog.redis;

/**
 * @author: 陈文振
 * @date: 2019/12/9
 * @description: redis 业务处理
 */
public interface RedisService {

    /**
     * @description: 判断key是否存在
     * @author: 陈文振
     * @date: 2019/12/9
     * @param key: 哈希中的key值
     * @return:
     */
    Boolean hasKey(String key);
}
