package com.cwz.blog.defaultblog.service;

import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.entity.ArticleLikesRecord;
import com.cwz.blog.defaultblog.utils.DataMap;

/**
 * @author: 陈文振
 * @date: 2019/12/9
 * @description: 文章点赞记录业务操作
 */
public interface ArticleLikesRecordService {

    /**
     * @description: 文章是否已经点过赞
     * @author: 陈文振
     * @date: 2019/12/9
     * @param articleId: 文章id
     * @param username: 点赞人
     * @return: true--已经点过赞  false--没有点赞
     */
    boolean isLike(Integer articleId, String username);

    /**
     * @description: 保存文章中点赞的记录
     * @author: 陈文振
     * @date: 2019/12/9
     * @param articleLikesRecord: 文章中点赞的记录
     * @return:
     */
    int insertArticleLikesRecord(ArticleLikesRecord articleLikesRecord);

    /**
     * @description: 通过文章id删除文章点赞记录
     * @author: 陈文振
     * @date: 2019/12/9
     * @param articleId: 文章id
     * @return:
     */
    int deleteArticleLikesRecordByArticleId(Integer articleId);

    /**
     * @description: 获得该用户的所有文章点赞信息
     * @author: 陈文振
     * @date: 2019/12/9
     * @param rows: 一页显示文章数
     * @param pageNum: 第几页
     * @param username: 用户名
     * @return:
     */
    DataMap getArticleThumbsUp(int rows, int pageNum, String username);

    /**
     * @description: 已读一条点赞记录
     * @author: 陈文振
     * @date: 2019/12/9
     * @param id:
     * @return:
     */
    DataMap readThisThumbsUp(int id);

    /**
     * @description: 已读所有点赞记录
     * @author: 陈文振
     * @date: 2019/12/9
     * @return:
     */
    DataMap readAllThumbsUp(String username);
}
