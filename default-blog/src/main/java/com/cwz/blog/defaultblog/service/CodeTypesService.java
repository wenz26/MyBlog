package com.cwz.blog.defaultblog.service;

import com.cwz.blog.defaultblog.utils.DataMap;

/**
 * @author: 陈文振
 * @date: 2020/2/1
 * @description: 返回状态码类业务操作
 */
public interface CodeTypesService {

    /**
     * @description: 返回查询的具体某些状态
     * @author: 陈文振
     * @date: 2020/2/1
     * @param code
     * @return: java.util.List<com.cwz.blog.defaultblog.entity.CodeTypes>
     */
    DataMap selectSomeCodeTypes(int rows, int pageNum, String code);
}
