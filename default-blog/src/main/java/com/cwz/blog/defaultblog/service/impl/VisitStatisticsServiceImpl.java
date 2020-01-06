package com.cwz.blog.defaultblog.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.entity.VisitStatistics;
import com.cwz.blog.defaultblog.mapper.VisitStatisticsMapper;
import com.cwz.blog.defaultblog.redis.RedisToService;
import com.cwz.blog.defaultblog.service.VisitStatisticsService;
import com.cwz.blog.defaultblog.utils.DataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: 陈文振
 * @date: 2019/12/22
 * @description: 访问量统计业务操作实现类
 */
@Service("visitorService")
public class VisitStatisticsServiceImpl implements VisitStatisticsService {

    // 访问键
    private static final String VISIT = "visit";
    // 总访问人数
    private static final String TOTAL_VISITORS = "totalVisitors";
    // 对应 某个对象 的访问量
    private static final String STATISTICS_VISIT = "statisticsVisit";

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private VisitStatisticsMapper visitStatisticsMapper;
    @Autowired
    private RedisToService redisToService;

    @Override
    public DataMap addVisitNumByStatisticsName(String statisticsName, HttpServletRequest request) {

        String visitor;
        Long statisticsVisit;
        JSONObject jsonObject = new JSONObject();
        Example example = new Example(VisitStatistics.class);
        example.selectProperties("visitNum");
        example.createCriteria().andEqualTo("statisticsName", statisticsName);

        // 在 session 上找有没有 用户的访问信息
        visitor = (String) request.getSession().getAttribute(statisticsName);
        logger.info("session中的visitor值（在session中保存该用户访问页面的记录，在一段时间内重复访问时不增加在页面的访问人次）：" + visitor);
        if (visitor == null) {
            // 先去 redis 中查找 (通过 statisticsName 来查找对应的 统计数)
            statisticsVisit = redisToService.addVisitorNumOnRedis(VISIT, statisticsName, 1);
            if (statisticsVisit == null) {
                logger.info("statisticsVisit is null（对应的名称在 redis 中没有对应的数据）：" + statisticsVisit);
                // redis 中未命中则从数据库中获得
                statisticsVisit = visitStatisticsMapper.selectOneByExample(example).getVisitNum();

                statisticsVisit = redisToService.putVisitorNumOnRedis(VISIT, statisticsName, statisticsVisit + 1);
            }

            // 在session中保存该用户访问页面的记录，在一段时间内重复访问时不增加在页面的访问人次
            request.getSession().setAttribute(statisticsName, "yes");
        } else {
            statisticsVisit = redisToService.addVisitorNumOnRedis(VISIT, statisticsName, 0);
            if (statisticsVisit == null) {
                logger.info("statisticsVisit is null（对应的名称在 redis 中没有对应的数据）：" + statisticsVisit);
                //redis 中未命中则从数据库中获得
                statisticsVisit = visitStatisticsMapper.selectOneByExample(example).getVisitNum();

                statisticsVisit = redisToService.putVisitorNumOnRedis(VISIT, statisticsName, statisticsVisit);
            }
        }


        // 在 session 上找有没有 totalVisitors
        String total = (String) request.getSession().getAttribute(TOTAL_VISITORS);
        logger.info("session中的total值（在session中保存该用户访问页面的记录，在一段时间内重复访问时不增加在页面的访问人次）：" + total);
        Long totalVisitors;

        // 增加总访问人数
        if (total == null) {
            totalVisitors = redisToService.addVisitorNumOnRedis(VISIT, TOTAL_VISITORS, 1);
            if (totalVisitors == null) {
                logger.info("totalVisitors is null（totalVisitors 在 redis 中没有对应的数据）：" + totalVisitors);
                example.clear();
                example.selectProperties("visitNum");
                example.createCriteria().andEqualTo("statisticsName", "totalVisitors");
                totalVisitors = visitStatisticsMapper.selectOneByExample(example).getVisitNum();

                totalVisitors = redisToService.putVisitorNumOnRedis(VISIT, TOTAL_VISITORS, totalVisitors + 1);
            }
            request.getSession().setAttribute(TOTAL_VISITORS, "yes");
        } else {
            totalVisitors = redisToService.addVisitorNumOnRedis(VISIT, TOTAL_VISITORS, 0);
        }

        jsonObject.put(TOTAL_VISITORS, totalVisitors);
        jsonObject.put(STATISTICS_VISIT, statisticsVisit);
        return DataMap.success().setData(jsonObject);
    }

    @Override
    public long getNumByStatisticsName(String statisticsName) {
        Example example = new Example(VisitStatistics.class);
        example.selectProperties("visitNum");
        example.createCriteria().andEqualTo("statisticsName", statisticsName);
        VisitStatistics visitStatistics = visitStatisticsMapper.selectOneByExample(example);
        if (visitStatistics != null) {
            return visitStatistics.getVisitNum();
        }
        return 0;
    }

    @Override
    public void insertVisitArticlePage(String statisticsName) {
        VisitStatistics visitStatistics = new VisitStatistics();
        visitStatistics.setVisitNum(0L);
        visitStatistics.setStatisticsName(statisticsName);
        visitStatisticsMapper.insert(visitStatistics);
    }

    @Override
    public long getTotalVisit() {
        Example example = new Example(VisitStatistics.class);
        example.selectProperties("visitNum");
        example.createCriteria().andEqualTo("statisticsName", "totalVisitors");
        return visitStatisticsMapper.selectOneByExample(example).getVisitNum();
    }

    @Override
    public void updateVisitNumByStatisticsName(String statisticsName, long visitNum) {
        visitStatisticsMapper.updateVisitNumByStatisticsName(statisticsName, visitNum);
    }
}
