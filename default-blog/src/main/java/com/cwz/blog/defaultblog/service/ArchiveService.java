package com.cwz.blog.defaultblog.service;

import com.cwz.blog.defaultblog.utils.DataMap;

/**
 * @author: 陈文振
 * @date: 2019/12/16
 * @description: 归档业务操作（主要获得文章的日期）
 */
public interface ArchiveService {

    /**
     * @description: 获得该用户所有文章的发布日期
     * @author: 陈文振
     * @date: 2019/12/16
     * @return:
     */
    DataMap findPublishDateToArticle(String username);
}
