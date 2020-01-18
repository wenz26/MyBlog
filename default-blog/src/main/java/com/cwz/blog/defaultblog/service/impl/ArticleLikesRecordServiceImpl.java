package com.cwz.blog.defaultblog.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.constant.CodeType;
import com.cwz.blog.defaultblog.entity.ArticleLikesRecord;
import com.cwz.blog.defaultblog.mapper.ArticleLikesRecordMapper;
import com.cwz.blog.defaultblog.redis.HashRedisServiceImpl;
import com.cwz.blog.defaultblog.redis.StringRedisServiceImpl;
import com.cwz.blog.defaultblog.service.ArticleLikesRecordService;
import com.cwz.blog.defaultblog.service.ArticleService;
import com.cwz.blog.defaultblog.service.UserService;
import com.cwz.blog.defaultblog.service.common.CommonReturn;
import com.cwz.blog.defaultblog.utils.DataMap;
import com.cwz.blog.defaultblog.utils.StringUtil;
import com.cwz.blog.defaultblog.utils.TimeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author: 陈文振
 * @date: 2019/12/9
 * @description: 文章点赞记录业务实现类
 */
@Service("articleLikesRecordService")
public class ArticleLikesRecordServiceImpl implements ArticleLikesRecordService {

    @Autowired
    private ArticleLikesRecordMapper articleLikesRecordMapper;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisServiceImpl stringRedisService;

    @Autowired
    private HashRedisServiceImpl hashRedisService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean isLike(Integer articleId, String username) {
        Example example = new Example(ArticleLikesRecord.class);
        example.selectProperties("likeDate");
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("articleId", articleId);
        criteria.andEqualTo("userId", userService.findIdByUsername(username));
        ArticleLikesRecord articleLikesRecord = articleLikesRecordMapper.selectOneByExample(example);
        return articleLikesRecord != null;
    }

    @Override
    public int insertArticleLikesRecord(ArticleLikesRecord articleLikesRecord) {
        return articleLikesRecordMapper.insert(articleLikesRecord);
    }

    @Override
    public int deleteArticleLikesRecordByArticleId(Integer articleId) {
        Example example = new Example(ArticleLikesRecord.class);
        example.createCriteria().andEqualTo("articleId", articleId);
        return articleLikesRecordMapper.deleteByExample(example);
    }

    @Override
    public DataMap getArticleThumbsUp(int rows, int pageNum, String username) {

        int userId = userService.findIdByUsername(username);

        JSONObject returnJson = new JSONObject();
        CommonReturn commonReturn = new CommonReturn();

        PageHelper.startPage(pageNum, rows);
        /*Example example = new Example(ArticleLikesRecord.class);
        example.orderBy("id").desc();
        List<ArticleLikesRecord> articleLikesRecords = articleLikesRecordMapper.selectByExample(example);*/

        List<ArticleLikesRecord> articleLikesRecords = articleLikesRecordMapper.findArticleLikesRecordByUserId(userId);
        PageInfo<ArticleLikesRecord> pageInfo = new PageInfo<>(articleLikesRecords);

        JSONArray returnJsonArray = new JSONArray();
        JSONObject articleLikesJson;
        for (ArticleLikesRecord articleLikesRecord : articleLikesRecords) {
            articleLikesJson = new JSONObject();
            articleLikesJson.put("id", articleLikesRecord.getId());
            articleLikesJson.put("articleId", articleLikesRecord.getArticleId());
            articleLikesJson.put("likeDate", TimeUtil.getFormatDateForSix(articleLikesRecord.getLikeDate()));
            articleLikesJson.put("praisePeople", userService.findUsernameById(articleLikesRecord.getUserId()));
            articleLikesJson.put("articleTitle", articleService.findArticleTitleByArticleId(articleLikesRecord.getArticleId()).get("articleTitle"));
            articleLikesJson.put("isRead", articleLikesRecord.getIsRead());
            returnJsonArray.add(articleLikesJson);
        }
        returnJson.put("result", returnJsonArray);

        // 找到当前用户所有未读的记录
        returnJson.put("msgIsNotReadNum", articleLikesRecordMapper.countArticleLikesRecordToNotReadByUserId(userId));

        JSONObject pageJson = commonReturn.jsonObjectToPageInfo(pageInfo);
        returnJson.put("pageInfo", pageJson);

        return DataMap.success().setData(returnJson);
    }

    @Override
    public DataMap readThisThumbsUp(int id) {
        try {
            // 文章点赞已读
            int i = articleLikesRecordMapper.readThisThumbsUp(id);
            // 文章点赞数 -1
            if (i > 0) {
                int articleId = articleLikesRecordMapper.findArticleIdById(id);
                hashRedisService.hashIncrement(StringUtil.ARTICLE_THUMBS_UP, String.valueOf(articleId), -1);

                int articleThumbsUp = (int) hashRedisService.get(StringUtil.ARTICLE_THUMBS_UP, String.valueOf(articleId));
                if (articleThumbsUp == 0) {
                    // 当 文章的点赞数为0 就把这个字段在redis中删除
                    hashRedisService.hashDelete(StringUtil.ARTICLE_THUMBS_UP, String.valueOf(articleId));
                    return DataMap.success(CodeType.READ_ARTICLE_THUMBS_UP_ALL_SUCCESS);
                }
                return DataMap.success(CodeType.READ_ARTICLE_THUMBS_UP_SUCCESS);
            }
            return DataMap.fail(CodeType.READ_ARTICLE_THUMBS_UP_FAIL);
        } catch (Exception e) {
            logger.error("阅读文章点赞信息失败：" + e);
            return DataMap.fail(CodeType.READ_ARTICLE_THUMBS_UP_FAIL);
        }
    }

    @Override
    public DataMap readAllThumbsUp(String username) {
        try {
            int userId = userService.findIdByUsername(username);
            List<Integer> articleIds = articleLikesRecordMapper.findArticleIdToNotReadByUserId(userId);
            int i = articleLikesRecordMapper.readAllArticleLikesRecordNotReadByUserId(userId);

            if (i > 0 && articleIds.size() != 0) {
                for (Integer articleId : articleIds) {
                    hashRedisService.hashDelete(StringUtil.ARTICLE_THUMBS_UP, String.valueOf(articleId));
                }
                return DataMap.success(CodeType.READ_ARTICLE_THUMBS_UP_ALL_SUCCESS);
            }
            return DataMap.fail(CodeType.READ_ARTICLE_THUMBS_UP_FAIL);
        } catch (Exception e) {
            logger.error("阅读文章点赞信息失败：" + e);
            return DataMap.fail(CodeType.READ_ARTICLE_THUMBS_UP_FAIL);
        }
    }
}
