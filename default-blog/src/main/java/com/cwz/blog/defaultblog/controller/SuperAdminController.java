package com.cwz.blog.defaultblog.controller;

import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.aspect.annotation.LogAnnotation;
import com.cwz.blog.defaultblog.aspect.annotation.PermissionCheck;
import com.cwz.blog.defaultblog.entity.Categories;
import com.cwz.blog.defaultblog.redis.HashRedisServiceImpl;
import com.cwz.blog.defaultblog.redis.RedisToService;
import com.cwz.blog.defaultblog.service.*;
import com.cwz.blog.defaultblog.utils.DataMap;
import com.cwz.blog.defaultblog.utils.JsonResult;
import com.cwz.blog.defaultblog.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;

/**
 * @author: 陈文振
 * @date: 2020/1/6
 * @description: 超级管理员控制层
 */
@Api(tags = "超级管理员控制层")
@RestController
public class SuperAdminController {

    @Autowired
    private FeedBackService feedBackService;
    @Autowired
    private VisitStatisticsService visitStatisticsService;
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private TagsService tagsService;
    @Autowired
    private HashRedisServiceImpl hashRedisService;
    @Autowired
    private CategoriesService categoriesService;
    @Autowired
    private RedisToService redisToService;


    /**
     * @description: 分页获得所有反馈信息
     * @author: 陈文振
     * @date: 2020/1/6
     * @param rows
     * @param pageNum
     * @return: java.lang.String
     */
    @ApiOperation(value = "分页获得所有反馈信息")
    @LogAnnotation(module = "分页获得所有反馈信息", operation = "查找")
    @GetMapping(value = "/getAllFeedback", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_SUPERADMIN")
    public String getAllFeedback(@RequestParam("rows") String rows,
                                 @RequestParam("pageNum") String pageNum){
        DataMap dataMap = feedBackService.getAllFeedback(Integer.parseInt(rows), Integer.parseInt(pageNum));
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 获得统计信息
     * @author: 陈文振
     * @date: 2020/1/6
     * @param
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得统计信息")
    @LogAnnotation(module = "获得统计信息", operation = "查找")
    @GetMapping(value = "/getStatisticsInfo", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_SUPERADMIN")
    public String getStatisticsInfo(){
        JSONObject returnJson = new JSONObject();
        Long totalVisitors = redisToService.getVisitorNumOnRedis("visit", "totalVisitors");
        Long yesterdayVisitors = redisToService.getVisitorNumOnRedis("visit", "yesterdayVisitors");

        returnJson.put("allVisitor", totalVisitors);
        returnJson.put("allUser", userService.countUserNum());
        returnJson.put("yesterdayVisitor", yesterdayVisitors);
        returnJson.put("articleNum", articleService.countArticleToPublish());

        if (hashRedisService.hasKey(StringUtil.ARTICLE_THUMBS_UP)) {
            // 将redis中的所有文章的点赞记录显示出来
            LinkedHashMap map = (LinkedHashMap) hashRedisService.getAllFieldAndValue(StringUtil.ARTICLE_THUMBS_UP);
            String statisticsName;
            Integer thumbsUpNum;
            Integer statisticsNum = 0;

            for (Object e : map.keySet()) {
                statisticsName = String.valueOf(e);
                thumbsUpNum = (Integer) map.get(statisticsName);
                returnJson.put("thumbsUpNumByArticle_" + statisticsName, thumbsUpNum);
                statisticsNum = statisticsNum + thumbsUpNum;
            }
            returnJson.put("articleThumbsUpNum", statisticsNum);
        } else {
            returnJson.put("articleThumbsUpNum", 0);
        }
        DataMap dataMap = DataMap.success().setData(returnJson);
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 获得文章管理
     * @author: 陈文振
     * @date: 2020/1/6
     * @param rows
     * @param pageNum
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得文章管理")
    @LogAnnotation(module = "获得文章管理", operation = "查找")
    @PostMapping(value = "/getArticleManagement", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_SUPERADMIN")
    public String getArticleManagement(@RequestParam("rows") String rows,
                                       @RequestParam("pageNum") String pageNum){
        DataMap dataMap = articleService.getArticleManagement(Integer.parseInt(rows), Integer.parseInt(pageNum), null, null);
        return JsonResult.build(dataMap).toJSON();

    }

    /**
     * @description: 获得所有分类信息
     * @author: 陈文振
     * @date: 2020/1/6
     * @param
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得所有分类信息")
    @LogAnnotation(module = "获得所有分类信息", operation = "查找")
    @GetMapping(value = "/getArticleCategories", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_SUPERADMIN")
    public String getArticleCategories(@RequestParam("rows") String rows,
                                       @RequestParam("pageNum") String pageNum){
        DataMap dataMap = categoriesService.findAllCategories(Integer.parseInt(rows), Integer.parseInt(pageNum));
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 添加或删除分类
     * @author: 陈文振
     * @date: 2020/1/6
     * @param categories
     * @param type
     * @return: java.lang.String
     */
    @ApiOperation(value = "添加或删除分类")
    @LogAnnotation(module = "添加或删除分类", operation = "新增/删除")
    @PostMapping(value = "/updateCategory", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_SUPERADMIN")
    public String updateCategory(Categories categories,
                                 @RequestParam("type") int type){
        // type的值： 1--增加分类   2--删除分类   3--修改分类（分类名不能修改，只能修改分类具体描述）
        DataMap dataMap = categoriesService.updateCategory(categories, type);
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 获得所有标签信息
     * @author: 陈文振
     * @date: 2020/1/6
     * @param
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得所有标签信息")
    @LogAnnotation(module = "获得所有标签信息", operation = "查找")
    @GetMapping(value = "/getArticleTags", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_SUPERADMIN")
    public String getArticleTags(@RequestParam("rows") String rows,
                                 @RequestParam("pageNum") String pageNum){
        DataMap dataMap = tagsService.findTagsInfoAndArticleCountNum(Integer.parseInt(rows), Integer.parseInt(pageNum));
        return JsonResult.build(dataMap).toJSON();
    }
}
