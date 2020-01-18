package com.cwz.blog.defaultblog.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.constant.CodeType;
import com.cwz.blog.defaultblog.entity.Comment;
import com.cwz.blog.defaultblog.entity.UserReadNews;
import com.cwz.blog.defaultblog.mapper.CommentLikesRecordMapper;
import com.cwz.blog.defaultblog.mapper.CommentMapper;
import com.cwz.blog.defaultblog.redis.HashRedisServiceImpl;
import com.cwz.blog.defaultblog.service.ArticleService;
import com.cwz.blog.defaultblog.service.CommentLikesRecordService;
import com.cwz.blog.defaultblog.service.CommentService;
import com.cwz.blog.defaultblog.service.UserService;
import com.cwz.blog.defaultblog.service.common.CommonReturn;
import com.cwz.blog.defaultblog.utils.DataMap;
import com.cwz.blog.defaultblog.utils.StringUtil;
import com.cwz.blog.defaultblog.utils.TimeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author: 陈文振
 * @date: 2019/12/19
 * @description: 评论业务操作实现类
 */
@Service("commentService")
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentLikesRecordMapper commentLikesRecordMapper;

    @Autowired
    private CommentLikesRecordService commentLikesRecordService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @Autowired
    private HashRedisServiceImpl hashRedisService;

    @Override
    public void insertComment(Comment comment) {
        if (comment.getAnswererId() == comment.getRespondentId()) {
            // 这个是评论（首评论），所以就是已读
            comment.setIsRead(0);
        }
        commentMapper.insert(comment);
        // redis中保存该用户未读消息
        addNotReadNews(comment);

    }

    @Override
    public DataMap findCommentByArticleId(int articleId, String username) {
        List<Comment> comments = commentMapper.findAllCommentByArticle(articleId);

        JSONArray commentJsonArray = new JSONArray();
        JSONArray replyJsonArray;
        JSONObject commentJsonObject;
        JSONObject replyJsonObject;
        List<Comment> replyLists;

        for (Comment comment : comments) {

            // 找到 根评论下的 子评论
            replyLists = commentMapper.findAllCommentReplyByArticleAndPId(articleId, comment.getId());

            replyJsonArray = new JSONArray();

            // 封装所有评论中的回复
            for (Comment reply : replyLists) {
                replyJsonObject = getReplyCommentJson(reply);
                replyJsonArray.add(replyJsonObject);
            }

            // 封装评论
            commentJsonObject = getCommentJson(comment, replyJsonArray);

            // 判断当前用户有没有给这条评论点过赞，如果有 前端那边就不能再点赞了
            if (username == null) {
                commentJsonObject.put("isLiked", 0);
            } else {
                if (commentLikesRecordService.isLike(articleId, comment.getId(), username)) {
                    commentJsonObject.put("isLiked", 1);
                } else {
                    commentJsonObject.put("isLiked", 0);
                }
            }

            commentJsonArray.add(commentJsonObject);
        }

        commentJsonArray.add(commentMapper.countCommentByArticleId(articleId));

        return DataMap.success().setData(commentJsonArray);
    }

    @Override
    public DataMap replyReplyReturn(Comment comment, String answerer, String respondent) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", comment.getId());
        jsonObject.put("answerer", answerer);
        jsonObject.put("respondent", respondent);
        jsonObject.put("commentContent", comment.getCommentContent());
        jsonObject.put("commentDate", TimeUtil.getFormatDateForSix(comment.getCommentDate()));
        return DataMap.success().setData(jsonObject);
    }

    @Override
    public DataMap updateLikeByArticleIdAndId(int articleId, int id) {
        commentMapper.updateLikeByArticleIdAndId(articleId, id);
        int likes = commentMapper.findLikesByArticleIdAndId(articleId, id);
        return DataMap.success().setData(likes);
    }

    @Override
    public DataMap findFiveNewComment(int rows, int pageNum) {

        CommonReturn commonReturn = new CommonReturn();
        JSONObject returnJson = new JSONObject();
        PageHelper.startPage(pageNum, rows);

        Example example = new Example(Comment.class);
        example.orderBy("id").desc();
        List<Comment> fiveComments = commentMapper.selectByExample(example);
        PageInfo<Comment> pageInfo = new PageInfo<>(fiveComments);

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;

        for (Comment comment : fiveComments) {
            if (comment.getpId() != 0) {
                comment.setCommentContent("@" + userService.findUsernameById(comment.getRespondentId()) + " " + comment.getCommentContent());
            }
            jsonObject = getCommentJson(comment);
            jsonArray.add(jsonObject);
        }
        returnJson.put("result", jsonArray);
        returnJson.put("pageInfo", commonReturn.jsonObjectToPageInfo(pageInfo));

        return DataMap.success().setData(returnJson);
    }

    @Override
    public DataMap getUserComment(int rows, int pageNum, String username) {

        CommonReturn commonReturn = new CommonReturn();
        int userId = userService.findIdByUsername(username);
        PageHelper.startPage(pageNum, rows);

        Example example = new Example(Comment.class);
        example.orderBy("id").desc();
        example.createCriteria().andEqualTo("respondentId", userId)
                .andNotEqualTo("answererId", userId);
        List<Comment> comments = commentMapper.selectByExample(example);
        PageInfo<Comment> pageInfo = new PageInfo<>(comments);

        JSONObject returnJson = new JSONObject();
        JSONObject commentJson;
        JSONArray commentJsonArray = new JSONArray();

        for (Comment comment : comments) {
            commentJson = getCommentJson(comment);
            commentJsonArray.add(commentJson);
        }

        returnJson.put("result", commentJsonArray);

        example.clear();
        example.createCriteria().andEqualTo("isRead", 1)
                .andEqualTo("respondentId", userId)
                .andNotEqualTo("answererId", userId);
        returnJson.put("msgIsNotReadNum", commentMapper.selectCountByExample(example));
        returnJson.put("pageInfo", commonReturn.jsonObjectToPageInfo(pageInfo));

        return DataMap.success().setData(returnJson);
    }

    @Override
    public int commentNum() {
        return commentMapper.selectCount(null);
    }

    @Override
    public int deleteCommentByArticleId(int articleId) {
        Example example = new Example(Comment.class);
        example.createCriteria().andEqualTo("articleId", articleId);
        return commentMapper.deleteByExample(example);
    }

    @Override
    public DataMap readOneCommentRecord(int id) {
        try {
            commentMapper.readCommentRecordById(id);
            return DataMap.success(CodeType.COMMENT_READ_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return DataMap.fail(CodeType.READ_MESSAGE_FAIL);
        }
    }

    @Override
    public DataMap readAllComment(String username) {
        int respondentId = userService.findIdByUsername(username);
        commentMapper.readCommentRecordByRespondentId(respondentId);
        return DataMap.success(CodeType.COMMENT_READ_ALL_SUCCESS);
    }

    @Override
    public DataMap findAllComment(int rows, int pageNum) {

        PageHelper.startPage(pageNum, rows);
        List<Comment> comments = commentMapper.findAllComment();
        PageInfo<Comment> pageInfo = new PageInfo<>(comments);

        JSONObject jsonObject;
        JSONArray resultJsonArray = new JSONArray();
        JSONObject resultJsonObject = new JSONObject();
        CommonReturn commonReturn = new CommonReturn();

        for (Comment comment : comments) {
            jsonObject = getAllCommentJson(comment);
            resultJsonArray.add(jsonObject);
        }

        resultJsonObject.put("result", resultJsonArray);
        resultJsonObject.put("pageInfo", commonReturn.jsonObjectToPageInfo(pageInfo));
        return DataMap.success().setData(resultJsonObject);
    }

    @Override
    public DataMap deleteOneCommentById(int id) {
        // 删除评论
        commentMapper.deleteOneCommentById(id);
        // 删除评论的回复
        commentMapper.deleteOneCommentBypId(id);
        // 删除评论的点赞
        commentLikesRecordMapper.deleteCommentLikesRecordBypId(id);

        return DataMap.success();
    }

    /**
     * @description: 保存评论成功后往redis中增加一条未读评论数
     * @author: 陈文振
     * @date: 2019/12/19
     * @param comment: 评论类
     * @return:
     */
    private void addNotReadNews(Comment comment) {
        if (comment.getRespondentId() != comment.getAnswererId()) {
            // 把 被回复者id 作为键插入到redis，判断是否有没有这个键
            boolean isExistKey = hashRedisService.hasKey(comment.getRespondentId() + StringUtil.BLANK);
            if (!isExistKey) {
                UserReadNews news = new UserReadNews(1, 1);
                hashRedisService.put(String.valueOf(comment.getRespondentId()), news, UserReadNews.class);
            } else {
                hashRedisService.hashIncrement(comment.getRespondentId() +  StringUtil.BLANK, "allNewsNum", 1);
                hashRedisService.hashIncrement(comment.getRespondentId() + StringUtil.BLANK, "commentNum" ,1);
            }
        }
    }

    /**
     * @description: 封装所有评论中的回复
     * @author: 陈文振
     * @date: 2019/12/21
     * @param reply: 评论中的回复类
     * @return:
     */
    private JSONObject getReplyCommentJson(Comment reply) {
        JSONObject replyJsonObject = new JSONObject();
        replyJsonObject.put("id", reply.getId());
        replyJsonObject.put("answerer", userService.findUsernameById(reply.getAnswererId()));
        replyJsonObject.put("commentDate", TimeUtil.getFormatDateForSix(reply.getCommentDate()));
        replyJsonObject.put("commentContent", reply.getCommentContent());
        replyJsonObject.put("respondent", userService.findUsernameById(reply.getRespondentId()));
        return replyJsonObject;
    }

    /**
     * @description: 封装评论（包括评论的回复详情）
     * @author: 陈文振
     * @date: 2019/12/21
     * @param comment: 评论类
     * @return:
     */
    private JSONObject getCommentJson(Comment comment, JSONArray replyJsonArray) {
        JSONObject commentJsonObject = new JSONObject();
        commentJsonObject.put("id", comment.getId());
        commentJsonObject.put("answerer", userService.findUsernameById(comment.getAnswererId()));
        commentJsonObject.put("commentDate", TimeUtil.getFormatDateForSix(comment.getCommentDate()));
        commentJsonObject.put("likes", comment.getLikes());
        commentJsonObject.put("commentContent", comment.getCommentContent());
        commentJsonObject.put("replies", replyJsonArray);
        commentJsonObject.put("avatarImgUrl", userService.getHeadPortraitUrlByUserId(comment.getAnswererId()));
        return commentJsonObject;
    }

    /**
     * @description: 封装评论
     * @author: 陈文振
     * @date: 2019/12/21
     * @param comment: 评论类
     * @return:
     */
    private JSONObject getCommentJson(Comment comment) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", comment.getId());
        jsonObject.put("pId", comment.getpId());
        jsonObject.put("articleId", comment.getArticleId());
        jsonObject.put("answerer", userService.findUsernameById(comment.getAnswererId()));
        jsonObject.put("commentDate", TimeUtil.getFormatDateForSix(comment.getCommentDate()));
        jsonObject.put("articleTitle", articleService.findArticleTitleByArticleId(comment.getArticleId()).get("articleTitle"));
        jsonObject.put("commentContent", comment.getCommentContent());
        jsonObject.put("isRead", comment.getIsRead());
        return jsonObject;
    }

    /**
     * @description: 封装评论（管理员专用）
     * @author: 陈文振
     * @date: 2020/1/12
     * @param comment
     * @return: com.alibaba.fastjson.JSONObject
     */
    private JSONObject getAllCommentJson(Comment comment) {
        JSONObject jsonObject = getCommentJson(comment);
        jsonObject.put("respondent", userService.findUsernameById(comment.getRespondentId()));

        // 被评论对象
        Integer pId = comment.getpId();
        if(pId == 0) {
            jsonObject.put("ParentObject", "这是根评论哦");
        } else {
            jsonObject.put("ParentObject", commentMapper.findOneCommentById(comment.getpId()).getCommentContent());
        }
        return jsonObject;
    }
}
