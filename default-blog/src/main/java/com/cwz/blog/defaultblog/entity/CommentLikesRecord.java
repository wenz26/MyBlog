package com.cwz.blog.defaultblog.entity;

import javax.persistence.Table;

/**
 * @author: 陈文振
 * @date: 2019/12/2
 * @description: 文章评论点赞记录
 */
@Table(name = "comment_likes_record")
public class CommentLikesRecord {

    private Integer id;

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 评论的id
     */
    private Long pId;

    /**
     * 点赞人
     */
    private Integer likerId;

    /**
     * 点赞时间
     */
    private String likeDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Long getpId() {
        return pId;
    }

    public void setpId(Long pId) {
        this.pId = pId;
    }

    public Integer getLikerId() {
        return likerId;
    }

    public void setLikerId(Integer likerId) {
        this.likerId = likerId;
    }

    public String getLikeDate() {
        return likeDate;
    }

    public void setLikeDate(String likeDate) {
        this.likeDate = likeDate == null ? null : likeDate.trim();
    }
}
