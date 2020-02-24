package com.cwz.blog.defaultblog.service;

import com.cwz.blog.defaultblog.utils.DataMap;

/**
 * @author: 陈文振
 * @date: 2020/2/6
 * @description: 公用操作业务操作
 */
public interface AllService {

    /**
     * @description: 根据关键字来搜索相应内容
     * @author: 陈文振
     * @date: 2020/2/6
     * @param keyWords
     * @param type: 1 为文章搜索，2 为用户搜索，3 为标签搜索，4 为分类搜索
     * @return: com.cwz.blog.defaultblog.utils.DataMap
     */
    DataMap searchByKeyWords(String keyWords, String timeRange, Integer userId, int type, int rows, int pageNum);

    /**
     * @description: 根据用户Id来查找用户最近的10个搜索历史
     * @author: 陈文振
     * @date: 2020/2/7
     * @param userId
     * @return: com.cwz.blog.defaultblog.utils.DataMap
     */
    DataMap searchHistory(int userId);

    /**
     * @description: 删除某条历史记录
     * @author: 陈文振
     * @date: 2020/2/8
     * @param userId
     * @param key
     * @return: com.cwz.blog.defaultblog.utils.DataMap
     */
    DataMap deleteOneHistory(int userId, String key);

    /**
     * @description: 删除全部历史记录
     * @author: 陈文振
     * @date: 2020/2/8
     * @param userId
     * @return: com.cwz.blog.defaultblog.utils.DataMap
     */
    DataMap deleteAllHistory(int userId);
}
