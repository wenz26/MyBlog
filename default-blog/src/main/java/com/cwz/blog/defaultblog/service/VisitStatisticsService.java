package com.cwz.blog.defaultblog.service;

import com.cwz.blog.defaultblog.utils.DataMap;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: 陈文振
 * @date: 2019/12/22
 * @description: 访问量统计业务操作
 */
public interface VisitStatisticsService {

    /**
     * @description: 通过 具体需要统计东西的名称（包括文章访问量、用户数、总访问量等等） 增加 访问量统计数
     * @author: 陈文振
     * @date: 2019/12/22
     * @param statisticsName: 具体需要统计东西的名称
     * @param request: request请求
     * @return:
     */
    DataMap addVisitNumByStatisticsName(String statisticsName, HttpServletRequest request);

    /**
     * @description: 通过 具体需要统计东西的名称（包括文章访问量、用户数、总访问量等等） 获得 访问量统计数
     * @author: 陈文振
     * @date: 2019/12/23
     * @param statisticsName: 具体需要统计东西的名称
     * @return:
     */
    long getNumByStatisticsName(String statisticsName);

    /**
     * @description: 发布文章后保存该文章的访客量
     * @author: 陈文振
     * @date: 2019/12/23
     * @param statisticsName: 文章url
     * @return:
     */
    void insertVisitArticlePage(String statisticsName);

    /**
     * @description: 获得总访问量
     * @author: 陈文振
     * @date: 2019/12/23
     * @return:
     */
    long getTotalVisit();

    /**
     * @description: 通过 具体需要统计东西的名称 更新 访客人数
     * @author: 陈文振
     * @date: 2019/12/23
     * @param statisticsName: 具体需要统计东西的名称
     * @param visitNum: 访客人数
     * @return:
     */
    void updateVisitNumByStatisticsName(String statisticsName, long visitNum);
}
