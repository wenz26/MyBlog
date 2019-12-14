package com.cwz.blog.defaultblog.service;

import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.entity.Archives;

/**
 * @author: 陈文振
 * @date: 2019/12/3
 * @description: 归档业务操作
 */
public interface ArchivesService {

    /**
     * @description: 获得归档信息（包括归档日期和该归档日期文章的数目）
     * @author: 陈文振
     * @date: 2019/12/3
     * @return: JSONObject
     */
    JSONObject findArchiveNameAndArticleNum();

    /**
     * @description: 添加归档日期
     * @author: 陈文振
     * @date: 2019/12/3
     * @param archives:
     * @return:
     */
    int addArchives(Archives archives);
}
