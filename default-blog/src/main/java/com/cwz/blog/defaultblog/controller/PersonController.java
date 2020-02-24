package com.cwz.blog.defaultblog.controller;

import com.alibaba.fastjson.JSON;
import com.cwz.blog.defaultblog.aspect.annotation.LogAnnotation;
import com.cwz.blog.defaultblog.aspect.annotation.PermissionCheck;
import com.cwz.blog.defaultblog.service.ArticleService;
import com.cwz.blog.defaultblog.service.UserAttentionService;
import com.cwz.blog.defaultblog.service.UserService;
import com.cwz.blog.defaultblog.utils.DataMap;
import com.cwz.blog.defaultblog.utils.JsonResult;
import com.cwz.blog.defaultblog.utils.StringUtil;
import com.cwz.blog.defaultblog.utils.TransCodingUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Objects;

/**
 * @author: 陈文振
 * @date: 2020/2/5
 * @description: 用户详情控制器
 */
@Api(tags = "用户详情控制器")
@RestController
public class PersonController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserAttentionService userAttentionService;

    /**
     * @description: 通过用户找到该用户的文章
     * @author: 陈文振
     * @date: 2020/2/5
     * @param personName
     * @param rows
     * @param pageNum
     * @return: java.lang.String
     */
    @ApiOperation(value = "通过用户找到该用户的文章")
    @LogAnnotation(module = "通过用户找到该用户的文章", operation = "查找")
    @PostMapping(value = "/getPersonInfo" , produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    //@PermissionCheck("ROLE_USER")
    public String getPersonInfo(@RequestParam("personName") String personName,
                                @RequestParam("articleTitle") String articleTitle,
                                @RequestParam("rows") String rows,
                                @RequestParam("pageNum") String pageNum,
                                @AuthenticationPrincipal Principal principal) {

        String username;
        if (principal == null) {
            username = null;
        } else {
          username = principal.getName();
        }

        if (!Objects.equals(personName, StringUtil.BLANK)) {
            personName = TransCodingUtil.unicodeToString(personName);
        }
        DataMap dataMap = articleService.getArticleByUsername(Integer.parseInt(rows), Integer.parseInt(pageNum), personName, username, articleTitle);
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 添加用户关注
     * @author: 陈文振
     * @date: 2020/2/6
     * @param attentionId
     * @param principal
     * @return: java.lang.String
     */
    @ApiOperation(value = "添加用户关注")
    @LogAnnotation(module = "添加用户关注", operation = "添加")
    @GetMapping(value = "/insertUserAttention" , produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck("ROLE_USER")
    public String insertUserAttention(@Param("attentionId") String attentionId,
                                      @AuthenticationPrincipal Principal principal) {

        DataMap dataMap = userAttentionService.insertUserAttention(principal.getName(), Integer.parseInt(attentionId));
        return JsonResult.build(dataMap).toJSON();
    }


}
