package com.cwz.blog.defaultblog.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.constant.CodeType;
import com.cwz.blog.defaultblog.entity.CommentLikesRecord;
import com.cwz.blog.defaultblog.mapper.CommentLikesRecordMapper;
import com.cwz.blog.defaultblog.redis.HashRedisServiceImpl;
import com.cwz.blog.defaultblog.service.ArticleService;
import com.cwz.blog.defaultblog.service.CommentLikesRecordService;
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
 * @date: 2019/12/19
 * @description: 评论点赞记录业务操作实现类
 */
@Service("commentLikesRecordService")
public class CommentLikesRecordServiceImpl implements CommentLikesRecordService {

    @Autowired
    private CommentLikesRecordMapper commentLikesRecordMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private HashRedisServiceImpl hashRedisService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean isLike(int articleId, int pId, String username) {
        Example example = new Example(CommentLikesRecord.class);
        example.createCriteria().andEqualTo("articleId", articleId)
                .andEqualTo("pId", pId)
                .andEqualTo("userId", userService.findIdByUsername(username));
        return commentLikesRecordMapper.selectOneByExample(example) != null;
    }

    @Override
    public void insertCommentLikesRecord(CommentLikesRecord commentLikesRecord) {
        commentLikesRecordMapper.insert(commentLikesRecord);
    }

    @Override
    public void deleteCommentLikesRecordByArticleId(int articleId) {
        Example example = new Example(CommentLikesRecord.class);
        example.createCriteria().andEqualTo("articleId", articleId);
        commentLikesRecordMapper.deleteByExample(example);
    }

    @Override
    public DataMap getCommentThumbsUp(int rows, int pageNum, String username) {

        int userId = userService.findIdByUsername(username);

        JSONObject returnJson = new JSONObject();
        CommonReturn commonReturn = new CommonReturn();

        PageHelper.startPage(pageNum, rows);
        List<CommentLikesRecord> commentLikesRecords = commentLikesRecordMapper.findCommentLikesRecordByUserId(userId);
        PageInfo<CommentLikesRecord> pageInfo = new PageInfo<>(commentLikesRecords);

        JSONArray returnJsonArray = new JSONArray();
        JSONObject commentLikesJson;

        for (CommentLikesRecord commentLikesRecord : commentLikesRecords) {
            commentLikesJson = new JSONObject();
            commentLikesJson.put("id", commentLikesRecord.getId());
            commentLikesJson.put("articleId", commentLikesRecord.getComment().getArticleId());
            commentLikesJson.put("articleTitle", articleService.findArticleTitleByArticleId(commentLikesRecord.getComment().getArticleId()).get("articleTitle"));
            commentLikesJson.put("commentContent", commentLikesRecord.getComment().getCommentContent());
            commentLikesJson.put("commentDate", TimeUtil.getFormatDateForSix(commentLikesRecord.getComment().getCommentDate()));
            commentLikesJson.put("praisePeople", userService.findUsernameById(commentLikesRecord.getUserId()));
            commentLikesJson.put("likeDate", TimeUtil.getFormatDateForSix(commentLikesRecord.getLikeDate()));
            commentLikesJson.put("isRead", commentLikesRecord.getIsRead());
            returnJsonArray.add(commentLikesJson);
        }
        returnJson.put("result", returnJsonArray);

        // 找到当前用户所有未读的评论点赞记录
        returnJson.put("msgIsNotReadNum", commentLikesRecordMapper.countCommentLikesRecordToNotReadByUserId(userId));

        returnJson.put("pageInfo", commonReturn.jsonObjectToPageInfo(pageInfo));

        return DataMap.success().setData(returnJson);
    }

    @Override
    public DataMap readThisThumbsUp(int id, String username) {
        try {
            // 评论点赞已读
            int i = commentLikesRecordMapper.readThisThumbsUp(id);
            // 评论点赞数 -1
            if (i > 0) {
                int userId = userService.findIdByUsername(username);
                hashRedisService.hashIncrement(StringUtil.COMMENT_THUMBS_UP, String.valueOf(userId), -1);

                int commentThumbsUp = (int) hashRedisService.get(StringUtil.COMMENT_THUMBS_UP, String.valueOf(userId));
                if (commentThumbsUp == 0) {
                    // 当 评论的点赞数为0 就把这个字段在redis中删除
                    hashRedisService.hashDelete(StringUtil.COMMENT_THUMBS_UP, String.valueOf(userId));
                    return DataMap.success(CodeType.READ_COMMENT_THUMBS_UP_ALL_SUCCESS);
                }
                return DataMap.success(CodeType.READ_COMMENT_THUMBS_UP_SUCCESS);
            }
            return DataMap.fail(CodeType.READ_COMMENT_THUMBS_UP_FAIL);
        } catch (Exception e) {
            logger.error("阅读评论点赞信息失败：" + e);
            return DataMap.fail(CodeType.READ_COMMENT_THUMBS_UP_FAIL);
        }
    }

    @Override
    public DataMap readAllThumbsUp(String username) {
        try {
            int userId = userService.findIdByUsername(username);
            int i = commentLikesRecordMapper.readAllCommentLikesRecordNotReadByUserId(userId);

            if (i > 0) {
                hashRedisService.hashDelete(StringUtil.COMMENT_THUMBS_UP, String.valueOf(userId));
                return DataMap.success(CodeType.READ_COMMENT_THUMBS_UP_ALL_SUCCESS);
            }
            return DataMap.fail(CodeType.READ_COMMENT_THUMBS_UP_FAIL);
        } catch (Exception e) {
            logger.error("阅读评论点赞信息失败：" + e);
            return DataMap.fail(CodeType.READ_COMMENT_THUMBS_UP_FAIL);
        }
    }
}
