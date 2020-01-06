package com.cwz.blog.defaultblog.controller;

import com.cwz.blog.defaultblog.aspect.annotation.LogAnnotation;
import com.cwz.blog.defaultblog.service.ArticleService;
import com.cwz.blog.defaultblog.service.CategoriesService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @author: 陈文振
 * @date: 2019/12/28
 * @description: 文章分类
 */
@Api(tags = "文章分类")
@RestController
public class CategoriesController {

    @Autowired
    private CategoriesService categoriesService;
    @Autowired
    private ArticleService articleService;

    /**
     * @description: 获得所有分类名以及每个分类名的文章数目
     * @author: 陈文振
     * @date: 2019/12/28
     * @param
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得所有分类名以及每个分类名的文章数目")
    @LogAnnotation(module = "分类信息", operation = "查找")
    @GetMapping(value = "/findCategoriesNameAndArticleNum", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    public String findCategoriesNameAndArticleNum(){
        DataMap dataMap = categoriesService.findCategoriesNameAndArticleCountNum();
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 分页获得该分类下的文章
     * @author: 陈文振
     * @date: 2019/12/28
     * @param categoryName
     * @param rows
     * @param pageNum
     * @return: java.lang.String
     */
    @ApiOperation(value = "分页获得该分类下的文章", notes = "分页获得该分类下的文章(如果分类名为空则显示全部文章)")
    @LogAnnotation(module = "分类信息", operation = "查找")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "categoryName", value = "查找的分类名", paramType = "query"),
            @ApiImplicitParam(name = "rows", value = "分页中每页查询条数", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "分页中查询的第几页", paramType = "query")
    })
    @GetMapping(value = "/getCategoryArticle", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    public String getCategoryArticle(@RequestParam(value = "categoryName", required = false) String categoryName,
                                     @RequestParam(value = "rows", defaultValue = "8") String rows,
                                     @RequestParam(value = "pageNum", defaultValue = "0") String pageNum) {
        if (!Objects.equals(categoryName, StringUtil.BLANK)) {
            categoryName = TransCodingUtil.unicodeToString(categoryName);
        }

        DataMap dataMap = articleService.findArticleByCategory(categoryName, Integer.parseInt(rows), Integer.parseInt(pageNum));
        return JsonResult.build(dataMap).toJSON();
    }
}
