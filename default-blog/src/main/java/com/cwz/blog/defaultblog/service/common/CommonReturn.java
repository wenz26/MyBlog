package com.cwz.blog.defaultblog.service.common;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: 陈文振
 * @date: 2019/12/18
 * @description: 封装共同的返回数据
 */
public class CommonReturn {

    /**
     * @description: 对分页信息的封装(JSONObject)
     * @author: 陈文振
     * @date: 2019/12/7
     * @param pageInfo: 分页信息
     * @return:
     */
    public JSONObject jsonObjectToPageInfo(PageInfo pageInfo){
        JSONObject pageJson = new JSONObject();
        // 当前页
        pageJson.put("pageNum", pageInfo.getPageNum());
        // 每页的数量
        pageJson.put("pageSize", pageInfo.getPageSize());
        // 总记录数
        pageJson.put("total", pageInfo.getTotal());
        // 总页数
        pageJson.put("pages", pageInfo.getPages());
        // 是否为第一页
        pageJson.put("isFirstPage", pageInfo.isIsFirstPage());
        // 是否为最后一页
        pageJson.put("isLastPage", pageInfo.isIsLastPage());
        return pageJson;
    }

    /**
     * @description: 对分页信息的封装(Map<String, Object>)
     * @author: 陈文振
     * @date: 2019/12/25
     * @param pageInfo
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    public Map<String, Object> mapToPageInfo(PageInfo pageInfo){
        Map<String, Object> thisPageInfo = new ConcurrentHashMap<>();
        // 当前页
        thisPageInfo.put("pageNum", pageInfo.getPageNum());
        // 每页的数量
        thisPageInfo.put("pageSize", pageInfo.getPageSize());
        // 总记录数
        thisPageInfo.put("total", pageInfo.getTotal());
        // 总页数
        thisPageInfo.put("pages", pageInfo.getPages());
        // 是否为第一页
        thisPageInfo.put("isFirstPage", pageInfo.isIsFirstPage());
        // 是否为最后一页
        thisPageInfo.put("isLastPage", pageInfo.isIsLastPage());
        return thisPageInfo;
    }
}
