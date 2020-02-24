package com.cwz.blog.defaultblog.controller;

import com.cwz.blog.defaultblog.aspect.annotation.LogAnnotation;
import com.cwz.blog.defaultblog.aspect.annotation.PermissionCheck;
import com.cwz.blog.defaultblog.component.StringAndArray;
import com.cwz.blog.defaultblog.constant.CodeType;
import com.cwz.blog.defaultblog.constant.OSSClientConstants;
import com.cwz.blog.defaultblog.entity.Article;
import com.cwz.blog.defaultblog.service.ArticleService;
import com.cwz.blog.defaultblog.service.CategoriesService;
import com.cwz.blog.defaultblog.service.TagsService;
import com.cwz.blog.defaultblog.service.UserService;
import com.cwz.blog.defaultblog.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author: 陈文振
 * @date: 2020/1/4
 * @description: 文章编辑器控制层
 */
@Api(tags = "文章编辑器控制层")
@RestController
public class EditorController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private UserService userService;
    @Autowired
    private TagsService tagsService;
    @Autowired
    private CategoriesService categoriesService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @description: 发表博客
     * @author: 陈文振
     * @date: 2020/1/4
     * @param principal
     * @param article
     * @param request
     * @return: java.lang.String
     */
    @ApiOperation(value = "发表博客")
    @LogAnnotation(module = "发表博客", operation = "新增/修改")
    @PostMapping(value = "/publishArticle", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    public String publishArticle(@AuthenticationPrincipal Principal principal,
                                 Article article,
                                 HttpServletRequest request){

        // 获得文章html代码并生成摘要
        BuildArticleTabloidUtil buildArticleTabloidUtil = new BuildArticleTabloidUtil();
        String articleHtmlContent = buildArticleTabloidUtil.buildArticleTabloid(request.getParameter("articleHtmlContent"));
        article.setArticleTabloid(articleHtmlContent + "...");

        // 设置文章标签
        String[] articleTags = request.getParameterValues("articleTagsValue");
        String[] tags = new String[articleTags.length + 1];

        int count = 0;
        for (int i = 0; i < articleTags.length; i++) {
            // 去掉可能出现的换行符
            articleTags[i] = articleTags[i].replaceAll("<br>", StringUtil.BLANK)
                    .replaceAll("&nbsp;", StringUtil.BLANK).replaceAll("\\s+", StringUtil.BLANK).trim();

            if (Objects.equals(articleTags[i], article.getArticleType())) {
                logger.info("这里文章类型是重复的");
                continue;
            }

            if ((Objects.equals(articleTags[i], "原创") && Objects.equals(article.getArticleType(), "转载")) ||
                    (Objects.equals(articleTags[i], "转载") && Objects.equals(article.getArticleType(), "原创"))) {
                logger.info("这里是改了文章类型的");
                continue;
            }
            //System.out.println(articleTags[i] + "   " + count + i);
            tags[count] = articleTags[i];
            count++;
        }
        //System.out.println(count);
        tags[count] = article.getArticleType();

        article.setTagName(StringAndArray.arrayToString(tags));

        // 设置文章分类
        String articleCategoryName = request.getParameter("articleCategoryName");
        article.setArticleCategories(categoriesService.findCategoryByCategoryName(articleCategoryName).getId());

        // 这里还有文章的图片URL
        if (Objects.isNull(article.getImageUrl())) {
            return JsonResult.fail(CodeType.ARTICLE_IMAGE_BLANK).toJSON();
        }

        article.setDraft(1);

        return returnJsonResult(principal, request, article);
    }

    /**
     * @description: 保存博客到草稿箱
     * @author: 陈文振
     * @date: 2020/1/5
     * @param principal
     * @param article
     * @param request
     * @return: java.lang.String
     */
    @ApiOperation(value = "保存博客到草稿箱")
    @LogAnnotation(module = "保存博客到草稿箱", operation = "新增/修改")
    @PostMapping(value = "/saveArticle", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    public String saveArticle(@AuthenticationPrincipal Principal principal,
                                 Article article,
                                 HttpServletRequest request){

        // 获得文章html代码并生成摘要
        BuildArticleTabloidUtil buildArticleTabloidUtil = new BuildArticleTabloidUtil();
        String articleHtmlContent = buildArticleTabloidUtil.buildArticleTabloid(request.getParameter("articleHtmlContent"));
        article.setArticleTabloid(articleHtmlContent + "...");

        article.setDraft(0);

        return returnJsonResult(principal, request, article);
    }

    /**
     * @description: 对返回数据的一个封装
     * @author: 陈文振
     * @date: 2020/1/5
     * @param
     * @return: java.lang.String
     */
    private String returnJsonResult(Principal principal, HttpServletRequest request, Article article) {
        // 判断用户是不是还没登录（大多数是判断用户写文章时是不是登录超时也就是，session过期了）
        if (principal == null) {
            request.getSession().setAttribute("article", article);
            logger.info("判断用户是不是还没登录（大多数是判断用户写文章时是不是登录超时也就是，session过期了）");
            return JsonResult.fail(CodeType.USER_NOT_LOGIN).toJSON();
        }
        String username = principal.getName();

        String phone = userService.findPhoneByUsername(username);
        if (!userService.isRoleUser(phone)) {
            return JsonResult.fail(CodeType.PUBLISH_ARTICLE_NO_PERMISSION).toJSON();
        }

        // 看看前端有没有id传过来，没有的话就新增，有的话就修改
        String id = request.getParameter("id");
        // 如果是修改文章的话
        if (!StringUtils.isBlank(id)) {
            LocalDateTime localDateTime = LocalDateTime.now();
            article.setUpdateDate(localDateTime);
            article.setId(Integer.parseInt(id));
            logger.info("该文章已存在数据库里，这是修改文章");
            DataMap dataMap = articleService.updateArticleById(article, username);
            return JsonResult.build(dataMap).toJSON();
        }

        logger.info("该文章还未插入数据库，这是新增文章");
        DataMap dataMap = articleService.insertArticle(article, username);
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 获得所有的分类
     * @author: 陈文振
     * @date: 2020/1/5
     * @param
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得所有的分类")
    @LogAnnotation(module = "获得所有的分类", operation = "查找")
    @GetMapping(value = "/findCategoriesName", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    public String findCategoriesName() {
        DataMap dataMap = categoriesService.findCategoriesName();
        return JsonResult.build(dataMap).toJSON();
    }

    /**
     * @description: 获得是否有未发布的草稿文章或是修改文章（这个其实是当用户登录超时时再次给用户补上的）
     * @author: 陈文振
     * @date: 2020/1/5
     * @param request
     * @return: java.lang.String
     */
    @ApiOperation(value = "获得是否有未发布的草稿文章或是修改文章")
    @LogAnnotation(module = "获得是否有未发布的草稿文章或是修改文章", operation = "查找")
    @GetMapping(value = "/getDraftArticle", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public String getDraftArticle(HttpServletRequest request){

        String articleId = (String) request.getSession().getAttribute("articleId");
        // 判断是否为修改文章（已发布文章）
        if (!Objects.isNull(articleId)) {
            request.getSession().removeAttribute("articleId");
            Article article = articleService.findArticleById(Integer.parseInt(articleId));
            DataMap dataMap = articleService.getDraftArticle(article);
            return JsonResult.build(dataMap).toJSON();
        }

        String draftId = (String) request.getSession().getAttribute("draftId");
        // 判断是否为修改文章（草稿箱文章）
        if (!Objects.isNull(draftId)) {
            request.getSession().removeAttribute("draftId");
            Article article = articleService.findArticleById(Integer.parseInt(draftId));
            DataMap dataMap = articleService.getDraftArticle(article);
            return JsonResult.build(dataMap).toJSON();
        }


        Article article = (Article) request.getSession().getAttribute("article");

        // 判断是否为写文章登录超时
        if(!Objects.isNull(article)) {
            DataMap dataMap = articleService.getDraftArticle(article);

            request.getSession().removeAttribute("article");
            return JsonResult.build(dataMap).toJSON();
        }
        return JsonResult.fail().toJSON();
    }

    /**
     * @description: 文章编辑本地上传图片
     * @author: 陈文振
     * @date: 2020/1/5
     * @param request
     * @param response
     * @param file
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    @ApiOperation(value = "文章编辑本地上传图片")
    @LogAnnotation(module = "文章编辑本地上传图片", operation = "上传图片")
    @RequestMapping("/uploadImage")
    public synchronized Map<String,Object> uploadImage(HttpServletRequest request, HttpServletResponse response,
                                          @RequestParam(value = "editormd-image-file", required = false) MultipartFile file){
        Map<String, Object> resultMap = new HashMap<>();

        try {
            request.setCharacterEncoding("utf-8");
            // 设置返回头后页面才能获取返回url
            response.setHeader("X-Frame-Options", "SAMEORIGIN");

            FileUtil fileUtil = new FileUtil();
            String filePath = request.getSession().getServletContext().getRealPath("") + "upload";
            logger.info("本地地址为：" + filePath);

            String fileContentType = file.getContentType();
            String fileExtension = fileContentType.substring(fileContentType.indexOf("/") + 1);

            Date date = new Date();
            String fileName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(date) + "." + fileExtension;
            String day = new SimpleDateFormat("yyyy-MM-dd").format(date);

            String fileUrl = fileUtil.uploadFile(fileUtil.multipartFileToFile(file, filePath, fileName),
                    OSSClientConstants.ARTICLE_IMAGE + "blogArticles/" + day + "/");

            resultMap.put("success", 1);
            resultMap.put("message", "上传成功");
            resultMap.put("url", fileUrl);
        } catch (Exception e) {
            try {
                response.getWriter().write( "{\"success\":0}" );
            } catch (IOException ioE) {
                ioE.printStackTrace();
            }
        }
        return resultMap;
    }

    /**
     * @description: 上传文章的图片URL
     * @author: 陈文振
     * @date: 2020/1/5
     * @param request
     * @param principal
     * @return: java.lang.String
     */
    @ApiOperation(value = "上传文章的图片URL")
    @LogAnnotation(module = "上传文章的图片URL", operation = "上传图片")
    @PostMapping(value = "/uploadArticleImageUrl", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @PermissionCheck(value = "ROLE_USER")
    public synchronized String uploadArticleImageUrl(HttpServletRequest request,
                             @AuthenticationPrincipal Principal principal){

        String username = principal.getName();
        String articleImageUrl = request.getParameter("articleImageUrl");

        //获得上传文件的后缀名
        int index = articleImageUrl.indexOf(";base64");
        String strFileExtendName = "." + articleImageUrl.substring(11, index);
        articleImageUrl = articleImageUrl.substring(index + 8);

        try {
            FileUtil fileUtil = new FileUtil();
            String filePath = request.getSession().getServletContext().getRealPath("") + "upload";
            logger.info("本地地址为：" + filePath);

            Date date = new Date();
            String fileName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(date);
            String day = new SimpleDateFormat("yyyy-MM-dd").format(date);

            File file = fileUtil.base64ToFile(filePath, articleImageUrl, fileName + strFileExtendName);
            logger.info("文章首页图片的本地存储路径为：" + filePath);

            String fileUrl = fileUtil.uploadFile(file, OSSClientConstants.ARTICLE_IMAGE + "indexUrl/" + day +"/");

            logger.info("用户[{}]上传文章首页图片成功！！！获得的url为：" + fileUrl, username);

            return fileUrl;

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("获得的url失败");
            return "fail";
        }
    }

}
