package com.cwz.blog.defaultblog.controller;

import com.cwz.blog.defaultblog.aspect.annotation.LogAnnotation;
import com.cwz.blog.defaultblog.service.ArticleService;
import com.cwz.blog.defaultblog.utils.StringUtil;
import com.cwz.blog.defaultblog.utils.TransCodingUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

/**
 * @author: 陈文振
 * @date: 2019/12/27
 * @description: 所有页面跳转
 */
@Api(tags = "所有页面跳转")
@Controller
public class BackController {

    @Autowired
    private ArticleService articleService;

    private static final String SLASH_SYMBOL = "/";

    /**
     * @description: 跳转首页
     * @author: 陈文振
     * @date: 2019/12/27
     * @param request
     * @param response
     * @return: java.lang.String
     */
    @ApiOperation(value = "跳转首页")
    @LogAnnotation(module = "首页", operation = "跳转")
    @GetMapping("/")
    public String index(HttpServletRequest request, HttpServletResponse response) {
        // 设置跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        // 判断是否有需要回跳的url，有则将需要回跳的url保存在响应头中
        response.setHeader("lastUrl", (String) request.getSession().getAttribute("lastUrl"));
        return "index";
    }

    /**
     * @description: 跳转登录页
     * @author: 陈文振
     * @date: 2019/12/27
     * @param
     * @return: java.lang.String
     */
    @ApiOperation(value = "跳转登录页")
    @LogAnnotation(module = "登录页", operation = "跳转")
    @GetMapping("/login")
    public String login(HttpSession session) {
        String imageLogin = (String) session.getAttribute("imageLogin");
        String smsLogin = (String) session.getAttribute("smsLogin");

        if (Objects.isNull(imageLogin) && Objects.isNull(smsLogin)) {
            session.setAttribute("imageLogin", "image");
        }

        return "login";
    }

