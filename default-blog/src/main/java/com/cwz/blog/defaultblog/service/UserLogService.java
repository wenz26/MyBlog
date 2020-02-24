package com.cwz.blog.defaultblog.service;

import com.cwz.blog.defaultblog.entity.UserLog;
import com.cwz.blog.defaultblog.utils.DataMap;

/**
 * @author: 陈文振
 * @date: 2019/12/27
 * @description: 用户日志业务操作
 */
public interface UserLogService {

    /**
     * @description: 插入 用户日志 记录
     * @author: 陈文振
     * @date: 2019/12/27
     * @param userLog
     * @return: int
     */
    int saveUserLog(UserLog userLog);

    /**
     * @description: 分页获得所有的 日志记录信息（可按条件查询）
     * @author: 陈文振
     * @date: 2019/12/27
     * @param rows
     * @param pageNum
     * @param type : 1 为用户登录日志, 2为用户操作日志, 3为自定义日志查询
     * @return: com.cwz.blog.defaultblog.utils.DataMap
     */
    DataMap getAllUserLog(int rows, int pageNum, UserLog userLogExample, int type, String firstDate, String lastDate);

    /**
     * @description:
     * @author: 陈文振
     * @date: 2020/1/31
     * @param id
     * @return: com.cwz.blog.defaultblog.utils.DataMap
     */
    DataMap getUserLogOneById(int id);
}
