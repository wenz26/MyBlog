package com.cwz.blog.defaultblog.controller;

import com.cwz.blog.defaultblog.aspect.annotation.LogAnnotation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author: 陈文振
 * @date: 2020/1/5
 * @description: 错误页面跳转
 */
@Api(tags = "错误页面跳转")
@Controller
public class ErrorPageController {

    @ApiOperation(value = "404跳转页面")
    @LogAnnotation(module = "404跳转页面", operation = "跳转")
    @GetMapping("/404")
    public String error404(){
        return "errorpage/404";
    }

    @ApiOperation(value = "403跳转页面")
    @LogAnnotation(module = "403跳转页面", operation = "跳转")
    @GetMapping("/403")
    public String error403(){
        return "errorpage/403";
    }

    @ApiOperation(value = "500跳转页面")
    @LogAnnotation(module = "500跳转页面", operation = "跳转")
    @GetMapping("/500")
    public String error500(){
        return "errorpage/500";
    }

    /*@ApiOperation(value = "error跳转页面")
    @LogAnnotation(module = "error跳转页面", operation = "跳转")
    @GetMapping("/error")
    public String error(){
        return "404";
    }*/
}
