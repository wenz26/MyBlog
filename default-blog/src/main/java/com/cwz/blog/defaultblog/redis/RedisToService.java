package com.cwz.blog.defaultblog.redis;

import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.entity.UserReadNews;
import com.cwz.blog.defaultblog.service.UserService;
import com.cwz.blog.defaultblog.utils.DataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

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

    @Autowired
    private UserService userService;

    /**
     * @description: 获得redis中用户的未读消息
     * @author: 陈文振
     * @date: 2019/12/23
     * @param username: 用户名
     * @return:
     */
    public DataMap getUserNews(String username) {
        JSONObject jsonObject = new JSONObject();

        int userId = userService.findIdByUsername(username);
        LinkedHashMap map = (LinkedHashMap) hashRedisService.getAllFieldAndValue(String.valueOf(userId));

        if (map.size() == 0) {
            jsonObject.put("result", 0);
        } else {
            int allNewsNum = (int) map.get("allNewsNum");
            int commentNum = (int) map.get("commentNum");
            UserReadNews news = new UserReadNews(allNewsNum, commentNum);
            jsonObject.put("result", news);
        }
        return DataMap.success().setData(jsonObject);
    }

    /**
     * @description: 已读一条消息时修改redis中的未读消息
     * @author: 陈文振
     * @date: 2019/12/23
     * @param userId: 用户id
     * @param msgType: 信息类型
     * @return:
     */
    public void readOneMsgOnRedis(int userId, int msgType) {
        hashRedisService.hashIncrement(String.valueOf(userId), "allNewsNum", -1);

        LinkedHashMap map = (LinkedHashMap) hashRedisService.getAllFieldAndValue(String.valueOf(userId));
        int allNewsNum = (int) map.get("allNewsNum");

        // 如果总留言评论数为0则删除该key
        if (allNewsNum == 0) {
            hashRedisService.hashDelete(String.valueOf(userId), UserReadNews.class);
        } else if (msgType == 1) {
            hashRedisService.hashIncrement(String.valueOf(userId), "commentNum", -1);
        }
    }

    /**
     * @description: 已读所有消息时修改redis中的未读消息
     * @author: 陈文振
     * @date: 2019/12/23
     * @param userId: 用户id
     * @param msgType: 信息类型
     * @return:
     */
    public void readAllMsgOnRedis(int userId, int msgType) {
        LinkedHashMap map = (LinkedHashMap) hashRedisService.getAllFieldAndValue(String.valueOf(userId));

        int allNewsNum = (int) map.get("allNewsNum");
        int commentNum = (int) map.get("commentNum");

        if (allNewsNum == 0) {
            hashRedisService.hashDelete(String.valueOf(userId), UserReadNews.class);
        } else if (msgType == 1) {
            hashRedisService.hashIncrement(String.valueOf(userId), "allNewsNum", -commentNum);
            hashRedisService.hashIncrement(String.valueOf(userId), "commentNum", -commentNum);
        }
    }

    /**
     * @description: 修改redis中的点赞未读量
     * @author: 陈文振
     * @date: 2019/12/23
     * @param key: 键值
     * @param increment: 增量
     * @return:
     */
    public void readThumbsUpRecordOnRedis(String key, String filed, int increment) {
        boolean thumbsUpNotReadIsExist = hashRedisService.hasKey(key);
        if (!thumbsUpNotReadIsExist) {
            hashRedisService.put(key, filed, 1);
        } else {
            hashRedisService.hashIncrement(key, filed, increment);
        }
    }

    /**
     * @description: 增加redis中的访客量
     * @author: 陈文振
     * @date: 2019/12/23
     * @param key: 键值
     * @param field: 记录名称（变量名）
     * @param increment: 增量
     * @return:
     */
    public Long addVisitorNumOnRedis(String key, Object field, long increment){
        boolean fieldIsExist = hashRedisService.hasHashKey(key, field);
        if (fieldIsExist) {
            return hashRedisService.hashIncrement(key, field, increment);
        }
        return null;
    }

    /**
     * @description: 向redis中保存访客量
     * @author: 陈文振
     * @date: 2019/12/23
     * @param key: 键值
     * @param field: 记录名称（变量名）
     * @param value: 要保存的值
     * @return:
     */
    public Long putVisitorNumOnRedis(String key, Object field, Object value){
        hashRedisService.put(key, field, value);
        return Long.valueOf(hashRedisService.get(key, field).toString());
    }

    /**
     * @description: 获得redis中的访客记录
     * @author: 陈文振
     * @date: 2019/12/23
     * @param key: 键值
     * @param field: 记录名称（变量名）
     * @return:
     */
    public Long getVisitorNumOnRedis(String key, Object field){
        boolean fieldIsExist = hashRedisService.hasHashKey(key, field);
        if (fieldIsExist) {
            return Long.valueOf(hashRedisService.get(key, field).toString());
        }
        return null;
    }

}
