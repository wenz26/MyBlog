package com.cwz.blog.defaultblog.service;

import com.cwz.blog.defaultblog.entity.CommentLikesRecord;
import com.cwz.blog.defaultblog.utils.DataMap;

/**
 * @author: 陈文振
 * @date: 2019/12/19
 * @description: 评论点赞记录业务操作
 */
public interface CommentLikesRecordService {

    /**
     * @description:  评定评论用户是否已经点赞过
     * @author: 陈文振
     * @date: 2019/12/21
     * @param articleId: 文章id
     * @param pId: 评论id
     * @param username: 用户名
     * @return:
     */
    boolean isLike(int articleId, int pId, String username);

    /**
     * @description: 保存评论中点赞的记录
     * @author: 陈文振
     * @date: 2019/12/21
     * @param commentLikesRecord: 评论点赞类
     * @return:
     */
    void insertCommentLikesRecord(CommentLikesRecord commentLikesRecord);

    /**
     * @description: 通过文章id删除该文章的所有点赞记录
     * @author: 陈文振
     * @date: 2019/12/21
     * @param articleId: 文章Id
     * @return:
     */
    void deleteCommentLikesRecordByArticleId(int articleId);

    /**
     * @description: 获得该用户的所有评论点赞信息
     * @author: 陈文振
     * @date: 2019/12/9
     * @param rows: 一页显示文章数
     * @param pageNum: 第几页
     * @param username: 用户名
     * @return:
     */
    DataMap getCommentThumbsUp(int rows, int pageNum, String username, Integer isRead, String firstDate, String lastDate);

    /**
     * @description: 已读一条点赞记录
     * @author: 陈文振
     * @date: 2019/12/9
     * @param id:
     * @return:
     */
    DataMap readThisThumbsUp(int id, String username);

    /**
     * @description: 已读所有点赞记录
     * @author: 陈文振
     * @date: 2019/12/9
     * @return:
     */
    DataMap readAllThumbsUp(String username);
}
