package com.cwz.blog.defaultblog.service_test;

import com.alibaba.fastjson.JSON;
import com.cwz.blog.defaultblog.entity.Tags;
import com.cwz.blog.defaultblog.service.TagsService;
import com.cwz.blog.defaultblog.utils.DataMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TagsServiceTest {

    @Autowired
    private TagsService tagsService;

    @Test
    void findTagsNameAndArticleCountNum() {
        DataMap dataMap = tagsService.findTagsInfoAndArticleCountNum(5,0, null, null, null);
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void findTagsName() {
        DataMap dataMap = tagsService.findTagsName();
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void countTagsNum() {
        int i = tagsService.countTagsNum();
        System.out.println(i);
    }

    @Test
    void findAllTags() {
        DataMap dataMap = tagsService.findAllTags();
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void updateTags() {
        DataMap dataMap = tagsService.updateTags("aa", 2);
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void tagIsExistByTagName() {
        Tags tag = tagsService.tagIsExistByTagName("旅游a");
        System.out.println(JSON.toJSONString(tag));
    }
}
