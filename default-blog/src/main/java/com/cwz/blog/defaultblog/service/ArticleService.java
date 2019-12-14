package com.cwz.blog.defaultblog.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.entity.Article;
import org.springframework.transaction.annotation.Transactional;

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
     * @return: status: 200--成功   500--失败
     */
    JSONObject insertArticle(Article article);

    /**
     * @description: 修改文章
     * @author: 陈文振
     * @date: 2019/12/3
     * @param article: 文章
     * @return:
     */
    @Transactional
    JSONObject updateArticleById(Article article);

    /**
     * @description: 获得文章
     * @author: 陈文振
     * @date: 2019/12/3
     * @param articleId: 文章id
     * @return:
     */
    JSONObject getArticleByArticleId(long articleId, String username);

    /**
     * @description: 通过文章id获得文章名和文章摘要
     * @author: 陈文振
     * @date: 2019/12/3
     * @param articleId: 文章id
     * @return: 文章名
     */
    Map<String, String> findArticleTitleByArticleId(long articleId);

    /**
     * @description: 分页获得所有文章
     * @author: 陈文振
     * @date: 2019/12/3
     * @param rows: 一页显示文章数
     * @param pageNum: 第几页
     * @return: 该页所有文章
     */
    JSONArray findAllArticles(int rows, int pageNum);

    /**
     * @description: 通过文章id更新它的上一篇或下一篇文章id
     * @author: 陈文振
     * @date: 2019/12/3
     */
    void updateArticleLastOrNextId(String lastOrNext, long lastOrNextArticleId, long articleId);

    /**
     * @description: 文章点赞
     * @author: 陈文振
     * @date: 2019/12/3
     * @param articleId: 文章id
     * @return: 目前点赞数
     */
    int updateLikeByArticleId(long articleId);

    /**
     * @description: 通过标签分页获得文章部分信息
     * @author: 陈文振
     * @date: 2019/12/3
     * @param tag: 标签
     * @param rows: 一页显示文章数
     * @param pageNum: 第几页
     * @return:
     */
    JSONObject findArticleByTag(String tag, int rows, int pageNum);

    /**
     * @description: 分页获得该分类下的所有文章
     * @author: 陈文振
     * @date: 2019/12/3
     * @param category: 分类
     * @param rows: 一页显示文章数
     * @param pageNum: 第几页
     * @return:
     */
    JSONObject findArticleByCategory(String category, int rows, int pageNum);

    /**
     * @description: 分页获得该归档日期下的所有文章
     * @author: 陈文振
     * @date: 2019/12/3
     * @param archiveName: 发布时间
     * @param rows: 一页显示文章数
     * @param pageNum: 页数
     * @return:
     */
    JSONObject findArticleByArchive(String archiveName, int rows, int pageNum);

    /**
     * @description: 获得草稿中的文章
     * @author: 陈文振
     * @date: 2019/12/3
     * @return:
     */
    JSONObject getDraftArticle(Article article, String[] articleTags, int articleGrade);

    /**
     * @description: 分页获得文章管理
     * @author: 陈文振
     * @date: 2019/12/3
     * @param rows: 一页显示文章数
     * @param pageNum: 第几页
     * @return: 该页所有文章
     */
    JSONObject getArticleManagement(int rows, int pageNum);

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
    int countArticleCategoryByCategory(String category);

    /**
     * @description: 计算该归档日期文章的数目
     * @author: 陈文振
     * @date: 2019/12/3
     * @param archive: 发布日期
     * @return: 该归档日期下文章的数目
     */
    int countArticleArchiveByArchive(String archive);

    /**
     * @description: 计算所有文章的数量
     * @author: 陈文振
     * @date: 2019/12/3
     * @return: 所有文章的数量
     */
    int countArticle();

    /**
     * @description: 通过id删除文章
     * @author: 陈文振
     * @date: 2019/12/3
     * @return: 1--删除成功  0--删除失败
     */
    @Transactional
    int deleteArticle(long id);
}
