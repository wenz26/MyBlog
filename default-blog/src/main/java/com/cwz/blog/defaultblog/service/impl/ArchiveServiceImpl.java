package com.cwz.blog.defaultblog.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.entity.Article;
import com.cwz.blog.defaultblog.mapper.ArticleMapper;
import com.cwz.blog.defaultblog.service.ArchiveService;
import com.cwz.blog.defaultblog.service.UserService;
import com.cwz.blog.defaultblog.utils.DataMap;
import com.cwz.blog.defaultblog.utils.StringUtil;
import com.cwz.blog.defaultblog.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author: 陈文振
 * @date: 2019/12/16
 * @description: 归档业务操作实现（主要获得文章的日期）
 */
@Service("archiveService")
public class ArchiveServiceImpl implements ArchiveService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private UserService userService;

    @Override
    public DataMap findPublishDateToArticle(String username) {
        Integer userId = null;
        Example articleExample = new Example(Article.class);

        articleExample.orderBy("publishDate").desc();
        Example.Criteria criteria = articleExample.createCriteria();
        criteria.andEqualTo("draft", 1);
        if (!Objects.isNull(username) && !Objects.equals(username, StringUtil.BLANK)) {
            userId = userService.findIdByUsername(username);
            criteria.andEqualTo("userId", userId);
        }
        articleExample.selectProperties("publishDate");
        List<Article> articles = articleMapper.selectByExample(articleExample);

        JSONArray articleJsonArray = new JSONArray();
        JSONObject articleJson;
        TimeUtil timeUtil = new TimeUtil();
        String formatDateForTwo;
        List<String> date = new ArrayList<>();

        for (Article article : articles) {
            articleJson = new JSONObject();
            LocalDateTime publishDate = article.getPublishDate();
            formatDateForTwo = TimeUtil.getFormatDateForTwo(publishDate);

            if (date.size() == 0) {
                selectPublishDateAndArticleNum(articleExample, articleJsonArray,
                        articleJson, timeUtil, userId, formatDateForTwo, date);
            } else {
                if (date.indexOf(formatDateForTwo) > -1) {
                    continue;
                } else {
                    selectPublishDateAndArticleNum(articleExample, articleJsonArray,
                            articleJson, timeUtil, userId, formatDateForTwo, date);
                }
            }
        }
        JSONObject returnJson = new JSONObject();
        returnJson.put("result", articleJsonArray);
        return DataMap.success().setData(returnJson);
    }

    private void selectPublishDateAndArticleNum(Example articleExample, JSONArray articleJsonArray,
                                                JSONObject articleJson, TimeUtil timeUtil, Integer userId,
                                                String formatDateForTwo, List<String> date) {
        String timeToYear;
        timeToYear = timeUtil.timeWhippletreeToYear(formatDateForTwo);
        articleJson.put("archiveDate", timeToYear);
        date.add(formatDateForTwo);

        articleExample.clear();
        Example.Criteria articleCriteria = articleExample.createCriteria();
        articleCriteria.andLike("publishDate", "%" + formatDateForTwo + "%");
        articleCriteria.andEqualTo("draft", 1);
        if (!Objects.isNull(userId)) {
            articleCriteria.andEqualTo("userId", userId);
        }
        /*articleCriteria.andCondition("publish_date like '%"+ formatDateForTwo +"%'");*/
        articleJson.put("archiveArticleNum", articleMapper.selectCountByExample(articleExample));
        articleJsonArray.add(articleJson);
    }
}
