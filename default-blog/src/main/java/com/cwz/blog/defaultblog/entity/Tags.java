package com.cwz.blog.defaultblog.entity;

import javax.persistence.Table;

/**
 * @author: 陈文振
 * @date: 2019/12/2
 * @description: 标签
 */
@Table(name = "tags")
public class Tags {

    private Integer id;

    /**
     * 标签名
     */
    private String tagName;

    /**
     * 标签大小（相当于该标签里有多少篇文章）
     */
    private Integer tagSize;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName == null ? null : tagName.trim();
    }

    public Integer getTagSize() {
        return tagSize;
    }

    public void setTagSize(Integer tagSize) {
        this.tagSize = tagSize;
    }
}
