package com.cwz.blog.defaultblog.service;

import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.entity.ArticleLikesRecord;

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
    boolean isLike(long articleId, String username);

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
    int deleteArticleLikesRecordByArticleId(long articleId);

    /**
     * @description: 获得文章点赞信息
     * @author: 陈文振
     * @date: 2019/12/9
     * @param username: 点赞人
     * @param rows: 一页显示文章数
     * @param pageNum: 第几页
     * @return:
     */
    JSONObject getArticleThumbsUp(String username, int rows, int pageNum);

    /**
     * @description: 已读一条点赞记录
     * @author: 陈文振
     * @date: 2019/12/9
     * @param id:
     * @return:
     */
    int readThisThumbsUp(int id);

    /**
     * @description: 已读所有点赞记录
     * @author: 陈文振
     * @date: 2019/12/9
     * @return:
     */
    JSONObject readAllThumbsUp();
}
