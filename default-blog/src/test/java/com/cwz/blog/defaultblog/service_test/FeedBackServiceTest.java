package com.cwz.blog.defaultblog.service_test;

import com.alibaba.fastjson.JSON;
import com.cwz.blog.defaultblog.entity.FeedBack;
import com.cwz.blog.defaultblog.service.FeedBackService;
import com.cwz.blog.defaultblog.utils.DataMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FeedBackServiceTest {

    @Autowired
    private FeedBackService feedBackService;

    @Test
    void submitFeedback() {
        FeedBack feedBack = new FeedBack();
        feedBack.setContactInfo("154297521");
        feedBack.setUserId(1);
        feedBack.setFeedbackContent("哇擦这个牛批！！！");
        feedBackService.submitFeedback(feedBack);
    }

    @Test
    void getAllFeedback() {
        DataMap allFeedback = feedBackService.getAllFeedback(5, 0);
        System.out.println(JSON.toJSONString(allFeedback));

    }
}
