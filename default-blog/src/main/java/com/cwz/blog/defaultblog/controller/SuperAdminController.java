package com.cwz.blog.defaultblog.controller;

import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.aspect.annotation.LogAnnotation;
import com.cwz.blog.defaultblog.aspect.annotation.PermissionCheck;
import com.cwz.blog.defaultblog.constant.CodeType;
import com.cwz.blog.defaultblog.entity.Categories;
import com.cwz.blog.defaultblog.entity.Tags;
import com.cwz.blog.defaultblog.entity.UserLog;
import com.cwz.blog.defaultblog.redis.HashRedisServiceImpl;
import com.cwz.blog.defaultblog.redis.RedisToService;
import com.cwz.blog.defaultblog.redis.StringRedisServiceImpl;
import com.cwz.blog.defaultblog.service.*;
import com.cwz.blog.defaultblog.utils.DataMap;
import com.cwz.blog.defaultblog.utils.JsonResult;
import com.cwz.blog.defaultblog.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Objects;

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
    private CommentService commentService;
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
    @Autowired
    private UserLogService userLogService;
    @Autowired
    private CodeTypesService codeTypesService;
    @Autowired
    private StringRedisServiceImpl stringRedisService;


    /**
     * @description: 分页获得所有反馈信息
     * @author: 陈文振
     * @date: 2020/1/6
     * @param rows
     * @param pageNum
     * @return: java.lang.String
     */
    @ApiOperation(value = "分页获得所有反馈信息")
    @LogAnnotation(module = "分页获得所有反馈信息", operation = "管理员查找")
    @GetMapping(value = "/getAllFeedback", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_SUPERADMIN")
    public String getAllFeedback(@RequestParam("rows") String rows,
                                 @RequestParam("pageNum") String pageNum,
                                 @RequestParam(value = "feedBackRead", required = false) String feedBackRead,
                                 @RequestParam(value = "firstDate", required = false) String firstDate,
                                 @RequestParam(value = "lastDate", required = false) String lastDate){

        Integer isRead;
        if (Objects.equals(feedBackRead, "choose")) {
            isRead = null;
        } else {
            isRead = Integer.parseInt(feedBackRead);
        }

        DataMap dataMap = feedBackService.getAllFeedback(Integer.parseInt(rows), Integer.parseInt(pageNum),
                isRead, firstDate, lastDate);
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 已读一条反馈信息
     * @author: 陈文振
     * @date: 2020/2/2
     * @param id
     * @return: java.lang.String
     */
    @ApiOperation(value = "已读一条反馈信息")
    @LogAnnotation(module = "已读一条反馈信息", operation = "管理员修改")
    @GetMapping(value = "/readOneFeedBackRecord", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_SUPERADMIN")
    public String readOneFeedBackRecord(@RequestParam("id") int id){
        DataMap dataMap = feedBackService.readOneFeedBackRecord(id);
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 已读全部反馈信息
     * @author: 陈文振
     * @date: 2020/2/2
     * @param
     * @return: java.lang.String
     */
    @ApiOperation(value = "已读全部反馈信息")
    @LogAnnotation(module = "已读全部反馈信息", operation = "管理员修改")
    @GetMapping(value = "/readAllFeedBack", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_SUPERADMIN")
    public String readAllFeedBack(){
        DataMap dataMap = feedBackService.readAllFeedBack();
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 获取管理员未读信息
     * @author: 陈文振
     * @date: 2020/2/2
     * @param
     * @return: java.lang.String
     */
    @ApiOperation(value = "获取管理员未读信息")
    @LogAnnotation(module = "获取管理员未读信息", operation = "管理员查找")
    @GetMapping(value = "/getSuperAdminMsg", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_SUPERADMIN")
    public String getSuperAdminMsg(){
        JSONObject jsonObject = new JSONObject();

        if (stringRedisService.hasKey(StringUtil.FEEDBACK_MSG)) {
            jsonObject.put("result", stringRedisService.get(StringUtil.FEEDBACK_MSG));
        } else {
            jsonObject.put("result", 0);
        }

        return JsonResult.build(DataMap.success().setData(jsonObject)).toJSON();
    }

    /**
     * @description: 获得统计信息
     * @author: 陈文振
     * @date: 2020/1/6
     * @param
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得统计信息")
    @LogAnnotation(module = "获得统计信息", operation = "管理员查找")
    @GetMapping(value = "/getStatisticsInfo", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_SUPERADMIN")
    public String getStatisticsInfo(){
        JSONObject returnJson = new JSONObject();
        String visit = StringUtil.VISIT;
        Long totalVisitors = redisToService.getVisitorNumOnRedis(visit, "totalVisitors");
        Long yesterdayVisitors = redisToService.getVisitorNumOnRedis(visit, "yesterdayVisitors");
        Long visitorVolume = redisToService.getVisitorNumOnRedis(visit, "visitorVolume");

        returnJson.put("allVisitor", totalVisitors);
        returnJson.put("allUser", userService.countUserNum());
        returnJson.put("yesterdayVisitor", yesterdayVisitors);
        returnJson.put("visitorVolume", visitorVolume);
        returnJson.put("articleNum", articleService.countArticleToPublish());
        returnJson.put("tagsNum", tagsService.countTagsNum());
        returnJson.put("categoriesNum", categoriesService.countCategoriesNum());
        returnJson.put("commentNum", commentService.commentNum());

        returnJson.put("MONDAY", redisToService.getVisitorNumOnRedis(visit, "MONDAY"));
        returnJson.put("TUESDAY", redisToService.getVisitorNumOnRedis(visit, "TUESDAY"));
        returnJson.put("WEDNESDAY", redisToService.getVisitorNumOnRedis(visit, "WEDNESDAY"));
        returnJson.put("THURSDAY", redisToService.getVisitorNumOnRedis(visit, "THURSDAY"));
        returnJson.put("FRIDAY", redisToService.getVisitorNumOnRedis(visit, "FRIDAY"));
        returnJson.put("SATURDAY", redisToService.getVisitorNumOnRedis(visit, "SATURDAY"));
        returnJson.put("SUNDAY", redisToService.getVisitorNumOnRedis(visit, "SUNDAY"));
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
    @LogAnnotation(module = "获得文章管理", operation = "管理员查找")
    @PostMapping(value = "/getArticleManagement", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_SUPERADMIN")
    public String getArticleManagement(@RequestParam("rows") String rows,
                                       @RequestParam("pageNum") String pageNum,
                                       @RequestParam(value = "articleTitle", required = false) String articleTitle,
                                       @RequestParam(value = "articleType", required = false) String articleType,
                                       @RequestParam(value = "articleCategory", required = false) String articleCategory,
                                       @RequestParam(value = "firstDate", required = false) String firstDate,
                                       @RequestParam(value = "lastDate", required = false) String lastDate,
                                       @RequestParam(value = "draft", required = false) String draft){

        Integer draftToInt;
        if (Objects.equals(draft, "choose")) {
            draftToInt = null;
        } else {
            draftToInt = Integer.parseInt(draft);
        }

        DataMap dataMap = articleService.getArticleManagement(Integer.parseInt(rows), Integer.parseInt(pageNum), null, draftToInt,
                articleTitle, articleType, articleCategory, firstDate, lastDate);
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
    @LogAnnotation(module = "获得所有分类信息", operation = "管理员查找")
    @GetMapping(value = "/getArticleCategories", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_SUPERADMIN")
    public String getArticleCategories(@RequestParam("rows") String rows,
                                       @RequestParam("pageNum") String pageNum,
                                       @RequestParam(value = "categorySearch", required = false) String categorySearch,
                                       @RequestParam(value = "firstDate", required = false) String firstDate,
                                       @RequestParam(value = "lastDate", required = false) String lastDate){
        DataMap dataMap = categoriesService.findAllCategories(Integer.parseInt(rows), Integer.parseInt(pageNum),
                categorySearch, firstDate, lastDate);
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 获得某个分类信息
     * @author: 陈文振
     * @date: 2020/1/6
     * @param
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得某个分类信息")
    @LogAnnotation(module = "获得某个分类信息", operation = "管理员查找")
    @GetMapping(value = "/findOneCategory", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_SUPERADMIN")
    public String findOneCategory(@RequestParam("id") String id){
        Categories category = categoriesService.findCategoryByCategoryId(Integer.parseInt(id));
        return JsonResult.build(DataMap.success().setData(category)).toJSON();
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
    @LogAnnotation(module = "添加或删除分类", operation = "管理员新增/删除")
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
    @LogAnnotation(module = "获得所有标签信息", operation = "管理员查找")
    @GetMapping(value = "/getArticleTags", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_SUPERADMIN")
    public String getArticleTags(@RequestParam("rows") String rows,
                                 @RequestParam("pageNum") String pageNum,
                                 @RequestParam(value = "tagSearch", required = false) String tagSearch,
                                 @RequestParam(value = "firstDate", required = false) String firstDate,
                                 @RequestParam(value = "lastDate", required = false) String lastDate){
        DataMap dataMap = tagsService.findTagsInfoAndArticleCountNum(Integer.parseInt(rows), Integer.parseInt(pageNum),
                tagSearch, firstDate, lastDate);
        return JsonResult.build(dataMap).toJSON();
    }

    @ApiOperation(value = "删除标签")
    @LogAnnotation(module = "删除标签", operation = "管理员删除")
    @PostMapping(value = "/deleteTags", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_SUPERADMIN")
    public String deleteTags(@RequestParam("tagName") String tagName,
                             @RequestParam("type") int type){
        // type的值： 1--增加标签  2--删除标签
        DataMap dataMap = tagsService.updateTags(tagName, type);
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 获得所有评论信息
     * @author: 陈文振
     * @date: 2020/1/12
     * @param rows
     * @param pageNum
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得所有评论信息")
    @LogAnnotation(module = "获得所有评论信息", operation = "管理员查找")
    @GetMapping(value = "/findAllComment", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_SUPERADMIN")
    public String findAllComment(@RequestParam("rows") String rows,
                                 @RequestParam("pageNum") String pageNum,
                                 @RequestParam(value = "articleTitle", required = false) String articleTitle,
                                 @RequestParam(value = "commentContent", required = false) String commentContent,
                                 @RequestParam(value = "firstDate", required = false) String firstDate,
                                 @RequestParam(value = "lastDate", required = false) String lastDate,
                                 @RequestParam(value = "searchUsername", required = false) String searchUsername){
        DataMap dataMap = commentService.findAllComment(Integer.parseInt(rows), Integer.parseInt(pageNum), null,
                articleTitle, commentContent, firstDate, lastDate, searchUsername);
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 删除评论
     * @author: 陈文振
     * @date: 2020/1/12
     * @param id
     * @return: java.lang.String
     */
    @ApiOperation(value = "删除评论")
    @LogAnnotation(module = "删除评论", operation = "管理员查找")
    @GetMapping(value = "/deleteOneCommentById", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_SUPERADMIN")
    public String deleteOneCommentById(@RequestParam("id") String id){
        if(StringUtils.isBlank(id)){
            return JsonResult.build(DataMap.fail(CodeType.DELETE_COMMENT_FAIL)).toJSON();
        }
        DataMap dataMap = commentService.deleteOneCommentById(Integer.parseInt(id));
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 获得网站所有用户
     * @author: 陈文振
     * @date: 2020/1/29
     * @param rows
     * @param pageNum
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得网站所有用户")
    @LogAnnotation(module = "获得网站所有用户", operation = "管理员查找")
    @GetMapping(value = "/findAllUser", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_SUPERADMIN")
    public String findAllUser(@RequestParam("rows") String rows,
                              @RequestParam("pageNum") String pageNum,
                              @RequestParam(value = "phoneSearch", required = false) String phoneSearch,
                              @RequestParam(value = "usernameSearch", required = false) String usernameSearch,
                              @RequestParam(value = "genderSearch", required = false) String genderSearch,
                              @RequestParam(value = "firstDate", required = false) String firstDate,
                              @RequestParam(value = "lastDate", required = false) String lastDate){

        if (Objects.equals(genderSearch, "choose")) {
            genderSearch = null;
        }

        DataMap dataMap = userService.findAllUser(Integer.parseInt(rows), Integer.parseInt(pageNum), phoneSearch,
                usernameSearch, genderSearch, firstDate, lastDate);
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 获取用户日志
     * @author: 陈文振
     * @date: 2020/1/29
     * @param rows
     * @param pageNum
     * @return: java.lang.String
     */
    @ApiOperation(value = "获取用户日志")
    @LogAnnotation(module = "获取用户日志", operation = "管理员查找")
    @PostMapping(value = "/getAllUserLog", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_SUPERADMIN")
    public String getAllUserLog(@RequestParam("rows") String rows,
                                @RequestParam("pageNum") String pageNum, UserLog userLog,
                                @RequestParam("type") int type,
                                @RequestParam(value = "firstDate", required = false) String firstDate,
                                @RequestParam(value = "lastDate", required = false) String lastDate){
        // 1 为用户登录日志, 2为用户操作日志, 3为自定义日志查询
        DataMap dataMap = userLogService.getAllUserLog(Integer.parseInt(rows), Integer.parseInt(pageNum), userLog, type,
                firstDate, lastDate);
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 获取某条用户日志信息
     * @author: 陈文振
     * @date: 2020/1/31
     * @return: java.lang.String
     */
    @ApiOperation(value = "获取某条用户日志信息")
    @LogAnnotation(module = "获取某条用户日志信息", operation = "管理员查找")
    @GetMapping(value = "/getUserLogOneById", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_SUPERADMIN")
    public String getUserLogOneById(@RequestParam("id") String id){
        DataMap dataMap = userLogService.getUserLogOneById(Integer.parseInt(id));
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 获取状态码帮助页
     * @author: 陈文振
     * @date: 2020/2/1
     * @param rows
     * @param pageNum
     * @param code
     * @return: java.lang.String
     */
    @ApiOperation(value = "获取状态码帮助页")
    @LogAnnotation(module = "获取状态码帮助页", operation = "管理员查找")
    @GetMapping(value = "/selectSomeCodeTypes", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_SUPERADMIN")
    public String selectSomeCodeTypes(@RequestParam("rows") String rows,
                                      @RequestParam("pageNum") String pageNum,
                                      @RequestParam(value = "code", required = false) String code){
        DataMap dataMap = codeTypesService.selectSomeCodeTypes(Integer.parseInt(rows), Integer.parseInt(pageNum), code);
        return JsonResult.build(dataMap).toJSON();
    }
}
