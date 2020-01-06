package com.cwz.blog.defaultblog.service_test;

import com.alibaba.fastjson.JSON;
import com.cwz.blog.defaultblog.entity.ArticleLikesRecord;
import com.cwz.blog.defaultblog.redis.StringRedisServiceImpl;
import com.cwz.blog.defaultblog.service.ArticleLikesRecordService;
import com.cwz.blog.defaultblog.utils.DataMap;
import com.cwz.blog.defaultblog.utils.StringUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
class ArticleLikesRecordServiceTest {

    @Autowired
    private ArticleLikesRecordService articleLikesRecordService;

    @Autowired
    private StringRedisServiceImpl stringRedisService;

    @Test
    void insertArticleLikesRecord() {
        LocalDateTime localDateTime = LocalDateTime.now();

        ArticleLikesRecord articleLikesRecord = new ArticleLikesRecord();
        articleLikesRecord.setArticleId(1);
        articleLikesRecord.setUserId(1);
        articleLikesRecord.setLikeDate(localDateTime);
        articleLikesRecordService.insertArticleLikesRecord(articleLikesRecord);
    }

    @Test
    void isLike() {
        boolean like = articleLikesRecordService.isLike(1, "陈文振");
        System.out.println(like);
    }

    @Test
    void deleteArticleLikesRecordByArticleId() {
        articleLikesRecordService.deleteArticleLikesRecordByArticleId(2);
    }

    @Test
    void getArticleThumbsUp() {
        DataMap articleThumbsUp = articleLikesRecordService.getArticleThumbsUp(5, 0, "陈文振");
        System.out.println(JSON.toJSONString(articleThumbsUp));
    }

    @Test
    void readThisThumbsUp() {
        //stringRedisService.set(StringUtil.ARTICLE_THUMBS_UP, 3);
        DataMap dataMap = articleLikesRecordService.readThisThumbsUp(4);
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void readAllThumbsUp() {
        DataMap dataMap = articleLikesRecordService.readAllThumbsUp("陈文振");
        System.out.println(JSON.toJSONString(dataMap));
    }
}
