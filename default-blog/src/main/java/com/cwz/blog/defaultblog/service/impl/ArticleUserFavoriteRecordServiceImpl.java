package com.cwz.blog.defaultblog.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.constant.CodeType;
import com.cwz.blog.defaultblog.entity.Article;
import com.cwz.blog.defaultblog.entity.ArticleUserFavoriteRecord;
import com.cwz.blog.defaultblog.mapper.ArticleMapper;
import com.cwz.blog.defaultblog.mapper.ArticleUserFavoriteRecordMapper;
import com.cwz.blog.defaultblog.service.ArticleService;
import com.cwz.blog.defaultblog.service.ArticleUserFavoriteRecordService;
import com.cwz.blog.defaultblog.service.UserService;
import com.cwz.blog.defaultblog.service.common.CommonReturn;
import com.cwz.blog.defaultblog.utils.DataMap;
import com.cwz.blog.defaultblog.utils.TimeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: 陈文振
 * @date: 2019/12/22
 * @description: 用户文章收藏业务操作
 */
@Service("articleUserFavoriteRecordService")
public class ArticleUserFavoriteRecordServiceImpl implements ArticleUserFavoriteRecordService {

    @Autowired
    private ArticleUserFavoriteRecordMapper articleUserFavoriteRecordMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public boolean isFavorite(int articleId, String username) {
        Example example = new Example(ArticleUserFavoriteRecord.class);
        example.selectProperties("createDate");
        example.createCriteria().andEqualTo("articleId", articleId)
                .andEqualTo("userId", userService.findIdByUsername(username));
        ArticleUserFavoriteRecord articleUserFavoriteRecord = articleUserFavoriteRecordMapper.selectOneByExample(example);
        return articleUserFavoriteRecord != null;
    }

    @Override
    public DataMap insertArticleFavoriteRecord(ArticleUserFavoriteRecord articleUserFavoriteRecord) {
        int insert = articleUserFavoriteRecordMapper.insert(articleUserFavoriteRecord);
        if (insert > 0) {
            return DataMap.success(CodeType.ARTICLE_FAVORITE_SUCCESS);
        } else {
            return DataMap.fail(CodeType.ARTICLE_FAVORITE_FAIL);
        }

    }

    @Override
    public DataMap deleteArticleFavoriteRecordByArticleIdAndUsername(int articleId, String username) {
        Example example = new Example(ArticleUserFavoriteRecord.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("articleId", articleId);
        if (!StringUtils.isBlank(username)) {
            criteria.andEqualTo("userId", userService.findIdByUsername(username));
        }

        int i = articleUserFavoriteRecordMapper.deleteByExample(example);
        if (i > 0) {
            return DataMap.success(CodeType.DELETE_ARTICLE_FAVORITE_SUCCESS);
        } else {
            return DataMap.fail(CodeType.DELETE_ARTICLE_FAVORITE_FAIL);
        }

    }

    @Override
    public DataMap findArticleFavoriteRecordByUsername(String username, String articleTitle, int rows, int pageNum) {
        CommonReturn commonReturn = new CommonReturn();
        int userId = userService.findIdByUsername(username);

        PageHelper.startPage(pageNum, rows);
        List<ArticleUserFavoriteRecord> articleUserFavoriteRecords;
        int articleFavoriteNum;

        if (StringUtils.isBlank(articleTitle)) {
            articleUserFavoriteRecords = articleUserFavoriteRecordMapper.findArticleFavoriteRecordByUserId(userId);
            articleFavoriteNum = articleUserFavoriteRecordMapper.countArticleFavoriteRecordByUserId(userId);

        } else {
            articleUserFavoriteRecords = articleUserFavoriteRecordMapper.findArticleFavoriteRecordByUserIdAndArticleTitle(userId, articleTitle);
            articleFavoriteNum = articleUserFavoriteRecordMapper.countArticleFavoriteRecordByUserIdAndArticleTitle(userId, articleTitle);
        }

        PageInfo<ArticleUserFavoriteRecord> pageInfo = new PageInfo<>(articleUserFavoriteRecords);

        JSONObject returnJson = new JSONObject();
        JSONArray returnJsonArray = new JSONArray();
        JSONObject articleFavoriteJson;

        for (ArticleUserFavoriteRecord articleUserFavoriteRecord : articleUserFavoriteRecords) {
            articleFavoriteJson = new JSONObject();
            articleFavoriteJson.put("id", articleUserFavoriteRecord.getId());
            articleFavoriteJson.put("articleId", articleUserFavoriteRecord.getArticleId());

            // 这里还有文章的作者啊，标题，分类，标签，类型，等等的一些信息要写
            Article article = articleMapper.getArticleAndTagsByArticleId(articleUserFavoriteRecord.getArticleId(), 1);
            JSONObject articleObject = articleService.getArticleByTagAndCategoryToJsonObject(article);
            articleFavoriteJson.put("article", articleObject);

            articleFavoriteJson.put("createDate", TimeUtil.getFormatDateForSix(articleUserFavoriteRecord.getCreateDate()));
            articleFavoriteJson.put("userId", articleUserFavoriteRecord.getUserId());
            returnJsonArray.add(articleFavoriteJson);
        }

        returnJson.put("result", returnJsonArray);
        returnJson.put("articleFavoriteNum", articleFavoriteNum);
        returnJson.put("pageInfo", commonReturn.jsonObjectToPageInfo(pageInfo));
        return DataMap.success().setData(returnJson);
    }

    @Override
    public int countArticleFavoriteRecordByUsername(String username) {
        Example example = new Example(ArticleUserFavoriteRecord.class);
        example.createCriteria().andEqualTo("userId", userService.findIdByUsername(username));
        return articleUserFavoriteRecordMapper.selectCountByExample(example);
    }

    @Override
    public int countArticleFavoriteRecordByArticleId(int articleId) {
        Example example = new Example(ArticleUserFavoriteRecord.class);
        example.createCriteria().andEqualTo("articleId", articleId);

        // 这里可以适当的添加一个 redis 用来统计文章的被收藏数

        return articleUserFavoriteRecordMapper.selectCountByExample(example);
    }

    @Override
    public DataMap deleteArticleFavoriteRecordById(int id) {
        int articleId = articleUserFavoriteRecordMapper.selectArticleFavoriteRecordById(id);
        articleUserFavoriteRecordMapper.updateArticleFavoriteRecord(articleId);
        articleUserFavoriteRecordMapper.deleteArticleFavoriteRecordById(id);
        return DataMap.success();
    }

}
