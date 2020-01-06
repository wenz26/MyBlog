package com.cwz.blog.defaultblog.controller;

import com.cwz.blog.defaultblog.aspect.annotation.LogAnnotation;
import com.cwz.blog.defaultblog.aspect.annotation.PermissionCheck;
import com.cwz.blog.defaultblog.constant.CodeType;
import com.cwz.blog.defaultblog.entity.ArticleLikesRecord;
import com.cwz.blog.defaultblog.entity.ArticleUserFavoriteRecord;
import com.cwz.blog.defaultblog.redis.RedisToService;
import com.cwz.blog.defaultblog.service.ArticleLikesRecordService;
import com.cwz.blog.defaultblog.service.ArticleService;
import com.cwz.blog.defaultblog.service.ArticleUserFavoriteRecordService;
import com.cwz.blog.defaultblog.service.UserService;
import com.cwz.blog.defaultblog.utils.DataMap;
import com.cwz.blog.defaultblog.utils.JsonResult;
import com.cwz.blog.defaultblog.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
 * @date: 2020/1/6
 * @description: 文章显示页面
 */
@Api(tags = "文章显示页面")
@RestController
public class ShowArticleController {

    @Autowired
    private ArticleLikesRecordService articleLikesRecordService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisToService redisToService;
    @Autowired
    private ArticleUserFavoriteRecordService articleUserFavoriteRecordService;

    /**
     * @description: 获取文章
     * @author: 陈文振
     * @date: 2020/1/6
     * @param articleId
     * @param principal
     * @return: java.lang.String
     */
    @ApiOperation(value = "获取文章")
    @LogAnnotation(module = "获取文章", operation = "查找")
    @PostMapping(value = "/getArticleByArticleId", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    public String getArticleByArticleId(@RequestParam("articleId") String articleId,
                                 @AuthenticationPrincipal Principal principal) {

        String username = null;
        if (!Objects.isNull(principal)) {
            username = principal.getName();
        }

        DataMap dataMap = articleService.getArticleByArticleId(Integer.parseInt(articleId), username);
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 新增文章点赞记录
     * @author: 陈文振
     * @date: 2020/1/6
     * @param articleId
     * @param principal
     * @return: java.lang.String
     */
    @ApiOperation(value = "新增文章点赞记录")
    @LogAnnotation(module = "新增文章点赞记录", operation = "新增")
    @GetMapping(value = "/addArticleLike", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String addArticleLike(@RequestParam("articleId") String articleId,
                                 @AuthenticationPrincipal Principal principal){

        String username = principal.getName();
        Integer parseInt = Integer.parseInt(articleId);

        if (articleLikesRecordService.isLike(parseInt, username)) {
            return JsonResult.fail(CodeType.ARTICLE_HAS_THUMBS_UP).toJSON();
        }

        DataMap dataMap = articleService.updateLikeByArticleId(parseInt);

        ArticleLikesRecord articleLikesRecord = new ArticleLikesRecord();
        articleLikesRecord.setArticleId(parseInt);
        articleLikesRecord.setUserId(userService.findIdByUsername(username));
        articleLikesRecord.setLikeDate(LocalDateTime.now());
        articleLikesRecordService.insertArticleLikesRecord(articleLikesRecord);

        redisToService.readThumbsUpRecordOnRedis(StringUtil.ARTICLE_THUMBS_UP, articleId, 1);
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 新增文章收藏记录
     * @author: 陈文振
     * @date: 2020/1/6
     * @param articleId
     * @param principal
     * @return: java.lang.String
     */
    @ApiOperation(value = "新增文章收藏记录")
    @LogAnnotation(module = "新增文章收藏记录", operation = "新增")
    @GetMapping(value = "/addArticleFavorite", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String addArticleFavorite(@RequestParam("articleId") String articleId,
                                 @AuthenticationPrincipal Principal principal){

        String username = principal.getName();
        Integer parseInt = Integer.parseInt(articleId);

        if (articleUserFavoriteRecordService.isFavorite(parseInt, username)) {
            return JsonResult.fail(CodeType.ARTICLE_HAS_FAVORITE).toJSON();
        }

        DataMap dataMap = articleService.updateFavoriteByArticleId(parseInt);

        ArticleUserFavoriteRecord articleUserFavoriteRecord = new ArticleUserFavoriteRecord();
        articleUserFavoriteRecord.setArticleId(parseInt);
        articleUserFavoriteRecord.setUserId(userService.findIdByUsername(username));
        articleUserFavoriteRecord.setCreateDate(LocalDateTime.now());
        articleUserFavoriteRecordService.insertArticleFavoriteRecord(articleUserFavoriteRecord);

        return JsonResult.build(dataMap).toJSON();
    }

}
