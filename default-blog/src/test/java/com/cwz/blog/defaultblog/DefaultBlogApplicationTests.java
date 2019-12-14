package com.cwz.blog.defaultblog;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.entity.Article;
import com.cwz.blog.defaultblog.mapper.ArticleMapper;
import com.cwz.blog.defaultblog.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootTest
class DefaultBlogApplicationTests {

    @Autowired
    private ArticleMapper articleMapper;

    @Test
    void contextLoads() {

    }

    @Test
    void stringToUniCode(){
        String gbString = "陈文振";
        char[] utfBytes = gbString.toCharArray();
        String unicodeBytes = "";
        for (int i = 0; i < utfBytes.length; i++) {
            String hexB = Integer.toHexString(utfBytes[i]);
            System.out.println(hexB);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        System.out.println(unicodeBytes);
    }

    @Test
    void stringTest(){
        List<String> strings = new ArrayList<>();
        strings.add("aaa");
        strings.add("bbb");
        strings.add("ccc");
        strings.add("ddd");
        strings.add("eee");

        System.out.println("strings的值为：" + strings.toString());
        System.out.println("strings的长度为：" + strings.size());

        List<List<String>> lists = Collections.singletonList(strings);
        System.out.println("lists的值为：" + lists.toString());
        System.out.println("lists的长度为：" + lists.size());

        // Collections.singletonList返回长度为 1 的List列表
        JSONArray jsonArray = new JSONArray(Collections.singletonList(strings));
        System.out.println("jsonArray的值为：" + jsonArray.toString());

        JSONArray jsonArray1 = JSONArray.parseArray(JSONObject.toJSONString(strings));
        System.out.println("jsonArray1的值为：" + jsonArray1.toString());
    }

    @Test
    void sqlTest(){
        System.out.println(articleMapper.selectCount(null));

        Article article = new Article();
        article.setLikes(3);

        System.out.println(articleMapper.selectCount(article));
    }

}
