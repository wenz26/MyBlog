package com.cwz.blog.defaultblog.service;

import com.cwz.blog.defaultblog.utils.DataMap;

/**
 * @author: 陈文振
 * @date: 2020/2/5
 * @description: 用户关注业务操作
 */
public interface UserAttentionService {

    /**
     * @description: 判断是否已经关注
     * @author: 陈文振
     * @date: 2020/2/6
     * @param personId: 被关注用户id
     * @param userId: 浏览页面的用户id
     * @return: boolean
     */
    boolean isAttention(int personId, int userId);

    /**
     * @description: 插入关注
     * @author: 陈文振
     * @date: 2020/2/5
     * @param username: 关注者
     * @param attentionId: 被关注者
     * @return: com.cwz.blog.defaultblog.utils.DataMap
     */
    DataMap insertUserAttention(String username, int attentionId);

    /**
     * @description: 取消关注
     * @author: 陈文振
     * @date: 2020/2/5
     * @return: com.cwz.blog.defaultblog.utils.DataMap
     */
    DataMap deleteUserAttention(int attentionId);

    /**
     * @description: 获得我的关注 或 我的粉丝
     * @author: 陈文振
     * @date: 2020/2/6
     * @param username
     * @param type: 1 为获得我的关注， 2 为获得我的粉丝
     * @param inquireName: 要查询的名字
     * @return: com.cwz.blog.defaultblog.utils.DataMap
     */
    DataMap getUserUserAttention(String username, String inquireName, int type, int rows, int pageNum);

    /**
     * @description: 获得关注 或 粉丝
     * @author: 陈文振
     * @date: 2020/2/8
     * @param username
     * @param myName
     * @param type
     * @param rows
     * @param pageNum
     * @return: com.cwz.blog.defaultblog.utils.DataMap
     */
    DataMap getSomeOneAttention(String username, String myName, int type, int rows, int pageNum);
}
