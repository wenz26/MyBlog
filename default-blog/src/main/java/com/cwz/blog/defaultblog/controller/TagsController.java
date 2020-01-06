package com.cwz.blog.defaultblog.controller;

import com.cwz.blog.defaultblog.aspect.annotation.LogAnnotation;
import com.cwz.blog.defaultblog.service.ArticleService;
import com.cwz.blog.defaultblog.service.TagsService;
import com.cwz.blog.defaultblog.utils.DataMap;
import com.cwz.blog.defaultblog.utils.JsonResult;
import com.cwz.blog.defaultblog.utils.StringUtil;
import com.cwz.blog.defaultblog.utils.TransCodingUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: 陈文振
 * @date: 2020/1/6
 * @description: 标签控制器
 */
@Api(tags = "标签控制器")
@RestController
public class TagsController {

    @Autowired
    private TagsService tagsService;
    @Autowired
    private ArticleService articleService;

    /**
     * @description: 分页获得该标签下的文章
     * @author: 陈文振
     * @date: 2020/1/6
     * @param tag
     * @return: java.lang.String
     */
    @ApiOperation(value = "分页获得该标签下的文章")
    @LogAnnotation(module = "分页获得该标签下的文章", operation = "查找")
    @PostMapping(value = "/getTagArticle", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    public String getTagArticle(@RequestParam("rows") String rows,
                                @RequestParam("pageNum") String pageNum,
                                @RequestParam("tag") String tag){

        if (StringUtils.equals(tag, StringUtil.BLANK)) {
            return JsonResult.build(tagsService.findAllTags()).toJSON();
        }

        tag = TransCodingUtil.unicodeToString(tag);
        DataMap dataMap = articleService.findArticleByTag(tag, Integer.parseInt(rows), Integer.parseInt(pageNum));

        return JsonResult.build(dataMap).toJSON();
    }

}
