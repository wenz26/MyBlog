package com.cwz.blog.defaultblog.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.constant.CodeType;
import com.cwz.blog.defaultblog.entity.Comment;
import com.cwz.blog.defaultblog.entity.User;
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
import org.apache.commons.lang3.StringUtils;
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
        jsonObject.put("avatarImgUrl", userService.findUserByUsername(answerer).getAvatarImgUrl());
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
            String articleTitle = articleService.findArticleTitleByArticleId(comment.getArticleId()).get("articleTitle");
            jsonObject = getCommentJson(comment, articleTitle);
            jsonArray.add(jsonObject);
        }
        returnJson.put("result", jsonArray);
        returnJson.put("pageInfo", commonReturn.jsonObjectToPageInfo(pageInfo));

        return DataMap.success().setData(returnJson);
    }

    @Override
    public DataMap getUserComment(int rows, int pageNum, String username, Integer isRead, String firstDate, String lastDate) {

        CommonReturn commonReturn = new CommonReturn();
        int userId = userService.findIdByUsername(username);

        PageHelper.startPage(pageNum, rows);

        List<Comment> comments = commentMapper.getUserComment(userId, userId, isRead, firstDate, lastDate);
        PageInfo<Comment> pageInfo = new PageInfo<>(comments);

        JSONObject returnJson = new JSONObject();
        JSONObject commentJson;
        JSONArray commentJsonArray = new JSONArray();

        for (Comment comment : comments) {
            String articleTitle = comment.getArticle().getArticleTitle();
            commentJson = getCommentJson(comment, articleTitle);
            commentJson.put("respondent", userService.findUsernameById(comment.getRespondentId()));

            if (comment.getpId() != 0) {
                Comment commentToParent = commentMapper.findCommentContentAndAnswererIdByPId(comment.getpId());
                commentJson.put("pRespondent", userService.findUsernameById(commentToParent.getAnswererId()));
                commentJson.put("pComment", commentToParent.getCommentContent());
            }

            commentJsonArray.add(commentJson);
        }

        returnJson.put("result", commentJsonArray);

        returnJson.put("msgIsNotReadNum", commentMapper.countUserCommentNotRead(userId, userId));
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
    public DataMap findAllComment(int rows, int pageNum, String username, String articleTitle,
                                  String commentContent, String firstDate, String lastDate, String searchUsername) {

        List<Comment> comments;
        int total;

        if (StringUtils.isBlank(username)) {
            PageHelper.startPage(pageNum, rows);
            comments = commentMapper.findAllCommentBySome(articleTitle, null, commentContent, firstDate, lastDate, searchUsername);
            total = commentMapper.countAllCommentBySome(articleTitle, null, commentContent, firstDate, lastDate, searchUsername);
        } else {
            int userId = userService.findIdByUsername(username);
            PageHelper.startPage(pageNum, rows);
            comments = commentMapper.findAllCommentBySome(articleTitle, userId, commentContent, firstDate, lastDate, searchUsername);
            total = commentMapper.countAllCommentBySome(articleTitle, userId, commentContent, firstDate, lastDate, searchUsername);
        }

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
        resultJsonObject.put("total", total);
        resultJsonObject.put("pageInfo", commonReturn.jsonObjectToPageInfo(pageInfo));
        return DataMap.success().setData(resultJsonObject);
    }

    @Override
    public DataMap deleteOneCommentById(int id) {
        Comment comment = commentMapper.findCommentById(id);

        int commentNum = 0;
        int commentLikesNum = 0;
        int answererId = comment.getAnswererId();

        if (comment.getpId() == 0) {
            commentNum = commentMapper.countCommentByPId(id, answererId); // 我自己发的评论，回复本人的未读数
            commentLikesNum = commentMapper.countCommentLikesByPId(id, answererId); // 我自己发的评论，点赞数未读数

            List<Integer> otherCommentRespondentIds = commentMapper.findOtherCommentRespondentId(id, answererId); // 我自己发的评论，回复其他人的人数
            for (Integer otherRId : otherCommentRespondentIds) {
                int otherCommentNum = commentMapper.countCommentByPId(id, otherRId); // 我自己发的评论，回复其他人的未读数

                if (hashRedisService.hasKey(String.valueOf(otherRId))) {
                    hashRedisService.hashIncrement(String.valueOf(otherRId), "allNewsNum", -otherCommentNum);
                    hashRedisService.hashIncrement(String.valueOf(otherRId), "commentNum", -otherCommentNum);
                }
                System.out.println(otherRId + " 要-" + otherCommentNum + "的评论未读数");
            }
        } else {
            if (comment.getIsRead() == 1) {
                if (hashRedisService.hasKey(String.valueOf(comment.getRespondentId()))) {
                    hashRedisService.hashIncrement(String.valueOf(comment.getRespondentId()), "allNewsNum", -1);
                    hashRedisService.hashIncrement(String.valueOf(comment.getRespondentId()), "commentNum", -1);
                }
                System.out.println(comment.getRespondentId() + " 要-1的评论未读数");
            } else {
                System.out.println(comment.getRespondentId() + " 要-0的评论未读数");
            }
        }

        // 删除评论
        commentMapper.deleteOneCommentById(id);
        // 删除评论的回复
        commentMapper.deleteOneCommentBypId(id);
        // 删除评论的点赞
        commentLikesRecordMapper.deleteCommentLikesRecordBypId(id);

        if (hashRedisService.hasKey(String.valueOf(answererId))) {
            hashRedisService.hashIncrement(String.valueOf(answererId), "allNewsNum", -commentNum);
            hashRedisService.hashIncrement(String.valueOf(answererId), "commentNum", -commentNum);
            System.out.println("回复我的未读数：" + commentNum);
        }

        if (hashRedisService.hasHashKey(StringUtil.COMMENT_THUMBS_UP, String.valueOf(answererId))) {
            hashRedisService.hashIncrement(StringUtil.COMMENT_THUMBS_UP, String.valueOf(answererId), -commentLikesNum);
            System.out.println("评论点赞的未读数：" + commentLikesNum);
        }

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
        User user = userService.findUserByUserId(reply.getAnswererId());
        replyJsonObject.put("id", reply.getId());
        replyJsonObject.put("answerer", user.getUsername());
        replyJsonObject.put("avatarImgUrl", user.getAvatarImgUrl());
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
    private JSONObject getCommentJson(Comment comment, String articleTitle) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id", comment.getId());
        jsonObject.put("pId", comment.getpId());
        jsonObject.put("articleId", comment.getArticleId());
        jsonObject.put("answerer", userService.findUsernameById(comment.getAnswererId()));
        jsonObject.put("commentDate", TimeUtil.getFormatDateForSix(comment.getCommentDate()));
        jsonObject.put("articleTitle", articleTitle);
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
        //String articleTitle = articleService.findArticleTitleByArticleId(comment.getArticleId()).get("articleTitle");
        JSONObject jsonObject = getCommentJson(comment, comment.getArticle().getArticleTitle());

        String respondent = userService.findUsernameById(comment.getRespondentId());
        // 被评论对象
        if(comment.getpId() == 0) {
            jsonObject.put("parentObject", comment.getArticle().getArticleTitle());
        } else {
            Comment oneCommentById = commentMapper.findOneCommentById(comment.getpId());
            jsonObject.put("parentObject", userService.findUsernameById(oneCommentById.getAnswererId()) + "：" + oneCommentById.getCommentContent());
        }

        jsonObject.put("respondent", respondent);
        return jsonObject;
    }
}
