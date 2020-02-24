package com.cwz.blog.defaultblog.controller;

import com.cwz.blog.defaultblog.aspect.annotation.LogAnnotation;
import com.cwz.blog.defaultblog.aspect.annotation.PermissionCheck;
import com.cwz.blog.defaultblog.redis.HashRedisServiceImpl;
import com.cwz.blog.defaultblog.service.AllService;
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
 * @date: 2020/2/6
 * @description: 公用操作控制类
 */
@Api(tags = "公用操作控制类")
@RestController
public class AllController {

    @Autowired
    private AllService allService;
    @Autowired
    private UserService userService;
    @Autowired
    private HashRedisServiceImpl hashRedisService;


    /**
     * @description: 根据关键字来搜索相应内容
     * @author: 陈文振
     * @date: 2020/2/6
     * @param
     * @return: java.lang.String
     */
    @ApiOperation(value = "根据关键字来搜索相应内容")
    @LogAnnotation(module = "根据关键字来搜索相应内容", operation = "查找")
    @PostMapping(value = "/searchByKeyWords", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    public String searchByKeyWords(@RequestParam("keyWords") String keyWords,
                                   @RequestParam(value = "timeRange", required = false) String timeRange,
                                   @RequestParam("type") int type,
                                   @RequestParam("rows") String rows,
                                   @RequestParam("pageNum") String pageNum,
                                   @AuthenticationPrincipal Principal principal) {

        if (!Objects.equals(keyWords, StringUtil.BLANK)) {
            keyWords = TransCodingUtil.unicodeToString(keyWords);
        }

        Integer userId;
        if (principal == null) {
            userId = null;
        } else {
            String username = principal.getName();
            userId = userService.findIdByUsername(username);
        }

        keyWords = keyWords.trim();

        // System.out.println(timeRange);

        // 1 为文章搜索，2 为用户搜索，3 为标签搜索，4 为分类搜索
        DataMap dataMap = allService.searchByKeyWords(keyWords, timeRange, userId, type, Integer.parseInt(rows), Integer.parseInt(pageNum));
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 根据用户Id来查找用户最近的10个搜索历史
     * @author: 陈文振
     * @date: 2020/2/7
     * @param principal
     * @return: java.lang.String
     */
    @ApiOperation(value = "根据用户Id来查找用户最近的10个搜索历史")
    @LogAnnotation(module = "根据用户Id来查找用户最近的10个搜索历史", operation = "查找")
    @GetMapping(value = "/searchHistory", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String searchHistory(@AuthenticationPrincipal Principal principal) {

        DataMap dataMap = allService.searchHistory(userService.findIdByUsername(principal.getName()));
        return JsonResult.build(dataMap).toJSON();
    }


    @ApiOperation(value = "删除某条历史记录")
    @LogAnnotation(module = "删除某条历史记录", operation = "删除")
    @GetMapping(value = "/deleteOneHistory", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String deleteOneHistory(@RequestParam("key") String key,
                                   @AuthenticationPrincipal Principal principal) {
        key = key.trim();
        DataMap dataMap = allService.deleteOneHistory(userService.findIdByUsername(principal.getName()), key);
        return JsonResult.build(dataMap).toJSON();
    }


    @ApiOperation(value = "删除全部历史记录")
    @LogAnnotation(module = "删除全部历史记录", operation = "删除")
    @GetMapping(value = "/deleteAllHistory", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String deleteAllHistory(@AuthenticationPrincipal Principal principal) {

        DataMap dataMap = allService.deleteAllHistory(userService.findIdByUsername(principal.getName()));
        return JsonResult.build(dataMap).toJSON();
    }

}
