package com.cwz.blog.defaultblog.component;

import com.cwz.blog.defaultblog.aspect.annotation.LogAnnotation;
import com.cwz.blog.defaultblog.redis.HashRedisServiceImpl;
import com.cwz.blog.defaultblog.service.VisitStatisticsService;
import com.cwz.blog.defaultblog.utils.StringUtil;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @author: 陈文振
 * @date: 2020/1/1
 * @description: 定时任务
 */
@Component
public class ScheduledTask {

    @Autowired
    private HashRedisServiceImpl hashRedisService;
    @Autowired
    private VisitStatisticsService visitStatisticsService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @description: 每晚24点清空redis中当日网站访问记录和验证码，totalVisitors、visitorVolume、yesterdayVisitors除外
     * cron表达式生成器：http://cron.qqe2.com/
     * @author: 陈文振
     * @date: 2020/1/1
     * @param
     * @return: void
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetRedisInfo() {
        logger.info("定时任务开始————Redis清除");
        long oldTotalVisitors = visitStatisticsService.getTotalVisit();
        long newTotalVisitors = Long.valueOf(hashRedisService.get("visit", "totalVisitors").toString());

        long yesterdayVisitors = newTotalVisitors - oldTotalVisitors;

        if (hashRedisService.hasHashKey("visit", "yesterdayVisitors")) {
            hashRedisService.put("visit", "yesterdayVisitors", yesterdayVisitors);
        } else {
            hashRedisService.put("visit", "yesterdayVisitors", oldTotalVisitors);
        }

        LocalDateTime localDateTime = LocalDateTime.now().plusSeconds(-3600);
        String string = localDateTime.getDayOfWeek().toString();
        if (hashRedisService.hasHashKey("visit", string)) {
            hashRedisService.put("visit", string, yesterdayVisitors);
        }

        // 将redis中的所有访客记录更新到数据库中
        LinkedHashMap map = (LinkedHashMap) hashRedisService.getAllFieldAndValue("visit");
        String statisticsName;
        for (Object e : map.keySet()) {
            statisticsName = String.valueOf(e);
            visitStatisticsService.updateVisitNumByStatisticsName(statisticsName, (Integer) map.get(statisticsName));
            /*if (!Objects.equals(statisticsName, "totalVisitors") && !Objects.equals(statisticsName, "visitorVolume")
                    && !Objects.equals(statisticsName, "yesterdayVisitors")) {
                hashRedisService.hashDelete("visit", statisticsName);
            }*/
        }

        hashRedisService.deleteByKey(StringUtil.PREFIX_IMAGE_CODE);
        hashRedisService.deleteByKey(StringUtil.PREFIX_SMS_CODE);
    }
}
