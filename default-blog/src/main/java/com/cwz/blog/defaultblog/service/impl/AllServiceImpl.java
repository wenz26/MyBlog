package com.cwz.blog.defaultblog.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.constant.CodeType;
import com.cwz.blog.defaultblog.entity.Article;
import com.cwz.blog.defaultblog.entity.Categories;
import com.cwz.blog.defaultblog.entity.Tags;
import com.cwz.blog.defaultblog.entity.User;
import com.cwz.blog.defaultblog.mapper.*;
import com.cwz.blog.defaultblog.redis.HashRedisServiceImpl;
import com.cwz.blog.defaultblog.service.AllService;
import com.cwz.blog.defaultblog.service.ArticleService;
import com.cwz.blog.defaultblog.service.UserAttentionService;
import com.cwz.blog.defaultblog.service.UserService;
import com.cwz.blog.defaultblog.service.common.CommonReturn;
import com.cwz.blog.defaultblog.utils.DataMap;
import com.cwz.blog.defaultblog.utils.StringUtil;
import com.cwz.blog.defaultblog.utils.TimeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author: 陈文振
 * @date: 2020/2/6
 * @description: 公用操作业务操作实现类
 */
@Service("allService")
public class AllServiceImpl implements AllService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserAttentionService userAttentionService;
    @Autowired
    private UserService userService;
    @Autowired
    private TagsMapper tagsMapper;
    @Autowired
    private CategoriesMapper categoriesMapper;
    @Autowired
    private UserAttentionMapper userAttentionMapper;
    @Autowired
    private HashRedisServiceImpl hashRedisService;


    @Override
    public DataMap searchByKeyWords(String keyWords, String timeRange, Integer userId, int type, int rows, int pageNum) {

        JSONObject resultJson = new JSONObject();
        CommonReturn commonReturn = new CommonReturn();

        // 1 为文章搜索，2 为用户搜索，3 为标签搜索，4 为分类搜索
        if (type == 1) {
            JSONObject articleJSON = searchByKeyWordsByArticleTitle(keyWords, timeRange, rows, pageNum, commonReturn);
            resultJson.put("articleJSON", articleJSON);
        } else if (type == 2) {
            JSONObject userJSON = searchByKeyWordsByUsername(keyWords, userId, rows, pageNum, commonReturn);
            resultJson.put("userJSON", userJSON);
        } else if (type == 3) {
            JSONObject tagsJSON = searchByKeyWordsByTagName(keyWords, rows, pageNum, commonReturn);
            resultJson.put("tagsJSON", tagsJSON);
        } else if (type == 4) {
            JSONObject categoriesJSON = searchByKeyWordsByCategoryName(keyWords, rows, pageNum, commonReturn);
            resultJson.put("categoriesJSON", categoriesJSON);
        }

        resultJson.put("searchKeyWords", keyWords);

        return DataMap.success().setData(resultJson);
    }

    @Override
    public DataMap searchHistory(int userId) {

        JSONObject resultJson = new JSONObject();
        String key = StringUtil.SEARCH_HISTORY + "_" + userId;

        if (hashRedisService.hasKey(key)) {
            LinkedHashMap map = (LinkedHashMap)hashRedisService.getAllFieldAndValue(key);

            if (map.size() == 0) {
                resultJson.put("searchHistory", null);
            } else {

                JSONArray searchHistoryArray = new JSONArray();
                List<String> sort = new ArrayList<>();

                for (Object object : map.keySet()) {
                    sort.add((String) object);
                }

                Collections.sort(sort);
                Collections.reverse(sort);

                JSONObject jsonObject;
                int i = 0;
                List<String> check = new ArrayList<>();
                while (searchHistoryArray.size() < 10 && i < sort.size()) {
                    String s = sort.get(i);
                    String o = (String) hashRedisService.get(key, s);

                    if (check.contains(o)) {
                        i++;
                        continue;
                    }

                    jsonObject = new JSONObject();
                    jsonObject.put("key", s);
                    jsonObject.put("value", o);
                    searchHistoryArray.add(jsonObject);
                    check.add(o);
                    i++;
                }

                resultJson.put("searchHistory", searchHistoryArray);
            }

        } else {
            resultJson.put("searchHistory", null);
        }

        return DataMap.success().setData(resultJson);

    }

    @Override
    public DataMap deleteOneHistory(int userId, String key) {

        String hashHead = StringUtil.SEARCH_HISTORY + "_" + userId;

        String value = (String) hashRedisService.get(hashHead, key);

        LinkedHashMap map = (LinkedHashMap) hashRedisService.getAllFieldAndValue(hashHead);
        for (Object object : map.keySet()) {

            if (Objects.equals(value, hashRedisService.get(hashHead, object))) {
                hashRedisService.hashDelete(hashHead, object);
            }
        }

        return DataMap.success();
    }

    @Override
    public DataMap deleteAllHistory(int userId) {
        String hashHead = StringUtil.SEARCH_HISTORY + "_" + userId;
        hashRedisService.deleteByKey(hashHead);
        return DataMap.success();
    }

    /**
     * @description: 关键字为文章标题时
     * @author: 陈文振
     * @date: 2020/2/6
     * @param keyWords
     * @param rows
     * @param pageNum
     * @return: com.alibaba.fastjson.JSONArray
     */
    private JSONObject searchByKeyWordsByArticleTitle(String keyWords, String timeRange, int rows, int pageNum, CommonReturn commonReturn){
        double currentPage = pageNum;
        pageNum = (pageNum - 1) * rows;

        List<Article> articles;
        double total;

        if (Objects.equals(timeRange, "all")) {
            articles = articleMapper.findAllToDraftArticles(1, null, null,
                    null, keyWords, pageNum, rows);
            total = articleMapper.countArticle(1, null, null, null, keyWords);

        } else if (Objects.equals(timeRange, "oneDay")) {
            articles = articleMapper.findTimeRangeToDraftArticles(1, null, null,
                    null, keyWords, "interval 1 day", pageNum, rows);
            total = articleMapper.countTimeRangeToArticle(1, null, null, null, keyWords, "interval 1 day");

        } else if (Objects.equals(timeRange, "oneWeek")) {
            articles = articleMapper.findTimeRangeToDraftArticles(1, null, null,
                    null, keyWords, "interval 7 day", pageNum, rows);
            total = articleMapper.countTimeRangeToArticle(1, null, null, null, keyWords, "interval 7 day");

        } else if (Objects.equals(timeRange, "oneMonth")) {
            articles = articleMapper.findTimeRangeToDraftArticles(1, null, null,
                    null, keyWords, "interval 1 month", pageNum, rows);
            total = articleMapper.countTimeRangeToArticle(1, null, null, null, keyWords, "interval 1 month");

        } else if (Objects.equals(timeRange, "threeMonth")) {
            articles = articleMapper.findTimeRangeToDraftArticles(1, null, null,
                    null, keyWords, "interval 3 month", pageNum, rows);
            total = articleMapper.countTimeRangeToArticle(1, null, null, null, keyWords, "interval 3 month");

        } else {
            articles = articleMapper.findAllToDraftArticles(1, null, null,
                    null, keyWords, pageNum, rows);
            total = articleMapper.countArticle(1, null, null, null, keyWords);
        }


        PageInfo<Article> pageInfo = new PageInfo<>(articles);

        double pages = Math.ceil(total / rows);

        pageInfo.setTotal((int) total);
        pageInfo.setPages((int) pages);
        pageInfo.setIsLastPage((int) currentPage == (int) pages);
        pageInfo.setPageSize(rows);
        pageInfo.setIsFirstPage((int) currentPage == 1);
        pageInfo.setPageNum((int) currentPage);

        JSONObject articleJson;
        JSONObject returnJson = new JSONObject();
        JSONArray returnJsonArray = new JSONArray();

        for (Article article : articles) {
            articleJson = articleService.getArticleByTagAndCategoryToJsonObject(article);
            returnJsonArray.add(articleJson);
        }

        returnJson.put("result", returnJsonArray);
        returnJson.put("count", (int) total);
        returnJson.put("pageInfo", commonReturn.jsonObjectToPageInfo(pageInfo));

        return returnJson;
    }

    /**
     * @description: 关键字为用户名时
     * @author: 陈文振
     * @date: 2020/2/6
     * @param keyWords
     * @param rows
     * @param pageNum
     * @return: com.alibaba.fastjson.JSONArray
     */
    private JSONObject searchByKeyWordsByUsername(String keyWords, Integer userId, int rows, int pageNum, CommonReturn commonReturn){

        PageHelper.startPage(pageNum, rows);

        List<User> users = userMapper.searchByKeyWordsByUsername(keyWords);
        PageInfo<User> pageInfo = new PageInfo<>(users);

        JSONObject userJson;
        JSONObject returnJson = new JSONObject();
        JSONArray returnJsonArray = new JSONArray();

        for (User user : users) {
            userJson = new JSONObject();

            userJson.put("userId", user.getId());
            userJson.put("username", user.getUsername());
            userJson.put("avatarImgUrl", user.getAvatarImgUrl());
            userJson.put("personalBrief", user.getPersonalBrief());

            userJson.put("countAttention", userAttentionMapper.countAttention(user.getId()));
            userJson.put("countFan", userAttentionMapper.countFan(user.getId()));
            userJson.put("articleNum", articleMapper.countArticleByUserId(user.getId()));

            if (Objects.isNull(userId)) {
                userJson.put("isAttention", false);
            } else {
                userJson.put("isAttention", userAttentionService.isAttention(user.getId(), userId));
            }
            returnJsonArray.add(userJson);
        }

        returnJson.put("result", returnJsonArray);
        returnJson.put("count", userMapper.countSearchByKeyWordsByUsername(keyWords));
        returnJson.put("myUserId", userId);
        returnJson.put("pageInfo", commonReturn.jsonObjectToPageInfo(pageInfo));
        return returnJson;
    }

    /**
     * @description: 关键字为标签名时
     * @author: 陈文振
     * @date: 2020/2/6
     * @param keyWords
     * @param rows
     * @param pageNum
     * @return: com.alibaba.fastjson.JSONArray
     */
    private JSONObject searchByKeyWordsByTagName(String keyWords, int rows, int pageNum, CommonReturn commonReturn){

        PageHelper.startPage(pageNum, rows);

        List<Tags> tags = tagsMapper.searchByKeyWordsByTagName(keyWords);
        PageInfo<Tags> pageInfo = new PageInfo<>(tags);

        JSONObject tagJson;
        JSONObject returnJson = new JSONObject();
        JSONArray returnJsonArray = new JSONArray();

        for (Tags tag : tags) {
            tagJson = new JSONObject();

            tagJson.put("tagName", tag.getTagName());
            tagJson.put("createDate", TimeUtil.getFormatDateForSix(tag.getCreateDate()));
            tagJson.put("id", tag.getId());

            tagJson.put("tagArticleCountNum", tagsMapper.tagArticleCountNum(tag.getId()));

            // 获得标签对应 最新一篇文章 的图片
            List<Object> articleIds = articleMapper.findArticleByTag(tag.getId());
            if (!Objects.isNull(articleIds) && articleIds.size() != 0) {
                Integer articleId = (Integer) articleIds.get(0);
                tagJson.put("imageUrl", articleService.getImageUrl(articleId));
            } else {
                tagJson.put("imageUrl", null);
            }

            returnJsonArray.add(tagJson);
        }

        returnJson.put("result", returnJsonArray);
        returnJson.put("count", tagsMapper.countSearchByKeyWordsByTagName(keyWords));
        returnJson.put("pageInfo", commonReturn.jsonObjectToPageInfo(pageInfo));
        return returnJson;
    }

    /**
     * @description: 关键字为分类名时
     * @author: 陈文振
     * @date: 2020/2/6
     * @param keyWords
     * @param rows
     * @param pageNum
     * @return: com.alibaba.fastjson.JSONArray
     */
    private JSONObject searchByKeyWordsByCategoryName(String keyWords, int rows, int pageNum, CommonReturn commonReturn){

        PageHelper.startPage(pageNum, rows);

        List<Categories> categories = categoriesMapper.searchByKeyWordsByCategoryName(keyWords);
        PageInfo<Categories> pageInfo = new PageInfo<>(categories);

        JSONObject categoryJson;
        JSONObject returnJson = new JSONObject();
        JSONArray returnJsonArray = new JSONArray();

        for (Categories category : categories) {
            categoryJson = new JSONObject();
            categoryJson.put("id", category.getId());
            categoryJson.put("categoryName", category.getCategoryName());
            categoryJson.put("createDate", TimeUtil.getFormatDateForSix(category.getCreateDate()));
            categoryJson.put("articleNum", articleService.countArticleCategoryByCategory(category.getId()));
            categoryJson.put("description", category.getDescription());

            // 获得标签对应 最新一篇文章 的图片
            List<Integer> articleIds = articleMapper.findArticleByCategory(category.getId());
            if (!Objects.isNull(articleIds) && articleIds.size() != 0) {
                Integer articleId = articleIds.get(0);
                categoryJson.put("imageUrl", articleService.getImageUrl(articleId));
            } else {
                categoryJson.put("imageUrl", null);
            }

            returnJsonArray.add(categoryJson);
        }

        returnJson.put("result", returnJsonArray);
        returnJson.put("count", categoriesMapper.countSearchByKeyWordsByCategoryName(keyWords));
        returnJson.put("pageInfo", commonReturn.jsonObjectToPageInfo(pageInfo));
        return returnJson;
    }
}
