package com.cwz.blog.defaultblog.service_test;

import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ArticleServiceTest {

    @Autowired
    private ArticleService articleService;

    @Test
    void findArticleByArchive(){
        JSONObject articleByArchive =
                articleService.findArticleByArchive("2018-08-15", 0, 5);
        System.out.println(articleByArchive);
    }
}
