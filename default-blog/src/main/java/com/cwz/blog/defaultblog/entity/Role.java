package com.cwz.blog.defaultblog.entity;

import javax.persistence.Table;

/**
 * @author: 陈文振
 * @date: 2019/12/2
 * @description: 权限
 */
@Table(name = "role")
public class Role {

    private Integer id;

    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}
