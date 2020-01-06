package com.cwz.blog.defaultblog.service;

import com.cwz.blog.defaultblog.entity.CommentLikesRecord;

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
}
