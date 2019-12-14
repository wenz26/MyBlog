package com.cwz.blog.defaultblog.entity;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author: 陈文振
 * @date: 2019/12/2
 * @description: 文章归档
 */
@Table(name = "archives")
public class Archives {

    private Integer id;

    /**
     * 归档日期
     */
    private String archiveName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getArchiveName() {
        return archiveName;
    }

    public void setArchiveName(String archiveName) {
        this.archiveName = archiveName == null ? null : archiveName.trim();
    }
}
