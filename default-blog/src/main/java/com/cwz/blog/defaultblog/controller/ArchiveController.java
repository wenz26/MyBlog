package com.cwz.blog.defaultblog.controller;

import com.cwz.blog.defaultblog.aspect.annotation.LogAnnotation;
import com.cwz.blog.defaultblog.service.ArchiveService;
import com.cwz.blog.defaultblog.service.ArticleService;
import com.cwz.blog.defaultblog.utils.DataMap;
import com.cwz.blog.defaultblog.utils.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author: 陈文振
 * @date: 2019/12/27
 * @description: 归档
 */
@Api(tags = "Archive归档的请求控制器")
@RestController
@RequestMapping("/archive")
public class ArchiveController {

    @Autowired
    private ArchiveService archiveService;
    @Autowired
    private ArticleService articleService;

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
        // 这里从 Spring Security 获得用户名

        DataMap dataMap = archiveService.findPublishDateToArticle("陈文振");
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
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "archiveDay", value = "查找的归档日期", paramType = "query"),
            @ApiImplicitParam(name = "rows", value = "分页中每页查询条数", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "分页中查询的第几页", paramType = "query")
    })
    @GetMapping(value = "/getArchiveArticle", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    public String getArchiveArticle(@RequestParam(value = "archiveDay", required = false) String archiveDay,
                                    @RequestParam(value = "rows", defaultValue = "8") String rows,
                                    @RequestParam(value = "pageNum", defaultValue = "0") String pageNum) {
        DataMap dataMap = articleService.findArticleByArchive(archiveDay, "陈文振", Integer.parseInt(rows), Integer.parseInt(pageNum));
        return JsonResult.build(dataMap).toJSON();
    }
}
