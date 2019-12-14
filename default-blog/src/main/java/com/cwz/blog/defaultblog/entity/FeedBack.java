package com.cwz.blog.defaultblog.entity;

import javax.persistence.Table;

/**
 * @author: 陈文振
 * @date: 2019/12/2
 * @description: 反馈
 */
@Table(name = "feedback")
public class FeedBack {

    private Integer id;

    /**
     * 联系方式
     */
    private String contactInfo;

    /**
     * 反馈人
     */
    private Integer personId;

    /**
     * 反馈时间
     */
    private String feedbackDate;

    /**
     * 反馈内容
     */
    private String feedbackContent;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo == null ? null : contactInfo.trim();
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getFeedbackDate() {
        return feedbackDate;
    }

    public void setFeedbackDate(String feedbackDate) {
        this.feedbackDate = feedbackDate == null ? null : feedbackDate.trim();
    }

    public String getFeedbackContent() {
        return feedbackContent;
    }

    public void setFeedbackContent(String feedbackContent) {
        this.feedbackContent = feedbackContent == null ? null : feedbackContent.trim();
    }
}
