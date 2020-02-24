package com.cwz.blog.defaultblog.service_test;

import com.alibaba.fastjson.JSON;
import com.cwz.blog.defaultblog.entity.ArticleUserFavoriteRecord;
import com.cwz.blog.defaultblog.service.ArticleUserFavoriteRecordService;
import com.cwz.blog.defaultblog.utils.DataMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArticleUserFavoriteRecordServiceTest {

    @Autowired
    private ArticleUserFavoriteRecordService articleUserFavoriteRecordService;

    @Test
    void isFavorite() {
        boolean favorite = articleUserFavoriteRecordService.isFavorite(5, "陈文振");
        System.out.println(favorite);

    }

    @Test
    void insertArticleFavoriteRecord() {
        ArticleUserFavoriteRecord articleUserFavoriteRecord = new ArticleUserFavoriteRecord();
        LocalDateTime localDateTime = LocalDateTime.now();
        articleUserFavoriteRecord.setCreateDate(localDateTime);
        articleUserFavoriteRecord.setArticleId(4);
        articleUserFavoriteRecord.setUserId(1);
        articleUserFavoriteRecordService.insertArticleFavoriteRecord(articleUserFavoriteRecord);
    }

    @Test
    void deleteArticleFavoriteRecordByArticleIdAndUsername() {
        DataMap dataMap = articleUserFavoriteRecordService.deleteArticleFavoriteRecordByArticleIdAndUsername(4, "陈文振");
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void findArticleFavoriteRecordByUsername() {
        DataMap dataMap = articleUserFavoriteRecordService.findArticleFavoriteRecordByUsername("陈文振", null, 2, 0);
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void countArticleFavoriteRecordByUsername() {
    }

    @Test
    void countArticleFavoriteRecordByArticleId() {
        int i = articleUserFavoriteRecordService.countArticleFavoriteRecordByArticleId(1);
        System.out.println(i);
    }
}
