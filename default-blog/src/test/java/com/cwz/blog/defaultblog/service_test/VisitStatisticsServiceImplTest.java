package com.cwz.blog.defaultblog.service_test;

import com.alibaba.fastjson.JSON;
import com.cwz.blog.defaultblog.service.VisitStatisticsService;
import com.cwz.blog.defaultblog.utils.DataMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@SpringBootTest
class VisitStatisticsServiceImplTest {

    @Autowired
    private VisitStatisticsService visitStatisticsService;

    @Test
    void addVisitNumByStatisticsName() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        DataMap dataMap = visitStatisticsService.addVisitNumByStatisticsName("article/1", request);
        System.out.println(JSON.toJSONString(dataMap));
    }

    @Test
    void getNumByStatisticsName() {
        long articleNum = visitStatisticsService.getNumByStatisticsName("article/1");
        System.out.println(articleNum);

    }

    @Test
    void insertVisitArticlePage() {
        //visitStatisticsService.insertVisitArticlePage("totalVisitors");
        visitStatisticsService.insertVisitArticlePage("article/1");
    }

    @Test
    void getTotalVisit() {
        long totalVisit = visitStatisticsService.getTotalVisit();
        System.out.println(totalVisit);
    }

    @Test
    void updateVisitNumByStatisticsName() {
        visitStatisticsService.updateVisitNumByStatisticsName("article/1", 1L);
        visitStatisticsService.updateVisitNumByStatisticsName("totalVisitors", 2L);
    }
}
