package com.cwz.blog.defaultblog.service_test;

import com.alibaba.fastjson.JSON;
import com.cwz.blog.defaultblog.service.AllService;
import com.cwz.blog.defaultblog.utils.DataMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AllControllerTest {

    @Autowired
    private AllService allService;

    @Test
    void searchByKeyWords() {
        DataMap dataMap = allService.searchByKeyWords("文", "all", 1, 4,8, 1);
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void searchHistory() {
        DataMap dataMap = allService.searchHistory(1);
        System.out.println(JSON.toJSONString(dataMap));
    }
}
