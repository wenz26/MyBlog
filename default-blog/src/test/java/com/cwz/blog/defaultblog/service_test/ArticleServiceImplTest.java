package com.cwz.blog.defaultblog.service_test;

import com.alibaba.fastjson.JSON;
import com.cwz.blog.defaultblog.component.StringAndArray;
import com.cwz.blog.defaultblog.entity.Article;
import com.cwz.blog.defaultblog.entity.Categories;
import com.cwz.blog.defaultblog.service.ArticleService;
import com.cwz.blog.defaultblog.service.CategoriesService;
import com.cwz.blog.defaultblog.utils.DataMap;
import com.cwz.blog.defaultblog.utils.StringUtil;
import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArticleServiceImplTest {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private CategoriesService categoriesService;

    @Test
    void insertArticle() {
        Article article = new Article();
        article.setArticleTitle("这是保存为草稿的哦");
        article.setArticleContent("123456我的啦啦啦");
        //article.setArticleType("原创");
        //article.setArticleCategories(3);
        //article.setImageUrl("http://test-imageurl");
        //article.setTagName("游戏,动漫,super");
        article.setArticleTabloid("┭┮﹏┭┮");
        article.setDraft(0);
        DataMap dataMap = articleService.insertArticle(article, "陈文振");
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void updateArticleById() {
        LocalDateTime localDateTime = LocalDateTime.now();

        Article article = new Article();
        article.setArticleTitle("我是第一个");
        article.setArticleContent("我要打十个");
        article.setArticleTabloid("giaooooooo！！！！");

        article.setId(17);
        article.setUpdateDate(localDateTime);
        article.setArticleType("原创");
        //article.setArticleUrl("http://1111111111");
        article.setArticleCategories(1);
        article.setImageUrl("http://test-imageurl");
        article.setTagName("游戏");
        article.setDraft(1);


        DataMap dataMap = articleService.updateArticleById(article, "陈文振");
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void getArticleByArticleId() {
        DataMap dataMap = articleService.getArticleByArticleId(10, "陈文振");
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void findArticleTitleByArticleId() {
        Map<String, String> dataMap = articleService.findArticleTitleByArticleId(18);
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void findAllToPublishArticles() {
        DataMap dataMap = articleService.findAllToPublishArticles(25, 1);
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void findAllToDraftArticles() {
        DataMap dataMap = articleService.findAllToDraftArticles("陈文振",10, 0);
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void updateArticleLastOrNextId() {

    }

    @Test
    void updateLikeByArticleId() {
        DataMap dataMap = articleService.updateLikeByArticleId(21);
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void findArticleByTag() {
        DataMap dataMap = articleService.findArticleByTag("动漫", 5, 0);
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void findArticleByCategory() {
        DataMap dataMap = articleService.findArticleByCategory("", 10, 0);
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void findArticleByArchive() {
        DataMap dataMap = articleService.findArticleByArchive("2019年12月", null, 10, 0);
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void getDraftArticle() {
        Article article = articleService.findArticleById(15);
        DataMap dataMap = articleService.getDraftArticle(article);
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void getArticleManagement() {
        DataMap dataMap = articleService.getArticleManagement(10, 0, null, null);
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void findArticleById() {
    }

    @Test
    void countArticleCategoryByCategory() {
        Categories category = categoriesService.findCategoryByCategoryName("我的故事");
        int i = articleService.countArticleCategoryByCategory(category.getId());
        System.out.println(i);
    }

    @Test
    void countArticleArchiveByArchive() {
        int i = articleService.countArticleArchiveByArchive("2019-12");
        System.out.println(i);
    }

    @Test
    void countArticleToPublish() {
        int i = articleService.countArticleToPublish();
        System.out.println(i);
    }

    @Test
    void countArticleToDraft() {
        int i = articleService.countArticleToDraft();
        System.out.println(i);
    }

    @Test
    void deleteArticle() {
        DataMap dataMap = articleService.deleteArticle(16);
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void getImageUrl() {
        String imageUrl = articleService.getImageUrl(1);
        System.out.println(imageUrl);
    }

    @Test
    void getArticleByTagAndCategoryToJsonObject() {
        String s = "";
        System.out.println(s.length());
    }

    @Test
    void tagsTest() {
        String[] strings = {"ads", "我的", "动漫", "asd564s王"};
        String string = StringAndArray.arrayToString(strings);
        System.out.println(string);

        String[] strings2 = {"adssd", "你的啥子哦", "dau87哈哈"};
        String string2 = StringAndArray.arrayToString(strings2);
        System.out.println(string2);
        /*String tags = "懂啊吗,我的,def,451";
        String[] toArrays = StringAndArray.stringToArray(tags);
        for (String toArray : toArrays) {
            System.out.println(toArray);
        }*/
    }
}
