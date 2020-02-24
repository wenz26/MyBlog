package com.cwz.blog.defaultblog.controller;

import com.cwz.blog.defaultblog.aspect.annotation.LogAnnotation;
import com.cwz.blog.defaultblog.aspect.annotation.PermissionCheck;
import com.cwz.blog.defaultblog.constant.CodeType;
import com.cwz.blog.defaultblog.constant.OSSClientConstants;
import com.cwz.blog.defaultblog.entity.User;
import com.cwz.blog.defaultblog.redis.HashRedisServiceImpl;
import com.cwz.blog.defaultblog.redis.RedisToService;
import com.cwz.blog.defaultblog.redis.StringRedisServiceImpl;
import com.cwz.blog.defaultblog.service.*;
import com.cwz.blog.defaultblog.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
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
import java.io.File;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * @author: 陈文振
 * @date: 2020/1/6
 * @description: 用户控制层
 */
@Api(tags = "用户控制层")
@RestController
public class UserController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ArticleLikesRecordService articleLikesRecordService;
    @Autowired
    private ArticleUserFavoriteRecordService articleUserFavoriteRecordService;
    @Autowired
    private CommentLikesRecordService commentLikesRecordService;
    @Autowired
    private UserAttentionService userAttentionService;
    @Autowired
    private HashRedisServiceImpl hashRedisService;
    @Autowired
    private RedisToService redisToService;

    private Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * @description: 用户获得已发布的文章 管理
     * @author: 陈文振
     * @date: 2020/1/6
     * @param rows
     * @param pageNum
     * @return: java.lang.String
     */
    @ApiOperation(value = "用户获得已发布的文章 管理")
    @LogAnnotation(module = "用户获得已发布的文章 管理", operation = "查找")
    @PostMapping(value = "/getPublishArticleManagementByUser", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String getPublishArticleManagementByUser(@RequestParam("rows") String rows,
                                                    @RequestParam("pageNum") String pageNum,
                                                    @AuthenticationPrincipal Principal principal,
                                                    @RequestParam(value = "articleTitle", required = false) String articleTitle,
                                                    @RequestParam(value = "articleType", required = false) String articleType,
                                                    @RequestParam(value = "articleCategory", required = false) String articleCategory,
                                                    @RequestParam(value = "firstDate", required = false) String firstDate,
                                                    @RequestParam(value = "lastDate", required = false) String lastDate){
        String username = principal.getName();
        int userId = userService.findIdByUsername(username);
        DataMap dataMap = articleService.getArticleManagement(Integer.parseInt(rows), Integer.parseInt(pageNum), userId, 1,
                articleTitle, articleType, articleCategory, firstDate, lastDate);
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 用户获得草稿箱的文章 管理
     * @author: 陈文振
     * @date: 2020/1/6
     * @param rows
     * @param pageNum
     * @return: java.lang.String
     */
    @ApiOperation(value = "用户获得草稿箱的文章 管理")
    @LogAnnotation(module = "用户获得草稿箱的文章 管理", operation = "查找")
    @PostMapping(value = "/getDraftArticleManagementByUser", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String getDraftArticleManagementByUser(@RequestParam("rows") String rows,
                                                  @RequestParam("pageNum") String pageNum,
                                                  @AuthenticationPrincipal Principal principal,
                                                  @RequestParam(value = "articleTitle", required = false) String articleTitle){
        String username = principal.getName();
        int userId = userService.findIdByUsername(username);
        DataMap dataMap = articleService.getArticleManagement(Integer.parseInt(rows), Integer.parseInt(pageNum), userId, 0,
                articleTitle, null, null, null, null);
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 删除文章
     * @author: 陈文振
     * @date: 2020/1/6
     * @param id
     * @return: java.lang.String
     */
    @ApiOperation(value = "删除文章")
    @LogAnnotation(module = "删除文章", operation = "删除")
    @GetMapping(value = "/deleteArticle", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String deleteArticle(@RequestParam("id") String id){
        if(StringUtils.isBlank(id)){
            return JsonResult.build(DataMap.fail(CodeType.DELETE_ARTICLE_FAIL)).toJSON();
        }
        DataMap dataMap = articleService.deleteArticle(Integer.parseInt(id));
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 获得文章点赞信息
     * @author: 陈文振
     * @date: 2020/1/6
     * @param rows
     * @param pageNum
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得文章点赞信息")
    @LogAnnotation(module = "获得文章点赞信息", operation = "查找")
    @PostMapping(value = "/getArticleThumbsUp", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String getArticleThumbsUp(@RequestParam("rows") String rows,
                                     @RequestParam("pageNum") String pageNum,
                                     @AuthenticationPrincipal Principal principal,
                                     @RequestParam(value = "articleThumbsUpRead", required = false) String articleThumbsUpRead,
                                     @RequestParam(value = "firstDate", required = false) String firstDate,
                                     @RequestParam(value = "lastDate", required = false) String lastDate){
        String username = principal.getName();

        Integer isRead;
        if (Objects.equals(articleThumbsUpRead, "choose")) {
            isRead = null;
        } else {
            isRead = Integer.parseInt(articleThumbsUpRead);
        }

        System.out.println(isRead);

        DataMap dataMap = articleLikesRecordService.getArticleThumbsUp(Integer.parseInt(rows), Integer.parseInt(pageNum), username,
                isRead, firstDate, lastDate);
        return JsonResult.build(dataMap).toJSON();
    }

    @ApiOperation(value = "获得评论点赞信息")
    @LogAnnotation(module = "获得评论点赞信息", operation = "查找")
    @PostMapping(value = "/getCommentThumbsUp", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String getCommentThumbsUp(@RequestParam("rows") String rows,
                                     @RequestParam("pageNum") String pageNum,
                                     @AuthenticationPrincipal Principal principal,
                                     @RequestParam(value = "commentThumbsUpRead", required = false) String commentThumbsUpRead,
                                     @RequestParam(value = "firstDate", required = false) String firstDate,
                                     @RequestParam(value = "lastDate", required = false) String lastDate){
        String username = principal.getName();

        Integer isRead;
        if (Objects.equals(commentThumbsUpRead, "choose")) {
            isRead = null;
        } else {
            isRead = Integer.parseInt(commentThumbsUpRead);
        }

        DataMap dataMap = commentLikesRecordService.getCommentThumbsUp(Integer.parseInt(rows), Integer.parseInt(pageNum), username,
                isRead, firstDate, lastDate);
        return JsonResult.build(dataMap).toJSON();
    }


    /**
     * @description: 已读一条文章点赞信息
     * @author: 陈文振
     * @date: 2020/1/6
     * @param id
     * @return: java.lang.String
     */
    @ApiOperation(value = "已读一条文章点赞信息")
    @LogAnnotation(module = "已读一条文章点赞信息", operation = "修改")
    @GetMapping(value = "/readThisArticleThumbsUp", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String readThisArticleThumbsUp(@RequestParam("id") int id){
        DataMap dataMap = articleLikesRecordService.readThisThumbsUp(id);
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 已读一条评论点赞信息
     * @author: 陈文振
     * @date: 2020/1/6
     * @param id
     * @return: java.lang.String
     */
    @ApiOperation(value = "已读一条评论点赞信息")
    @LogAnnotation(module = "已读一条评论点赞信息", operation = "修改")
    @GetMapping(value = "/readThisCommentThumbsUp", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String readThisCommentThumbsUp(@RequestParam("id") int id, @AuthenticationPrincipal Principal principal){
        DataMap dataMap = commentLikesRecordService.readThisThumbsUp(id, principal.getName());
        return JsonResult.build(dataMap).toJSON();
    }


    /**
     * @description: 已读全部文章点赞信息
     * @author: 陈文振
     * @date: 2020/1/6
     * @return: java.lang.String
     */
    @ApiOperation(value = "已读全部文章点赞信息")
    @LogAnnotation(module = "已读全部文章点赞信息", operation = "修改")
    @GetMapping(value = "/readAllArticleThumbsUp", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String readAllArticleThumbsUp(@AuthenticationPrincipal Principal principal){
        DataMap dataMap = articleLikesRecordService.readAllThumbsUp(principal.getName());
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 已读全部评论点赞信息
     * @author: 陈文振
     * @date: 2020/1/6
     * @return: java.lang.String
     */
    @ApiOperation(value = "已读全部评论点赞信息")
    @LogAnnotation(module = "已读全部评论点赞信息", operation = "修改")
    @GetMapping(value = "/readAllCommentThumbsUp", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String readAllCommentThumbsUp(@AuthenticationPrincipal Principal principal){
        DataMap dataMap = commentLikesRecordService.readAllThumbsUp(principal.getName());
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 获得用户的所有收藏文章
     * @author: 陈文振
     * @date: 2020/1/6
     * @param rows
     * @param pageNum
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得用户的所有收藏文章")
    @LogAnnotation(module = "获得用户的所有收藏文章", operation = "查找")
    @PostMapping(value = "/getFavoriteArticleByUser", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String getFavoriteArticleByUser(@RequestParam("rows") String rows,
                                           @RequestParam("pageNum") String pageNum,
                                           @AuthenticationPrincipal Principal principal,
                                           @RequestParam(value = "articleTitle", required = false) String articleTitle){
        String username = principal.getName();
        DataMap dataMap = articleUserFavoriteRecordService.findArticleFavoriteRecordByUsername(username, articleTitle, Integer.parseInt(rows), Integer.parseInt(pageNum));
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 用户取消某篇收藏文章
     * @author: 陈文振
     * @date: 2020/2/4
     * @param
     * @return: java.lang.String
     */
    @ApiOperation(value = "用户取消某篇收藏文章")
    @LogAnnotation(module = "用户取消某篇收藏文章", operation = "删除")
    @GetMapping(value = "/cancelFavoriteArticle", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String cancelFavoriteArticle(@RequestParam("id") String id) {
        DataMap dataMap = articleUserFavoriteRecordService.deleteArticleFavoriteRecordById(Integer.parseInt(id));
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 获得用户的所有点赞文章
     * @author: 陈文振
     * @date: 2020/1/6
     * @param rows
     * @param pageNum
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得用户的所有点赞文章")
    @LogAnnotation(module = "获得用户的所有点赞文章", operation = "查找")
    @PostMapping(value = "/getLikeArticleByUser", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String getLikeArticleByUser(@RequestParam("rows") String rows,
                                       @RequestParam("pageNum") String pageNum,
                                       @AuthenticationPrincipal Principal principal,
                                       @RequestParam(value = "articleTitle", required = false) String articleTitle){
        String username = principal.getName();
        DataMap dataMap = articleLikesRecordService.findArticleLikesRecordByUsername(username, articleTitle, Integer.parseInt(rows), Integer.parseInt(pageNum));
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 用户取消某篇点赞文章
     * @author: 陈文振
     * @date: 2020/2/4
     * @param
     * @return: java.lang.String
     */
    @ApiOperation(value = "用户取消某篇点赞文章")
    @LogAnnotation(module = "用户取消某篇点赞文章", operation = "删除")
    @GetMapping(value = "/cancelLikeArticle", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String cancelLikeArticle(@RequestParam("id") String id) {
        DataMap dataMap = articleLikesRecordService.deleteArticleLikesRecordById(Integer.parseInt(id));
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 上传头像
     * @author: 陈文振
     * @date: 2020/1/6
     * @param request
     * @param principal
     * @return: java.lang.String
     */
    @ApiOperation(value = "上传头像")
    @LogAnnotation(module = "上传头像", operation = "上传图片")
    @PostMapping(value = "/uploadHead", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String uploadHead(HttpServletRequest request,
                             @AuthenticationPrincipal Principal principal){

        String username = principal.getName();
        String phone = userService.findPhoneByUsername(username);
        String avatarImgUrl = request.getParameter("avatarImgUrl");

        //获得上传文件的后缀名
        int index = avatarImgUrl.indexOf(";base64,");
        String strFileExtendName = "." + avatarImgUrl.substring(11,index);
        avatarImgUrl = avatarImgUrl.substring(index + 8);

        try {
            FileUtil fileUtil = new FileUtil();

            String filePath = request.getSession().getServletContext().getRealPath("") + "upload";
            logger.info("本地地址为：" + filePath);

            Date date = new Date();
            String fileName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(date);
            String day = new SimpleDateFormat("yyyy-MM-dd").format(date);

            File file = fileUtil.base64ToFile(filePath, avatarImgUrl, fileName + strFileExtendName);
            logger.info("用户头像图片的本地存储路径为：" + filePath);

            String fileUrl = fileUtil.uploadFile(file, OSSClientConstants.USER_IMAGE + phone + "/" + day + "/");

            logger.info("用户[{}]上传头像图片成功！！！获得的url为：" + fileUrl, username);

            int userId = userService.findIdByUsername(username);
            userService.updateAvatarImgUrlById(fileUrl, userId);

            DataMap dataMap = userService.getHeadPortraitUrl(userId);
            return JsonResult.build(dataMap).toJSON();
        } catch (Exception e){
            e.printStackTrace();
            logger.error("用户[{}]更改头像失败", username, e);
            return JsonResult.fail(CodeType.MODIFY_HEAD_PORTRAIT_FAIL).toJSON();
        }
    }

    /**
     * @description: 获得个人资料
     * @author: 陈文振
     * @date: 2020/1/6
     * @param principal
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得个人资料")
    @LogAnnotation(module = "获得个人资料", operation = "查找")
    @PostMapping(value = "/getUserPersonalInfo", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String getUserPersonalInfo(@AuthenticationPrincipal Principal principal){
        String username = principal.getName();
        DataMap dataMap = userService.getUserPersonalInfoByUsername(username);
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 保存个人资料
     * @author: 陈文振
     * @date: 2020/1/6
     * @param user
     * @param principal
     * @return: java.lang.String
     */
    @ApiOperation(value = "保存个人资料")
    @LogAnnotation(module = "保存个人资料", operation = "修改")
    @PostMapping(value = "/savePersonalDate", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String savePersonalDate(User user, @AuthenticationPrincipal Principal principal){

        String username = principal.getName();
        DataMap dataMap = userService.savePersonalData(user, username);
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 获得该用户曾今的所有评论
     * @author: 陈文振
     * @date: 2020/1/6
     * @param rows
     * @param pageNum
     * @param principal
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得该用户曾今的所有评论")
    @LogAnnotation(module = "获得该用户曾今的所有评论", operation = "查找")
    @PostMapping(value = "/getUserComment", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String getUserComment(@RequestParam("rows") String rows,
                                 @RequestParam("pageNum") String pageNum,
                                 @AuthenticationPrincipal Principal principal,
                                 @RequestParam(value = "commentMsgRead", required = false) String commentMsgRead,
                                 @RequestParam(value = "firstDate", required = false) String firstDate,
                                 @RequestParam(value = "lastDate", required = false) String lastDate){
        String username = principal.getName();

        Integer isRead;
        if (Objects.equals(commentMsgRead, "choose")) {
            isRead = null;
        } else {
            isRead = Integer.parseInt(commentMsgRead);
        }
        DataMap dataMap = commentService.getUserComment(Integer.parseInt(rows), Integer.parseInt(pageNum), username,
                isRead, firstDate, lastDate);
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 获得用户的所有评论信息
     * @author: 陈文振
     * @date: 2020/1/21
     * @param rows
     * @param pageNum
     * @param principal
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得用户的所有评论信息")
    @LogAnnotation(module = "获得用户的所有评论信息", operation = "查找")
    @PostMapping(value = "/findAllCommentByUser", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String findAllCommentByUser(@RequestParam("rows") String rows,
                                       @RequestParam("pageNum") String pageNum,
                                       @AuthenticationPrincipal Principal principal,
                                       @RequestParam(value = "articleTitle", required = false) String articleTitle,
                                       @RequestParam(value = "commentContent", required = false) String commentContent,
                                       @RequestParam(value = "firstDate", required = false) String firstDate,
                                       @RequestParam(value = "lastDate", required = false) String lastDate,
                                       @RequestParam(value = "searchUsername", required = false) String searchUsername){
        DataMap dataMap = commentService.findAllComment(Integer.parseInt(rows), Integer.parseInt(pageNum), principal.getName(),
                articleTitle, commentContent, firstDate, lastDate, searchUsername);
        return JsonResult.build(dataMap).toJSON();
    }

    @ApiOperation(value = "删除用户的某条评论")
    @LogAnnotation(module = "删除用户的某条评论", operation = "删除")
    @GetMapping(value = "/deleteCommentByUser", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String deleteCommentByUser(@RequestParam("id") String id){
        if(StringUtils.isBlank(id)){
            return JsonResult.build(DataMap.fail(CodeType.DELETE_COMMENT_FAIL)).toJSON();
        }
        DataMap dataMap = commentService.deleteOneCommentById(Integer.parseInt(id));
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 已读一条消息
     * @author: 陈文振
     * @date: 2020/1/6
     * @param id
     * @param msgType
     * @param principal
     * @return: java.lang.String
     * msgType == 1 为读评论   2 为文章点赞记录   3 为评论点赞记录
     */
    @ApiOperation(value = "已读一条消息")
    @LogAnnotation(module = "已读一条消息", operation = "读取信息")
    @GetMapping(value = "/readThisMsg", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String readThisMsg(@RequestParam("id") int id,
                              @RequestParam("msgType") int msgType,
                              @AuthenticationPrincipal Principal principal){

        redisToService.readOneMsgOnRedis(userService.findIdByUsername(principal.getName()), msgType);
        if(msgType == 1){
            return JsonResult.build(commentService.readOneCommentRecord(id)).toJSON();
        } /*else {
            return JsonResult.build(leaveMessageService.readOneLeaveMessageRecord(id)).toJSON();
        }*/
        return JsonResult.fail().toJSON();
    }

    /**
     * @description: 已读所有消息
     * @author: 陈文振
     * @date: 2020/1/6
     * @param msgType
     * @param principal
     * @return: java.lang.String
     */
    @ApiOperation(value = "已读所有消息")
    @LogAnnotation(module = "已读所有消息", operation = "读取信息")
    @GetMapping(value = "/readAllMsg", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String readAllMsg(@RequestParam("msgType") int msgType,
                             @AuthenticationPrincipal Principal principal){
        String username = principal.getName();

        // msgType == 1 为读评论
        redisToService.readAllMsgOnRedis(userService.findIdByUsername(username), msgType);
        if(msgType == 1){
            return JsonResult.build(commentService.readAllComment(username)).toJSON();
        } /*else {
            return JsonResult.build(leaveMessageService.readAllLeaveMessage(username)).toJSON();
        }*/
        return JsonResult.fail().toJSON();
    }

    /**
     * @description: 获得用户未读消息
     * @author: 陈文振
     * @date: 2020/1/6
     * @param principal
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得用户未读消息")
    @LogAnnotation(module = "获得用户未读消息", operation = "查找")
    @PostMapping(value = "/getUserNews", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String getUserNews(@AuthenticationPrincipal Principal principal){
        String username = principal.getName();
        DataMap dataMap = redisToService.getUserNews(username);
        return JsonResult.build(dataMap).toJSON();
    }


    /**
     * @description:
     * @author: 陈文振
     * @date: 2020/2/6
     * @param inquireName: 要查询的名字
     * @param principal
     * @param rows
     * @param pageNum
     * @param type: 1 为获得我的关注， 2 为获得我的粉丝
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得我的关注 或 我的粉丝")
    @LogAnnotation(module = "获得我的关注 或 我的粉丝", operation = "查找")
    @PostMapping(value = "/getUserUserAttention", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String getUserUserAttention(@RequestParam("inquireName") String inquireName,
                                       @AuthenticationPrincipal Principal principal,
                                       @RequestParam("rows") String rows,
                                       @RequestParam("pageNum") String pageNum,
                                       @RequestParam("type") int type){
        String username = principal.getName();
        DataMap dataMap = userAttentionService.getUserUserAttention(username, inquireName, type,
                Integer.parseInt(rows), Integer.parseInt(pageNum));
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 取消关注
     * @author: 陈文振
     * @date: 2020/2/6
     * @param attentionId
     * @return: java.lang.String
     */
    @ApiOperation(value = "取消关注")
    @LogAnnotation(module = "取消关注", operation = "删除")
    @GetMapping(value = "/deleteUserAttention", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String deleteUserAttention(@RequestParam("attentionId") String attentionId){
        DataMap dataMap = userAttentionService.deleteUserAttention(Integer.parseInt(attentionId));
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 获得关注 或 粉丝
     * @author: 陈文振
     * @date: 2020/2/6
     * @param username
     * @param rows
     * @param pageNum
     * @param type: 1 为获得我的关注， 2 为获得我的粉丝
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得关注 或 粉丝")
    @LogAnnotation(module = "获得关注 或 粉丝", operation = "查找")
    @PostMapping(value = "/getSomeOneAttention", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    public String getSomeOneAttention(@RequestParam("username") String username,
                                      @AuthenticationPrincipal Principal principal,
                                      @RequestParam("rows") String rows,
                                      @RequestParam("pageNum") String pageNum,
                                      @RequestParam("type") int type){

        if (!Objects.equals(username, StringUtil.BLANK)) {
            username = TransCodingUtil.unicodeToString(username);
        }

        String myName;
        if (principal == null) {
            myName = null;
        } else {
            myName = principal.getName();
        }

        DataMap dataMap = userAttentionService.getSomeOneAttention(username, myName, type,
                Integer.parseInt(rows), Integer.parseInt(pageNum));
        return JsonResult.build(dataMap).toJSON();
    }
}
