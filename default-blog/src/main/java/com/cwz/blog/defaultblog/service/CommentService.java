package com.cwz.blog.defaultblog.service;

import com.cwz.blog.defaultblog.entity.Comment;
import com.cwz.blog.defaultblog.utils.DataMap;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: 陈文振
 * @date: 2019/12/19
 * @description: 评论业务操作
 */
public interface CommentService {

    /**
     * @description: 保存留言
     * @author: 陈文振
     * @date: 2019/12/19
     * @param comment: 留言
     * @return:
     */
    @Transactional
    void insertComment(Comment comment);

    /**
     * @description: 通过文章id获得文章所有评论和回复
     * @author: 陈文振
     * @date: 2019/12/19
     * @param articleId: 文章id
     * @return:
     */
    DataMap findCommentByArticleId(int articleId, String username);

    /**
     * @description: 返回评论中的回复
     * @author: 陈文振
     * @date: 2019/12/19
     * @param comment: 评论类
     * @param answerer: 评论者姓名
     * @param respondent: 被回复者姓名
     * @return:
     */
    DataMap replyReplyReturn(Comment comment, String answerer, String respondent);

    /**
     * @description: 更新评论点赞数
     * @author: 陈文振
     * @date: 2019/12/19
     * @param articleId: 文章id
     * @param id: 该评论的id
     * @return:
     */
    DataMap updateLikeByArticleIdAndId(int articleId, int id);

    /**
     * @description: 获得最新的5条评论
     * @author: 陈文振
     * @date: 2019/12/19
     * @return:
     */
    DataMap findFiveNewComment(int rows, int pageNum);

    /**
     * @description: 分页获得用户所有的被回复的评论
     * @author: 陈文振
     * @date: 2019/12/19
     * @param rows: 一页大小
     * @param pageNum: 当前页
     * @param username: 用户
     * @return:
     */
    DataMap getUserComment(int rows, int pageNum, String username);

    /**
     * @description: 获得评论总数
     * @author: 陈文振
     * @date: 2019/12/19
     * @return:
     */
    int commentNum();

    /**
     * @description: 通过文章id删除该文章的所有评论
     * @author: 陈文振
     * @date: 2019/12/19
     * @param articleId: 文章id
     * @return:
     */
    int deleteCommentByArticleId(int articleId);

    /**
     * @description: 已读一条评论
     * @author: 陈文振
     * @date: 2019/12/19
     * @param id: 评论id
     * @return:
     */
    DataMap readOneCommentRecord(int id);

    /**
     * @description: 将该用户的所有未读消息标记为已读
     * @author: 陈文振
     * @date: 2019/12/19
     * @param username: 用户名
     * @return:
     */
    DataMap readAllComment(String username);
}