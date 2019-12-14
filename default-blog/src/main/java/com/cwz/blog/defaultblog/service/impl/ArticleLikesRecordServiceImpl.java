package com.cwz.blog.defaultblog.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.entity.ArticleLikesRecord;
import com.cwz.blog.defaultblog.mapper.ArticleLikesRecordMapper;
import com.cwz.blog.defaultblog.service.ArticleLikesRecordService;
import com.cwz.blog.defaultblog.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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



    @Override
    public boolean isLike(long articleId, String username) {
        return false;
    }

    @Override
    public int insertArticleLikesRecord(ArticleLikesRecord articleLikesRecord) {
        return 0;
    }

    @Override
    public int deleteArticleLikesRecordByArticleId(long articleId) {
        return 0;
    }

    @Override
    public JSONObject getArticleThumbsUp(String username, int rows, int pageNum) {
        return null;
    }

    @Override
    public int readThisThumbsUp(int id) {
        return 0;
    }

    @Override
    public JSONObject readAllThumbsUp() {
        return null;
    }
}
