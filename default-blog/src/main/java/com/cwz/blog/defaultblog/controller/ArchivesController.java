package com.cwz.blog.defaultblog.controller;

import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.entity.Archives;
import com.cwz.blog.defaultblog.entity.Result;
import com.cwz.blog.defaultblog.exception.MyException;
import com.cwz.blog.defaultblog.service.ArchivesService;
import com.cwz.blog.defaultblog.service.ArticleService;
import com.cwz.blog.defaultblog.utils.ResultUtil;
import com.cwz.blog.defaultblog.utils.TransCodingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: 陈文振
 * @date: 2019/12/4
 * @description: 归档业务逻辑控制层
 */
@RestController
public class ArchivesController {

    @Autowired
    private ArchivesService archivesService;

    @Autowired
    private ArticleService articleService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @description: 获得所有归档日期以及每个归档日期的文章数目
     * @author: 陈文振
     * @date: 2019/12/4
     * @return:
     */
    @GetMapping("/findArchiveNameAndArticleNum")
    public JSONObject findArchiveNameAndArticleNum() {
        return archivesService.findArchiveNameAndArticleNum();
    }

    /**
     * @description: 分页获得该归档日期下的文章
     * @author: 陈文振
     * @date: 2019/12/6
     * @param archives
     * @param request
     * @return:
     */
    @GetMapping("/getArchiveArticle")
    public JSONObject getArchiveArticle(Archives archives, HttpServletRequest request,
                                        HttpServletResponse response) throws IOException {
        String archiveName = "";
        try {
            // 把 unicode编码 转成 中文
            archiveName = TransCodingUtil.unicodeToString(archives.getArchiveName());
        } catch (Exception e) {
            logger.error("unicode编码 转成 中文出现错误");

            Result error = ResultUtil.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "unicode编码 转成 中文失败", e.getMessage());
            response.setContentType("application/json;charset=UTF-8");
            // 这里只返回错误的消息
            response.getWriter().write(JSONObject.toJSONString(error));
            return null;
        }

        int rows = Integer.parseInt(request.getParameter("rows"));
        int pageNum = Integer.parseInt(request.getParameter("pageNum"));
        return articleService.findArticleByArchive(archiveName, rows, pageNum);
    }

}
