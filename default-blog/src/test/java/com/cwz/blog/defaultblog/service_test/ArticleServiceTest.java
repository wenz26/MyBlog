package com.cwz.blog.defaultblog.service_test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.entity.Article;
import com.cwz.blog.defaultblog.entity.Tags;
import com.cwz.blog.defaultblog.mapper.ArticleMapper;
import com.cwz.blog.defaultblog.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class ArticleServiceTest {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleService articleService;

    @Test
    void findArticleByArchive(){

    }

    @Test
    void addArtcile(){
        LocalDateTime localDateTime = LocalDateTime.now();
        Article article = new Article();
        /*article.setUserId(1);
        article.setAuthor("陈文振");
        article.setOriginalAuthor("陈文振");
        article.setArticleTitle("我的第一篇博客");
        article.setArticleContent("我的老天爷，这个真的好呀！！！");
        article.setArticleType("原创");
        article.setArticleCategories(1);
        article.setPublishDate(localDateTime);
        article.setUpdateDate(localDateTime);
        article.setArticleUrl("http://test-artcileUrl");
        article.setImageUrl("http://test-imageUrl");
        article.setArticleTabloid("我的老天爷");
        article.setLikes(0);
        article.setLastArticleId(0);
        article.setNextArticleId(0);*/
        article.setUserId(2);
        article.setOriginalAuthor("张三");
        article.setArticleTitle("关于我的大学");
        article.setArticleContent("我的大学呀，我要成为第一！！！");
        article.setArticleType("原创");
        article.setArticleCategories(2);
        article.setPublishDate(localDateTime);
        article.setUpdateDate(localDateTime);
        article.setArticleUrl("http://test-artcileUrl");
        article.setImageUrl("http://test-imageUrl");
        article.setArticleTabloid("哈子呀");
        article.setLikes(0);
        article.setLastArticleId(0);
        article.setNextArticleId(0);
        articleMapper.insert(article);
        System.out.println(article.getId());
    }

    @Test
    void selectArticleAndTags(){
        Article article = articleMapper.getArticleAndTagsByArticleId(1, null);
        System.out.println(JSON.toJSONString(article));
    }

    @Test
    void findAllToPublishArticles(){

        /*List<Article> articles = articleMapper.findAllToDraftArticles(1, null, "2019-12", "陈文振");
        System.out.println(JSON.toJSONString(articles));*/
    }

    @Test
    void findTag(){
        /*List<Object> articleId = articleMapper.findArticleByTag(1);
        System.out.println(JSON.toJSONString(articleId.get(0)));*/
        articleMapper.updateLastOrNextId("likes", 1, 1);
    }

    @Test
    void listByTest(){
        List<String> tags = new ArrayList<>();
        tags.add("11");
        tags.add("22");
        tags.add("33");
        System.out.println(tags.size());

        String[] tagNames = new String[10];
        for (int i = 0; i < tags.size(); i++) {
            tagNames[i] = tags.get(i);
        }
        System.out.println(tagNames.toString());
    }


}
