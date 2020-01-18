package com.cwz.blog.defaultblog.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author: 陈文振
 * @date: 2019/12/2
 * @description: 文章评论
 */
@Table(name = "comment_record")
public class Comment {

    /**
     * 评论id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 被评论的文章id
     */
    private Integer articleId;

    /**
     * 回复的父id 若是评论则为 0，是评论中的回复则为对应评论的id
     */
    private Integer pId = 0;

    /**
     * 评论者id
     */
    private Integer answererId;

    /**
     * 被回复者id
     */
    private Integer respondentId;

    /**
     * 评论日期
     */
    private LocalDateTime commentDate;

    /**
     * 评论点赞数
     */
    private Integer likes = 0;

    /**
     * 评论内容
     */
    private String commentContent;

    /**
     * 该条评论是否已读  1--未读   0--已读
     */
    private Integer isRead = 1;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Integer getpId() {
        return pId;
    }

    public void setpId(Integer pId) {
        this.pId = pId;
    }

    public Integer getAnswererId() {
        return answererId;
    }

    public void setAnswererId(Integer answererId) {
        this.answererId = answererId;
    }

    public Integer getRespondentId() {
        return respondentId;
    }

    public void setRespondentId(Integer respondentId) {
        this.respondentId = respondentId;
    }

    public LocalDateTime getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(LocalDateTime commentDate) {
        this.commentDate = commentDate;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }
}
