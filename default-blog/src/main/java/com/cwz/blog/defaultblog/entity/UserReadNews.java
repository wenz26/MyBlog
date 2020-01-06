package com.cwz.blog.defaultblog.entity;

/**
 * @author: 陈文振
 * @date: 2019/12/2
 * @description: 用户评论留言未读数
 */
public class UserReadNews {

    /**
     * 留言+评论未读数
     */
    private Integer allNewsNum;

    /**
     * 评论未读数
     */
    private Integer commentNum;

    public UserReadNews(Integer allNewsNum, Integer commentNum) {
        this.allNewsNum = allNewsNum;
        this.commentNum = commentNum;
    }

    public Integer getAllNewsNum() {
        return allNewsNum;
    }

    public void setAllNewsNum(Integer allNewsNum) {
        this.allNewsNum = allNewsNum;
    }

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }
}