    /**
     * @description: 登录前尝试保存上一个页面的url
     * @author: 陈文振
     * @date: 2019/12/27
     * @param request
     * @return: void
     */
    @ApiOperation(value = "登录前尝试保存上一个页面的url")
    @LogAnnotation(module = "保存上一个页面的url", operation = "保存")
    @GetMapping("/toLogin")
    @ResponseBody
    public void toLogin(HttpServletRequest request) {
        //保存跳转页面的url
        String lastUrl = request.getHeader("Referer");
        if (lastUrl != null) {
            try {
                URL url = new URL(lastUrl);
                if (!Objects.equals(SLASH_SYMBOL, url.getPath())) {
                    request.getSession().setAttribute("lastUrl", lastUrl);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @description: 跳转注册页
     * @author: 陈文振
     * @date: 2019/12/28
     * @param
     * @return: java.lang.String
     */
    @ApiOperation(value = "跳转注册页")
    @LogAnnotation(module = "注册页", operation = "跳转")
    @GetMapping("/register")
    public String register(){
        return "register";
    }

    /**
     * @description: 跳转更新页
     * @author: 陈文振
     * @date: 2019/12/28
     * @param request
     * @return: java.lang.String
     */
    @ApiOperation(value = "跳转更新页")
    @LogAnnotation(module = "更新页", operation = "跳转")
    @GetMapping("/update")
    public String update(HttpServletRequest request){
        request.getSession().removeAttribute("lastUrl");
        return "update";
    }

    /**
     * @description: 跳转到用户页
     * @author: 陈文振
     * @date: 2019/12/28
     * @param request
     * @return: java.lang.String
     */
    @ApiOperation(value = "跳转到用户页")
    @LogAnnotation(module = "用户页", operation = "跳转")
    @GetMapping("/user")
    public String user(HttpServletRequest request){
        request.getSession().removeAttribute("lastUrl");
        return "user";
    }

    /**
     * @description: 跳转到文章编辑页
     * @author: 陈文振
     * @date: 2019/12/28
     * @param request
     * @return: java.lang.String
     */
    @ApiOperation(value = "跳转到文章编辑页")
    @LogAnnotation(module = "文章编辑页", operation = "跳转")
    @GetMapping("/editor")
    public String editor(HttpServletRequest request){
        request.getSession().removeAttribute("lastUrl");

        String id = request.getParameter("id");
        String draftId = request.getParameter("draftId");

        if (!StringUtils.isBlank(id)) {
            // 这里是已发布文章
            request.getSession().setAttribute("articleId", id);
        } else if (!StringUtils.isBlank(draftId)) {
            // 这里是草稿箱文章
            request.getSession().setAttribute("draftId", draftId);
        }
        return "editor";
    }

    /**
     * @description: 跳转到文章显示页
     * @author: 陈文振
     * @date: 2019/12/28
     * @param articleId
     * @param request
     * @param response
     * @param model
     * @return: java.lang.String
     */
    @ApiOperation(value = "跳转到文章显示页")
    @LogAnnotation(module = "文章显示页", operation = "跳转")
    @GetMapping("/article/{articleId}")
    public String show(@PathVariable("articleId") int articleId,
                       HttpServletRequest request,
                       HttpServletResponse response,
                       Model model) {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        request.getSession().removeAttribute("lastUrl");

        Map<String, String> articleMap = articleService.findArticleTitleByArticleId(articleId);
        if (articleMap.get("articleTitle") != null) {
            model.addAttribute("articleTitle", articleMap.get("articleTitle"));
            String articleTabloid = articleMap.get("articleTabloid");

            if (articleTabloid.length() <= 110) {
                model.addAttribute("articleTabloid", articleTabloid);
            } else {
                model.addAttribute("articleTabloid", articleTabloid.substring(0, 110));
            }
        }
        // 将文章id存入响应头
        response.setHeader("articleId", String.valueOf(articleId));
        return "show";
    }

    /**
     * @description: 跳转到用户归档页
     * @author: 陈文振
     * @date: 2019/12/28
     * @param response
     * @param request
     * @return: java.lang.String
     */
    @ApiOperation(value = "跳转到用户归档页")
    @LogAnnotation(module = "用户归档页", operation = "跳转")
    @GetMapping("/archive")
    public String archive(HttpServletResponse response,
                           HttpServletRequest request){
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        request.getSession().removeAttribute("lastUrl");
        String archiveDay = request.getParameter("archiveDay");

        if (!Objects.isNull(archiveDay) && !Objects.equals(archiveDay, StringUtil.BLANK)) {
            response.setHeader("archiveDay", archiveDay);
        }
        return "archive";
    }

    /**
     * @description: 跳转到归档页
     * @author: 陈文振
     * @date: 2020/1/7
     * @param response
     * @param request
     * @return: java.lang.String
     */
    @ApiOperation(value = "跳转到归档页")
    @LogAnnotation(module = "归档页", operation = "跳转")
    @GetMapping("/archives")
    public String archives(HttpServletResponse response,
                           HttpServletRequest request){
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        request.getSession().removeAttribute("lastUrl");
        String archiveDay = request.getParameter("archiveDay");

        if(!Objects.isNull(archiveDay) && !Objects.equals(archiveDay, StringUtil.BLANK)){
            response.setHeader("archiveDay", archiveDay);
        }

        return "archives";
    }

    /**
     * @description: 跳转到分类页
     * @author: 陈文振
     * @date: 2019/12/28
     * @param response
     * @param request
     * @return: java.lang.String
     */
    @ApiOperation(value = "跳转到分类页")
    @LogAnnotation(module = "分类页", operation = "跳转")
    @GetMapping("/categories")
    public String categories(HttpServletResponse response,
                             HttpServletRequest request){
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        request.getSession().removeAttribute("lastUrl");
        String categoryName = request.getParameter("categoryName");

        if(!Objects.isNull(categoryName) && !Objects.equals(categoryName, StringUtil.BLANK)){
            response.setHeader("categoryName", TransCodingUtil.stringToUnicode(categoryName));
        }

        return "categories";
    }

    /**
     * @description: 跳转到标签页
     * @author: 陈文振
     * @date: 2019/12/28
     * @param response
     * @param request
     * @return: java.lang.String
     */
    @ApiOperation(value = "跳转到标签页")
    @LogAnnotation(module = "标签页", operation = "跳转")
    @GetMapping("/tags")
    public String tags(HttpServletResponse response,
                       HttpServletRequest request){
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        request.getSession().removeAttribute("lastUrl");
        String tag = request.getParameter("tag");

        if(!Objects.isNull(tag) && !Objects.equals(tag, StringUtil.BLANK)){
            response.setHeader("tag", TransCodingUtil.stringToUnicode(tag));
        }

        return "tags";
    }

    /**
     * @description: 跳转到超级管理员页
     * @author: 陈文振
     * @date: 2019/12/28
     * @param request
     * @return: java.lang.String
     */
    @ApiOperation(value = "跳转到超级管理员页")
    @LogAnnotation(module = "超级管理员页", operation = "跳转")
    @GetMapping("/superAdmin")
    public String superAdmin(HttpServletRequest request){
        request.getSession().removeAttribute("lastUrl");
        return "superAdmin";
    }



}
