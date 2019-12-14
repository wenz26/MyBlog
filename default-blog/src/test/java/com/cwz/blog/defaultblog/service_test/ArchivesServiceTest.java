package com.cwz.blog.defaultblog.service_test;

import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.service.ArchivesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ArchivesServiceTest {

    @Autowired
    private ArchivesService archivesService;

    @Test
    void findArchiveNameAndArticleNum(){
        JSONObject archiveNameAndArticleNum = archivesService.findArchiveNameAndArticleNum();
        System.out.println(archiveNameAndArticleNum.toJSONString());
    }
}
