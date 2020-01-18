package com.cwz.blog.defaultblog.controller;

import com.cwz.blog.defaultblog.aspect.annotation.LogAnnotation;
import com.cwz.blog.defaultblog.aspect.annotation.PermissionCheck;
import com.cwz.blog.defaultblog.constant.CodeType;
import com.cwz.blog.defaultblog.entity.FeedBack;
import com.cwz.blog.defaultblog.entity.User;
import com.cwz.blog.defaultblog.service.*;
import com.cwz.blog.defaultblog.utils.DataMap;
import com.cwz.blog.defaultblog.utils.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author: 陈文振
 * @date: 2020/1/5
 * @description: 首页控制器
 */
@Api(tags = "首页控制器")
@RestController
public class IndexController {

    @Autowired
    private VisitStatisticsService visitStatisticsService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private TagsService tagsService;
    @Autowired
    private CategoriesService categoriesService;
    @Autowired
    private FeedBackService feedBackService;
    @Autowired
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @description: 增加访客量
     * @author: 陈文振
     * @date: 2020/1/5
     * @param request
     * @param statisticsName
     * @return: java.lang.String 网站总访问量以及访客量
     */
    @ApiOperation(value = "增加访客量")
    @LogAnnotation(module = "增加访客量", operation = "新增")
    @GetMapping(value = "/getVisitorNumByPageName", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    public String getVisitorNumByPageName(HttpServletRequest request,
                                          @RequestParam("statisticsName") String statisticsName){
        try {
            int index = statisticsName.indexOf("/");
            if (index == -1) {
                // 访客量
                statisticsName = "visitorVolume";
            }
            DataMap dataMap = visitStatisticsService.addVisitNumByStatisticsName(statisticsName, request);
            return JsonResult.build(dataMap).toJSON();
        } catch (Exception e){
            logger.info("需要统计访问量的统计页[{}]在统计过程中出现问题，统计失败", statisticsName, e);
        }
        return JsonResult.fail(CodeType.SERVER_EXCEPTION).toJSON();
    }

    /**
     * @description: 分页获得当前页已发布文章
     * @author: 陈文振
     * @date: 2020/1/5
     * @param rows
     * @param pageNum
     * @return: java.lang.String
     */
    @ApiOperation(value = "分页获得当前页已发布文章")
    @LogAnnotation(module = "分页获得当前页已发文章", operation = "查找")
    @PostMapping(value = "/myPublishArticles", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    public String myPublishArticles(@RequestParam("rows") String rows,
                             @RequestParam("pageNum") String pageNum){
        try {
            DataMap dataMap = articleService.findAllToPublishArticles(Integer.parseInt(rows), Integer.parseInt(pageNum));
            return JsonResult.build(dataMap).toJSON();
        } catch (Exception e){
            logger.info("获取当前页已发布文章出现错误", e);
        }
        return JsonResult.fail(CodeType.SERVER_EXCEPTION).toJSON();
    }

    /**
     * @description: 分页获得当前页草稿箱文章
     * @author: 陈文振
     * @date: 2020/1/5
     * @param rows
     * @param pageNum
     * @return: java.lang.String
     */
    @ApiOperation(value = "分页获得当前页草稿箱文章")
    @LogAnnotation(module = "分页获得当前页草稿箱文章", operation = "查找")
    @PostMapping(value = "/myDraftArticles", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    public String myDraftArticles(@RequestParam("rows") String rows, @RequestParam("pageNum") String pageNum,
                                  @AuthenticationPrincipal Principal principal){
        try {
            DataMap dataMap = articleService.findAllToDraftArticles(principal.getName(), Integer.parseInt(rows), Integer.parseInt(pageNum));
            return JsonResult.build(dataMap).toJSON();
        } catch (Exception e){
            logger.info("获取当前页草稿箱文章出现错误", e);
        }
        return JsonResult.fail(CodeType.SERVER_EXCEPTION).toJSON();
    }

    /**
     * @description: 获得最新评论
     * @author: 陈文振
     * @date: 2020/1/5
     * @param rows
     * @param pageNum
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得最新评论")
    @LogAnnotation(module = "获得最新评论", operation = "查找")
    @GetMapping(value = "/newComment", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    public String newComment(@RequestParam("rows") String rows,
                             @RequestParam("pageNum") String pageNum){

        DataMap dataMap = commentService.findFiveNewComment(Integer.parseInt(rows), Integer.parseInt(pageNum));
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 获得标签云
     * @author: 陈文振
     * @date: 2020/1/5
     * @param
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得标签云")
    @LogAnnotation(module = "获得标签云", operation = "查找")
    @GetMapping(value = "/findTagsCloud", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    public String findTagsCloud() {
        DataMap dataMap = tagsService.findAllTags();
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 获得右侧栏日志数、分类数、标签数
     * @author: 陈文振
     * @date: 2020/1/5
     * @param
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得右侧栏已发布文章数、分类数、标签数")
    @LogAnnotation(module = "获得右侧栏已发布文章数、分类数、标签数", operation = "查找")
    @GetMapping(value = "/findArticleCategoriesTagsNum", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    public String findArticleCategoriesTagsNum() {
        Map<String, Integer> dataMap = new HashMap<>(4);
        dataMap.put("tagsNum", tagsService.countTagsNum());
        dataMap.put("categoriesNum", categoriesService.countCategoriesNum());
        dataMap.put("articleNum", articleService.countArticleToPublish());
        return JsonResult.success().data(dataMap).toJSON();

    }

    /**
     * @description: 获得网站基本数据信息
     * @author: 陈文振
     * @date: 2020/1/5
     * @param
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得网站基本数据信息")
    @LogAnnotation(module = "获得网站基本数据信息", operation = "查找")
    @GetMapping(value = "/getSiteInfo", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    public String getSiteInfo(){
        Map<String, Integer> dataMap = new HashMap<>(4);
        dataMap.put("articleNum", articleService.countArticleToPublish());
        dataMap.put("tagsNum", tagsService.countTagsNum());
        dataMap.put("commentNum", commentService.commentNum());
        return JsonResult.success().data(dataMap).toJSON();
    }

    /**
     * @description: 提交反馈
     * @author: 陈文振
     * @date: 2020/1/5
     * @param feedBack
     * @param principal
     * @return: java.lang.String
     */
    @ApiOperation(value = "提交反馈")
    @LogAnnotation(module = "提交反馈", operation = "新增")
    @PostMapping(value = "/submitFeedback", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String submitFeedback(FeedBack feedBack,
                                 @AuthenticationPrincipal Principal principal){
        String username = principal.getName();
        feedBack.setUserId(userService.findIdByUsername(username));
        feedBackService.submitFeedback(feedBack);
        return JsonResult.success().toJSON();
    }

    /**
     * @description: 获取用户头像和个人简介信息
     * @author: 陈文振
     * @date: 2020/1/14
     * @param principal
     * @return: java.lang.String
     */
    @ApiOperation(value = "获取用户头像和个人简介信息")
    @LogAnnotation(module = "获取用户头像和个人简介信息", operation = "查询")
    @GetMapping(value = "/getUserIndexInfo", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String getUserIndexInfo(@AuthenticationPrincipal Principal principal){
        String username = principal.getName();
        User user = userService.findUserByUsername(username);
        Map<String, String> dataMap = new HashMap<>(3);
        dataMap.put("avatarImgUrl", user.getAvatarImgUrl());

        if (Objects.isNull(user.getPersonalBrief())) {
            dataMap.put("personalBrief", "null");
        } else {
            dataMap.put("personalBrief", user.getPersonalBrief());
        }

        return JsonResult.success().data(dataMap).toJSON();
    }
}
