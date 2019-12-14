package com.cwz.blog.defaultblog.entity;

import javax.persistence.Table;

/**
 * @author: 陈文振
 * @date: 2019/12/2
 * @description: 留言
 */
@Table(name = "leave_message_record")
public class LeaveMessage {

    private Integer id;

    /**
     * 留言页
     */
    private String pageName;

    /**
     * 留言的父id 若是留言则为 0，则是留言中的回复则为对应留言的id
     */
    private Integer pId = 0;

    /**
     * 留言者
     */
    private Integer answererId;

    /**
     * 被回复者
     */
    private Integer respondentId;

    /**
     * 留言日期
     */
    private String leaveMessageDate;

    /**
     * 喜欢数
     */
    private Integer likes = 0;

    /**
     * 该条留言是否已读  1--未读   0--已读
     */
    private Integer isRead = 1;

    /**
     * 留言内容
     */
    private String leaveMessageContent;

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

    public String getLeaveMessageDate() {
        return leaveMessageDate;
    }

    public void setLeaveMessageDate(String leaveMessageDate) {
        this.leaveMessageDate = leaveMessageDate == null ? null : leaveMessageDate.trim();
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public String getLeaveMessageContent() {
        return leaveMessageContent;
    }

    public void setLeaveMessageContent(String leaveMessageContent) {
        this.leaveMessageContent = leaveMessageContent == null ? null : leaveMessageContent.trim();
    }
}
