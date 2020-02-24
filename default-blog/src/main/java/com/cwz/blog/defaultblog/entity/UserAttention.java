package com.cwz.blog.defaultblog.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author: 陈文振
 * @date: 2020/2/5
 * @description: 用户关注实体类
 */
@Table(name = "user_attention")
public class UserAttention {

    /**
     * 关注id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 当前用户id
     */
    private Integer userId;

    /**
     * 被关注用户id
     */
    private Integer attentionUserId;

    /**
     * 关注日期
     */
    private LocalDateTime attentionDate;

    /**
     * 用户详情
     */
    private User user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAttentionUserId() {
        return attentionUserId;
    }

    public void setAttentionUserId(Integer attentionUserId) {
        this.attentionUserId = attentionUserId;
    }

    public LocalDateTime getAttentionDate() {
        return attentionDate;
    }

    public void setAttentionDate(LocalDateTime attentionDate) {
        this.attentionDate = attentionDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
