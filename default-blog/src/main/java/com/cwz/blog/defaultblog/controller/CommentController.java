package com.cwz.blog.defaultblog.controller;

import com.cwz.blog.defaultblog.aspect.annotation.LogAnnotation;
import com.cwz.blog.defaultblog.aspect.annotation.PermissionCheck;
import com.cwz.blog.defaultblog.component.JavaScriptCheck;
import com.cwz.blog.defaultblog.constant.CodeType;
import com.cwz.blog.defaultblog.entity.Comment;
import com.cwz.blog.defaultblog.entity.CommentLikesRecord;
import com.cwz.blog.defaultblog.mapper.ArticleMapper;
import com.cwz.blog.defaultblog.mapper.CommentMapper;
import com.cwz.blog.defaultblog.redis.RedisToService;
import com.cwz.blog.defaultblog.service.CommentLikesRecordService;
import com.cwz.blog.defaultblog.service.CommentService;
import com.cwz.blog.defaultblog.service.UserService;
import com.cwz.blog.defaultblog.utils.DataMap;
import com.cwz.blog.defaultblog.utils.JsonResult;
import com.cwz.blog.defaultblog.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author: 陈文振
 * @date: 2019/12/28
 * @description: 评论和回复
 */
@Api(tags = "评论和回复")
@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentLikesRecordService commentLikesRecordService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private RedisToService redisToService;

    /**
     * @description: 获得该文章所有评论
     * @author: 陈文振
     * @date: 2020/1/4
     * @param articleId
     * @param principal
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得该文章所有评论")
    @LogAnnotation(module = "获得文章所有评论信息", operation = "查找")
    @PostMapping(value = "/getAllComment", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    public String getAllComment(@RequestParam("articleId") int articleId,
                                @AuthenticationPrincipal Principal principal) {
        String username = null;
        if (principal != null) {
            username = principal.getName();
        }
        DataMap dataMap = commentService.findCommentByArticleId(articleId, username);
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 发布评论
     * @author: 陈文振
     * @date: 2020/1/4
     * @param comment
     * @param principal
     * @return: java.lang.String
     */
    @ApiOperation(value = "发布评论信息")
    @LogAnnotation(module = "发布评论信息", operation = "新增")
    @PostMapping(value = "/publishComment", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String publishComment(Comment comment,
                                 @AuthenticationPrincipal Principal principal){

        String publisher = principal.getName();
        LocalDateTime localDateTime = LocalDateTime.now();
        comment.setCommentDate(localDateTime);

        // 如果是首评论不是回复的话，评论id和被回复id都是本人
        int userId = userService.findIdByUsername(publisher);
        comment.setAnswererId(userId);
        comment.setRespondentId(articleMapper.findUserIdByArticleId(comment.getArticleId()));

        comment.setCommentContent(JavaScriptCheck.javaScriptCheck(comment.getCommentContent()));

        commentService.insertComment(comment);
        DataMap dataMap = commentService.findCommentByArticleId(comment.getArticleId(), publisher);
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 评论中的回复
     * @author: 陈文振
     * @date: 2020/1/4
     * @param comment
     * @param parentId
     * @param respondent
     * @param principal
     * @return: java.lang.String
     */
    @ApiOperation(value = "发布评论中的回复信息")
    @LogAnnotation(module = "发布评论中的回复信息", operation = "新增")
    @PostMapping(value = "/publishReply", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String publishReply(Comment comment,
                               @RequestParam("parentId") String parentId,
                               @RequestParam("respondent") String respondent,
                               @AuthenticationPrincipal Principal principal){
        String username = principal.getName();

        // 这里前端传过来的parentId 是动态生成的id （P数字）
        comment.setpId(Integer.parseInt(parentId.substring(1)));
        comment.setAnswererId(userService.findIdByUsername(username));

        // respondent 是动态生成的值 （id或者名字），这里判断respondent能否转为数字
        if (NumberUtils.isCreatable(respondent)) {
            comment.setRespondentId(Integer.parseInt(respondent));
        } else {
            comment.setRespondentId(userService.findIdByUsername(respondent));
        }

        LocalDateTime localDateTime = LocalDateTime.now();
        comment.setCommentDate(localDateTime);

        String commentContent = comment.getCommentContent();
        // 去掉评论中的@who
        if (Objects.equals('@', commentContent.charAt(0))) {
            comment.setCommentContent(commentContent.substring(respondent.length() + 1).trim());
        } else {
            comment.setCommentContent(commentContent.trim());
        }

        // 判断用户输入内容是否为空字符串
        if (Objects.equals(comment.getCommentContent(), StringUtil.BLANK)) {
            return JsonResult.fail(CodeType.COMMENT_BLANK).toJSON();
        } else {
            // 防止xss攻击
            comment.setCommentContent(JavaScriptCheck.javaScriptCheck(comment.getCommentContent()));
            commentService.insertComment(comment);
        }
        DataMap dataMap = commentService.replyReplyReturn(comment, username, respondent);
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 是否登陆
     * @author: 陈文振
     * @date: 2020/1/4
     * @param
     * @return: java.lang.String
     */
    @ApiOperation(value = "用户是否登陆")
    @LogAnnotation(module = "用户是否登陆", operation = "登录判断")
    @GetMapping(value = "/isLogin", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String isLogin(){
        return JsonResult.success().toJSON();
    }

    /**
     * @description: 新增评论的点赞记录
     * @author: 陈文振
     * @date: 2020/1/4
     * @param articleId
     * @param respondentId
     * @param principal
     * @return: java.lang.String
     */
    @ApiOperation(value = "新增评论的点赞信息")
    @LogAnnotation(module = "新增评论的点赞信息", operation = "新增")
    @GetMapping(value = "/addCommentLike", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String addCommentLike(@RequestParam("articleId") String articleId,
                                 @RequestParam("respondentId") String respondentId,
                                 @AuthenticationPrincipal Principal principal){
        String username = principal.getName();
        LocalDateTime localDateTime = LocalDateTime.now();
        int pId = Integer.parseInt(respondentId.substring(1));
        int userId = commentMapper.findUserIdByPId(pId);
        int likeId = userService.findIdByUsername(username);

        CommentLikesRecord commentLikesRecord = new CommentLikesRecord();
        commentLikesRecord.setLikeDate(localDateTime);
        commentLikesRecord.setUserId(likeId);
        commentLikesRecord.setpId(pId);
        commentLikesRecord.setArticleId(Integer.parseInt(articleId));

        if (commentLikesRecordService.isLike(commentLikesRecord.getArticleId(), commentLikesRecord.getpId(), username)) {
            return JsonResult.fail(CodeType.MESSAGE_HAS_THUMBS_UP).toJSON();
        }

        if (Objects.equals(userId, likeId)) {
            commentLikesRecord.setIsRead(0);
        } else {
            commentLikesRecord.setIsRead(1);
            redisToService.readThumbsUpRecordOnRedis(StringUtil.COMMENT_THUMBS_UP, String.valueOf(userId), 1);
        }
        DataMap dataMap = commentService.updateLikeByArticleIdAndId(commentLikesRecord.getArticleId(), commentLikesRecord.getpId());
        commentLikesRecordService.insertCommentLikesRecord(commentLikesRecord);

        return JsonResult.build(dataMap).toJSON();
    }

}
