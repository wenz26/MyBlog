package com.cwz.blog.defaultblog.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author: 陈文振
 * @date: 2019/12/2
 * @description: 反馈
 */
@Table(name = "feedback")
public class FeedBack {

    /**
     * 反馈id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 反馈内容
     */
    private String feedbackContent;

    /**
     * 反馈者信息（邮箱、QQ、微信等）
     */
    private String contactInfo;

    /**
     * 反馈者Id
     */
    private Integer userId;

    /**
     * 反馈时间
     */
    private LocalDateTime feedbackDate;

    /**
     * 该条点赞是否已读  1--未读   0--已读
     */
    private Integer isRead = 1;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFeedbackContent() {
        return feedbackContent;
    }

    public void setFeedbackContent(String feedbackContent) {
        this.feedbackContent = feedbackContent;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDateTime getFeedbackDate() {
        return feedbackDate;
    }

    public void setFeedbackDate(LocalDateTime feedbackDate) {
        this.feedbackDate = feedbackDate;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }
}
