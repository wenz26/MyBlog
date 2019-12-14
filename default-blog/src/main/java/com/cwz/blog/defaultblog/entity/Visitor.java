package com.cwz.blog.defaultblog.entity;

import javax.persistence.Table;

/**
 * @author: 陈文振
 * @date: 2019/12/2
 * @description: 访客
 */
@Table(name = "visitor")
public class Visitor {

    private Integer id;

    /**
     * 访客人数
     */
    private Long visitorNum;

    /**
     * 当前页的name or 文章名
     */
    private String pageName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getVisitorNum() {
        return visitorNum;
    }

    public void setVisitorNum(Long visitorNum) {
        this.visitorNum = visitorNum;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName == null ? null : pageName.trim();
    }
}
