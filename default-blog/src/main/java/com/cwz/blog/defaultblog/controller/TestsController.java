package com.cwz.blog.defaultblog.controller;

import io.swagger.annotations.Api;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Api(tags = "测试用控制层")
@Controller
public class TestsController {

    @GetMapping("/test")
    public String toTest(){
        return "test";
    }

    @GetMapping("/bar")
    public String bar(){
        return "common/bar";
    }

    @GetMapping("/getUser")
    @ResponseBody
    public Principal getUser(@AuthenticationPrincipal Principal principal) {
        return principal;
    }

    @GetMapping("/getPrin")
    @ResponseBody
    public Object getPrin() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        System.out.println("-----------------------" + principal.getClass());
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        return username;
    }
}
