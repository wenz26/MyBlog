package com.cwz.blog.defaultblog.service;

import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.entity.Article;
import com.cwz.blog.defaultblog.utils.DataMap;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author: 陈文振
 * @date: 2019/12/3
 * @description: 文章业务操作
 */
public interface ArticleService {

    /**
     * @description: 保存文章
     * @author: 陈文振
     * @date: 2019/12/3
     * @param article: 文章
     * @param username: 当前用户的用户名
     * @return: status: 200--成功   500--失败
     */
    @Transactional
    DataMap insertArticle(Article article, String username);

    /**
     * @description: （获得草稿文章或已发布文章后）修改文章
     * @author: 陈文振
     * @date: 2019/12/3
     * @param article: 文章
     * @return:
     */
    @Transactional
    DataMap updateArticleById(Article article, String username);

    /**
     * @description: 获得文章
     * @author: 陈文振
     * @date: 2019/12/3
     * @param articleId: 文章id
     * @return:
     */
    DataMap getArticleByArticleId(int articleId, String username);

    /**
     * @description: 通过文章id获得文章名和文章摘要
     * @author: 陈文振
     * @date: 2019/12/3
     * @param articleId: 文章id
     * @return: 文章名
     */
    Map<String, String> findArticleTitleByArticleId(int articleId);

    /**
     * @description: 分页获得所有发布文章
     * @author: 陈文振
     * @date: 2019/12/3
     * @param rows: 一页显示文章数
     * @param pageNum: 第几页
     * @return: 该页所有文章
     */
    DataMap findAllToPublishArticles(int rows, int pageNum);

    /**
     * @description: 分页获得该用户的所有草稿文章
     * @author: 陈文振
     * @date: 2019/12/25
     * @param rows: 一页显示文章数
     * @param pageNum: 第几页
     * @return: com.cwz.blog.defaultblog.utils.DataMap
     */
    DataMap findAllToDraftArticles(String username, int rows, int pageNum);

    /**
     * @description: 通过文章id更新它的上一篇或下一篇文章id
     * @author: 陈文振
     * @date: 2019/12/3
     */
    void updateArticleLastOrNextId(String lastOrNext, int lastOrNextArticleId, int articleId);

    /**
     * @description: 文章点赞
     * @author: 陈文振
     * @date: 2019/12/3
     * @param articleId: 文章id
     * @return: 目前点赞数
     */
    DataMap updateLikeByArticleId(int articleId);

    /**
     * @description: 文章收藏
     * @author: 陈文振
     * @date: 2020/1/6
     * @param articleId
     * @return: com.cwz.blog.defaultblog.utils.DataMap
     */
    DataMap updateFavoriteByArticleId(int articleId);

    /**
     * @description: 通过标签分页获得文章部分信息
     * @author: 陈文振
     * @date: 2019/12/3
     * @param tagName: 标签名
     * @param rows: 一页显示文章数
     * @param pageNum: 第几页
     * @return:
     */
    DataMap findArticleByTag(String tagName, int rows, int pageNum);

    /**
     * @description: 分页获得该分类下的所有文章
     * @author: 陈文振
     * @date: 2019/12/3
     * @param categoryName: 分类名
     * @param rows: 一页显示文章数
     * @param pageNum: 第几页
     * @return:
     */
    DataMap findArticleByCategory(String categoryName, int rows, int pageNum);

    /**
     * @description: 分页获得该归档日期下的该用户的所有文章
     * @author: 陈文振
     * @date: 2019/12/3
     * @param archiveDay: 发布时间
     * @param rows: 一页显示文章数
     * @param pageNum: 页数
     * @return:
     */
    DataMap findArticleByArchive(String archiveDay, String username, int rows, int pageNum);

    /**
     * @description: 获得草稿中的文章
     * @author: 陈文振
     * @date: 2019/12/3
     * @return:
     */
    DataMap getDraftArticle(Article article);

    /**
     * @description: 分页获得文章管理
     * @author: 陈文振
     * @date: 2020/1/6
     * @param rows
     * @param pageNum
     * @param userId
     * @param draft
     * @return: com.cwz.blog.defaultblog.utils.DataMap
     */
    DataMap getArticleManagement(int rows, int pageNum, Integer userId, Integer draft);

    /**
     * @description: 通过id获取文章
     * @author: 陈文振
     * @date: 2019/12/3
     * @return:
     */
    Article findArticleById(int id);

    /**
     * @description: 计算该分类文章的数目
     * @author: 陈文振
     * @date: 2019/12/3
     * @param category: 分类名
     * @return: 该分类下文章的数目
     */
    int countArticleCategoryByCategory(int category);

    /**
     * @description: 计算该归档日期文章的数目
     * @author: 陈文振
     * @date: 2019/12/3
     * @param archiveDay: 发布日期
     * @return: 该归档日期下文章的数目
     */
    int countArticleArchiveByArchive(String archiveDay);

    /**
     * @description: 计算所有已发布文章的数量
     * @author: 陈文振
     * @date: 2019/12/3
     * @return: 所有已发布文章的数量
     */
    int countArticleToPublish();

    /**
     * @description: 计算所有草稿箱文章的数量
     * @author: 陈文振
     * @date: 2019/12/3
     * @return: 所有草稿箱文章的数量
     */
    int countArticleToDraft();

    /**
     * @description: 通过id删除文章
     * @author: 陈文振
     * @date: 2019/12/3
     * @return: 1--删除成功  0--删除失败
     */
    @Transactional
    DataMap deleteArticle(int id);

    /**
     * @description: 通过文章id找文章图片url
     * @author: 陈文振
     * @date: 2019/12/18
     * @param articleId: 文章id
     * @return:
     */
    String getImageUrl(int articleId);

    /**
     * @description: 对 通过标签查找文章 返回的数据进行一个封装
     * @author: 陈文振
     * @date: 2019/12/25
     * @param article
     * @return: com.alibaba.fastjson.JSONObject
     */
    JSONObject getArticleByTagAndCategoryToJsonObject(Article article);

    /**
     * @description: 通过文章id找文章对应的标签
     * @author: 陈文振
     * @date: 2019/12/18
     * @param articleId: 文章id
     * @return:
     */
    //List<Object> findTagesByArticleId(int articleId);

    /**
     * @description: 通过文章id找文章对应的作者id
     * @author: 陈文振
     * @date: 2019/12/18
     * @param articleId: 文章id
     * @return:
     */
    //int findUserIdByArticleId(int articleId);

    /**
     * @description: 通过文章id找文章对应的文章分类
     * @author: 陈文振
     * @date: 2019/12/18
     * @param articleId: 文章id
     * @return:
     */
    //int findArticleCategoriesByArticleId(int articleId);

    // 通过文章id找图片，imageUrl作为标签的背景图

    // 找到所有的草稿箱文章
}
