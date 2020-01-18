package com.cwz.blog.defaultblog.controller;

import com.cwz.blog.defaultblog.aspect.annotation.LogAnnotation;
import com.cwz.blog.defaultblog.aspect.annotation.PermissionCheck;
import com.cwz.blog.defaultblog.service.ArchiveService;
import com.cwz.blog.defaultblog.service.ArticleService;
import com.cwz.blog.defaultblog.utils.DataMap;
import com.cwz.blog.defaultblog.utils.JsonResult;
import com.cwz.blog.defaultblog.utils.StringUtil;
import com.cwz.blog.defaultblog.utils.TransCodingUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Objects;

/**
 * @author: 陈文振
 * @date: 2019/12/27
 * @description: 归档
 */
@Api(tags = "Archive归档的请求控制器")
@RestController
public class ArchiveController {

    @Autowired
    private ArchiveService archiveService;
    @Autowired
    private ArticleService articleService;

    /**
     * @description: 获得该用户所有归档日期以及每个归档日期的文章数目
     * @author: 陈文振
     * @date: 2019/12/27
     * @param
     * @return: java.lang.String
     */
    @ApiOperation(value = "获取用户归档信息", notes = "获得该用户所有归档日期以及每个归档日期的文章数目")
    @LogAnnotation(module = "归档信息", operation = "查找")
    @GetMapping(value = "/findArchiveNameAndArticleNumByUser", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String findArchiveNameAndArticleNumByUser(@AuthenticationPrincipal Principal principal) {
        DataMap dataMap = archiveService.findPublishDateToArticle(principal.getName());
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 分页获得该用户该归档日期下的文章
     * @author: 陈文振
     * @date: 2019/12/27
     * @param
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得该用户归档日期下的文章", notes = "分页获得该用户该归档日期下的文章(如果归档日期为空则显示全部文章)")
    @LogAnnotation(module = "归档信息", operation = "查找")
    @GetMapping(value = "/getArchiveArticleByUser", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String getArchiveArticleByUser(@RequestParam(value = "archiveDay", required = false) String archiveDay,
                                          @RequestParam(value = "rows", defaultValue = "8") String rows,
                                          @RequestParam(value = "pageNum", defaultValue = "0") String pageNum,
                                          @AuthenticationPrincipal Principal principal) {
        DataMap dataMap = articleService.findArticleByArchive(archiveDay, principal.getName(), Integer.parseInt(rows), Integer.parseInt(pageNum));
        return JsonResult.build(dataMap).toJSON();
    }


    /**
     * @description: 获得所有归档日期以及每个归档日期的文章数目
     * @author: 陈文振
     * @date: 2019/12/27
     * @param
     * @return: java.lang.String
     */
    @ApiOperation(value = "获取归档信息", notes = "获得所有归档日期以及每个归档日期的文章数目")
    @LogAnnotation(module = "归档信息", operation = "查找")
    @GetMapping(value = "/findArchiveNameAndArticleNum", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    public String findArchiveNameAndArticleNum() {
        DataMap dataMap = archiveService.findPublishDateToArticle(null);
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 分页获得该归档日期下的文章
     * @author: 陈文振
     * @date: 2019/12/27
     * @param
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得该归档日期下的文章", notes = "分页获得该归档日期下的文章(如果归档日期为空则显示全部文章)")
    @LogAnnotation(module = "归档信息", operation = "查找")
    @GetMapping(value = "/getArchiveArticle", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    public String getArchiveArticle(@RequestParam(value = "archiveDay", required = false) String archiveDay,
                                    @RequestParam(value = "rows", defaultValue = "8") String rows,
                                    @RequestParam(value = "pageNum", defaultValue = "0") String pageNum) {
        DataMap dataMap = articleService.findArticleByArchive(archiveDay, null, Integer.parseInt(rows), Integer.parseInt(pageNum));
        return JsonResult.build(dataMap).toJSON();
    }

}
