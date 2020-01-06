package com.cwz.blog.defaultblog.service;

import com.cwz.blog.defaultblog.entity.ArticleUserFavoriteRecord;
import com.cwz.blog.defaultblog.utils.DataMap;

/**
 * @author: 陈文振
 * @date: 2019/12/22
 * @description: 用户文章收藏业务操作
 */
public interface ArticleUserFavoriteRecordService {

    /**
     * @description: 评定文章用户是否已经收藏过
     * @author: 陈文振
     * @date: 2019/12/22
     * @param articleId: 文章Id
     * @param username: 用户名
     * @return: true--已经收藏过false--没有收藏
     */
    boolean isFavorite(int articleId, String username);

    /**
     * @description: 保存用户收藏的记录
     * @author: 陈文振
     * @date: 2019/12/22
     * @param articleUserFavoriteRecord: 收藏类
     * @return:
     */
    DataMap insertArticleFavoriteRecord(ArticleUserFavoriteRecord articleUserFavoriteRecord);

    /**
     * @description: 通过文章id和用户id删除该文章的收藏记录
     * @author: 陈文振
     * @date: 2019/12/22
     * @param articleId: 文章Id
     * @param username: 用户名
     * @return:
     */
    DataMap deleteArticleFavoriteRecordByArticleIdAndUsername(int articleId, String username);

    /**
     * @description: 通过用户名获得该用户的文章收藏信息
     * @author: 陈文振
     * @date: 2019/12/22
     * @param username: 用户名
     * @return:
     */
    DataMap findArticleFavoriteRecordByUsername(String username, int rows, int pageNum);

    /**
     * @description: 统计该用户的所有收藏文章数
     * @author: 陈文振
     * @date: 2019/12/22
     * @param username: 用户名
     * @return:
     */
    int countArticleFavoriteRecordByUsername(String username);

    /**
     * @description: 统计该文章的被收藏数
     * @author: 陈文振
     * @date: 2019/12/22
     * @param articleId: 文章Id
     * @return:
     */
    int countArticleFavoriteRecordByArticleId(int articleId);
}
