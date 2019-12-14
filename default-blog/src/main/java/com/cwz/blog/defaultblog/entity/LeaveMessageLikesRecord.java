package com.cwz.blog.defaultblog.entity;

import javax.persistence.Table;

/**
 * @author: 陈文振
 * @date: 2019/12/2
 * @description: 留言中点赞
 */
@Table(name = "leave_message_likes_record")
public class LeaveMessageLikesRecord {

    private Integer id;

    /**
     * 文章页
     */
    private String pageName;

    /**
     * 留言id
     */
    private Integer pId;

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

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName == null ? null : pageName.trim();
    }

    public Integer getpId() {
        return pId;
    }

    public void setpId(Integer pId) {
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
