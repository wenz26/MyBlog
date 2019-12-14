package com.cwz.blog.defaultblog.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.component.StringAndArray;
import com.cwz.blog.defaultblog.entity.Article;
import com.cwz.blog.defaultblog.mapper.ArticleMapper;
import com.cwz.blog.defaultblog.service.ArticleService;
import com.cwz.blog.defaultblog.utils.TimeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author: 陈文振
 * @date: 2019/12/3
 * @description: 文章业务操作实现类
 */
@Service("articleService")
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public JSONObject insertArticle(Article article) {
        return null;
    }

    @Override
    public JSONObject updateArticleById(Article article) {
        return null;
    }

    @Override
    public JSONObject getArticleByArticleId(long articleId, String username) {
        return null;
    }

    @Override
    public Map<String, String> findArticleTitleByArticleId(long articleId) {
        return null;
    }

    @Override
    public JSONArray findAllArticles(int rows, int pageNum) {
        return null;
    }

    @Override
    public void updateArticleLastOrNextId(String lastOrNext, long lastOrNextArticleId, long articleId) {

    }

    @Override
    public int updateLikeByArticleId(long articleId) {
        return 0;
    }

    @Override
    public JSONObject findArticleByTag(String tag, int rows, int pageNum) {
        return null;
    }

    @Override
    public JSONObject findArticleByCategory(String category, int rows, int pageNum) {
        return null;
    }

    @Override
    public JSONObject findArticleByArchive(String archiveName, int rows, int pageNum) {
        List<Article> articles;
        PageInfo<Article> pageInfo;
        Example articleExample;
        TimeUtil timeUtil = new TimeUtil();
        String showMonth = "hide";
        if (!Objects.equals(archiveName, "")) {
            archiveName = timeUtil.timeYearToWhippletree(archiveName);
        }

        // true表示需要统计总数，这样会多进行一次请求select count(0)；省略掉true参数只返回分页数据。
        PageHelper.startPage(pageNum, rows, true);

        if (Objects.equals(archiveName, "")) {
            articleExample = new Example(Article.class);
            articleExample.orderBy("id").desc();
            articles = articleMapper.selectByExample(articleExample);
        } else {
            articleExample = new Example(Article.class);
            articleExample.orderBy("id").desc();
            Example.Criteria articleCriteria = articleExample.createCriteria();
            articleCriteria.andLike("publishDate", "%" + archiveName + "%");
            articles = articleMapper.selectByExample(articleExample);
            showMonth = "show";
        }
        pageInfo = new PageInfo<>(articles);

        JSONArray articleJsonArray = timeLineReturn(articles);

        // 对分页信息的封装
        JSONObject pageJson = jsonObjectToPageInfo(pageInfo);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", 200);
        jsonObject.put("result", articleJsonArray);
        jsonObject.put("pageInfo", pageJson);
        // article的总数
        jsonObject.put("articleNum", articleMapper.selectCount(null));
        jsonObject.put("showMonth", showMonth);

        return jsonObject;
    }

    @Override
    public JSONObject getDraftArticle(Article article, String[] articleTags, int articleGrade) {
        return null;
    }

    @Override
    public JSONObject getArticleManagement(int rows, int pageNum) {
        return null;
    }

    @Override
    public Article findArticleById(int id) {
        return null;
    }

    @Override
    public int countArticleCategoryByCategory(String category) {
        return 0;
    }

    @Override
    public int countArticleArchiveByArchive(String archive) {
        Example articleExample = new Example(Article.class);
        Example.Criteria articleCriteria = articleExample.createCriteria();
        articleCriteria.andLike("publishDate", "%" + archive + "%");
        return articleMapper.selectCountByExample(articleExample);
    }

    @Override
    public int countArticle() {
        return 0;
    }

    @Override
    public int deleteArticle(long id) {
        return 0;
    }

    /**
     * @description: 封装时间线中数据成JsonArray形式
     * @author: 陈文振
     * @date: 2019/12/7
     * @param articles: article数组
     * @return:
     */
    private JSONArray timeLineReturn(List<Article> articles) {
        JSONArray articleJsonArray = new JSONArray();
        JSONObject articleJson;
        for (Article article : articles) {
            String[] tagsArray = StringAndArray.stringToArray(article.getArticleTags());
            // 把 Article 类封装成 JSONObject
            articleJson = jsonObjectToArticle(article, tagsArray);
            articleJsonArray.add(articleJson);
        }
        return articleJsonArray;
    }

    /**
     * @description: 把 Article 类封装成 JSONObject
     * @author: 陈文振
     * @date: 2019/12/7
     * @param article: Article 类
     * @param tagsArray: tags 数组
     * @return:
     */
    private JSONObject jsonObjectToArticle(Article article, String[] tagsArray) {
        JSONObject articleJson = new JSONObject();
        articleJson.put("articleId", article.getArticleId());
        articleJson.put("originalAuthor", article.getOriginalAuthor());
        articleJson.put("articleTitle", article.getArticleTitle());
        articleJson.put("articleCategories", article.getArticleCategories());
        articleJson.put("publishDate", article.getPublishDate());
        articleJson.put("articleTags", tagsArray);
        return articleJson;
    }

    /**
     * @description: 对分页信息的封装
     * @author: 陈文振
     * @date: 2019/12/7
     * @param pageInfo: 分页信息
     * @return:
     */
    private JSONObject jsonObjectToPageInfo(PageInfo pageInfo){
        JSONObject pageJson = new JSONObject();
        // 当前页
        pageJson.put("pageNum", pageInfo.getPageNum());
        // 每页的数量
        pageJson.put("pageSize", pageInfo.getPageSize());
        // 总记录数
        pageJson.put("total", pageInfo.getTotal());
        // 总页数
        pageJson.put("pages", pageInfo.getPages());
        // 是否为第一页
        pageJson.put("isFirstPage", pageInfo.isIsFirstPage());
        // 是否为最后一页
        pageJson.put("isLastPage", pageInfo.isIsLastPage());

        return pageJson;
    }

}
