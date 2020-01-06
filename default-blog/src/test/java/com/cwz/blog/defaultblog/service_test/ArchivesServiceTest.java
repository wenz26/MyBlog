package com.cwz.blog.defaultblog.service_test;

import com.alibaba.fastjson.JSON;
import com.cwz.blog.defaultblog.service.ArchiveService;
import com.cwz.blog.defaultblog.utils.DataMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ArchivesServiceTest {

    @Autowired
    private ArchiveService archiveService;

    @Test
    void findPublishDateToArctile() {
        DataMap publishDateToArctile = archiveService.findPublishDateToArticle("陈文振");
        System.out.println(JSON.toJSONString(publishDateToArctile));
    }
}
